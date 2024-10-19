package org.baker.limiter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.baker.model.Request;
import org.baker.model.Response;

public class TokenBucketLimiter implements Limiter {
  private final List<BucketConfig> bucketConfigs;

  private record BucketConfig(
      Integer maxTokensPerBucket,
      Integer tokensRefilledPerSecond,
      Function<Request, String> keyResolver,
      Map<String, Integer> buckets) {

    public void printBucket() {
      System.out.println("BUCKET!");
      System.out.printf("maxTokensPerBucket: %d, tokensRefilledPerSecond:%d%n", maxTokensPerBucket, tokensRefilledPerSecond);
      System.out.println(buckets);
    }
  }

  public TokenBucketLimiter(Integer globalBucketLimit) {
    var globalBucketMap = new HashMap<String, Integer>();
    globalBucketMap.put("", globalBucketLimit);
    bucketConfigs = List.of(
        // global bucket
        new BucketConfig(
            globalBucketLimit,
            1,
            request -> "",
            globalBucketMap
        ),
        // ip bucket
        new BucketConfig(
            30,
            1,
            Request::getIpAddress,
            new HashMap<>()
        ),
        // user bucket
        new BucketConfig(
            10,
            1,
            request -> request.getUserId().toString(),
            new HashMap<>()
        ),
        // operation bucket
        // TODO: allow custom bucketing per operation method/name
        new BucketConfig(
            50,
            1,
            request -> "%s#%s".formatted(request.getRequestMethod(), request.getOperationName()),
            new HashMap<>()
        )
    );
  }

  @Override
  public Response tryRequest(Request request) {
    var isRequestValid = this.bucketConfigs.stream()
        .allMatch(bucketConfig ->
            bucketConfig.buckets.getOrDefault(
                bucketConfig.keyResolver.apply(request),
                bucketConfig.maxTokensPerBucket) > 0
        );

    if (!isRequestValid) {
      return new Response(request.getRequestId(), Response.RESPONSE_CODE_LIMITED);
    } else {
      this.consumeFromBuckets(request);
      return new Response(request.getRequestId(), Response.RESPONSE_CODE_SUCCESS);
    }
  }

  // TODO: maybe write to a log and recompute bucket status off a log value
  private void consumeFromBuckets(Request request) {
    this.bucketConfigs.forEach(bucketConfig -> {
      // get bucket key
      var bucketKey = bucketConfig.keyResolver.apply(request);
      bucketConfig.buckets.put(
          bucketConfig.keyResolver.apply(request),
          Math.max(0,
              bucketConfig.buckets.getOrDefault(bucketKey,
                  bucketConfig.maxTokensPerBucket) - 1));
    });
  }

  // TODO: this is super not thread safe
  public void refillBuckets() {
    System.out.println("Refilling buckets!");
    this.bucketConfigs.forEach(bucketConfig -> {
      bucketConfig.buckets.keySet()
          .forEach(key ->
              bucketConfig.buckets.put(
                  key,
                  Math.min(bucketConfig.maxTokensPerBucket,
                      bucketConfig.buckets.get(key) + bucketConfig.tokensRefilledPerSecond))
          );
      //bucketConfig.printBucket();
    });
    //System.out.println();
  }

}
