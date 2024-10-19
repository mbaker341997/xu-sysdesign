package org.baker.model;

public class Response {
  private Integer requestId;
  private Integer responseCode;

  public static final Integer RESPONSE_CODE_SUCCESS = 200;
  public static final Integer RESPONSE_CODE_LIMITED = 429;

  public Response(Integer requestId, Integer responseCode) {
    this.requestId = requestId;
    this.responseCode = responseCode;
  }

  public Integer getRequestId() {
    return requestId;
  }

  public void setRequestId(Integer requestId) {
    this.requestId = requestId;
  }

  public Integer getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(Integer responseCode) {
    this.responseCode = responseCode;
  }
}
