package org.baker.model;

public class Request {
  private Integer requestId;
  private RequestMethod requestMethod;
  private String operationName;
  private Integer userId;
  // TODO: some better representation but this is just a side project anyways
  private String ipAddress;

  public Request(
      Integer requestId,
      RequestMethod requestMethod,
      String operationName,
      Integer userId,
      String ipAddress
  ) {
    this.requestId = requestId;
    this.requestMethod = requestMethod;
    this.operationName = operationName;
    this.userId = userId;
    this.ipAddress = ipAddress;
  }

  public Integer getRequestId() {
    return requestId;
  }

  public void setRequestId(Integer requestId) {
    this.requestId = requestId;
  }

  public RequestMethod getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(RequestMethod requestMethod) {
    this.requestMethod = requestMethod;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }
}
