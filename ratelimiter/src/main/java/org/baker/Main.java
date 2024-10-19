package org.baker;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.baker.limiter.TokenBucketLimiter;
import org.baker.model.Request;
import org.baker.model.RequestMethod;

public class Main {

  public static void main(String[] args) {
    var globalBucketLimit = args.length > 0
        ? Integer.parseInt(args[0])
        : 500;
    System.out.println("Token fill algorithm with limit: " + globalBucketLimit);
    var tokenBucketLimiter = new TokenBucketLimiter(globalBucketLimit);

    // Create a ScheduledExecutorService with a single thread
    try (var scheduler = Executors.newScheduledThreadPool(1)) {
      // refill buckets every minute
      scheduler.scheduleAtFixedRate(tokenBucketLimiter::refillBuckets, 0, 1, TimeUnit.SECONDS);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("Shutting down scheduler...");
        scheduler.shutdown();
      }));

      Thread.sleep(2000);

      IntStream.range(0, 5).forEach(i -> runRequestAndPrint(tokenBucketLimiter, i));

      Thread.sleep(2000);
      IntStream.range(0, 3).forEach(i -> runRequestAndPrint(tokenBucketLimiter, i));

      Thread.sleep(3000);
      IntStream.range(0, 5).forEach(i -> runRequestAndPrint(tokenBucketLimiter, i));
    } catch (InterruptedException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private static void runRequestAndPrint(TokenBucketLimiter tokenBucketLimiter, Integer reqNumber) {
    var response = tokenBucketLimiter.tryRequest(new Request(
        reqNumber,
        RequestMethod.GET,
        "getFood",
        12,
        "1.3.4.5"));
    System.out.println(response.getResponseCode());
  }
}