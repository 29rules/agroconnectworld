package com.ai.company.testing;

import java.util.Map;

public class APITestCase {
    private String testId;
    private String endpoint;
    private String method;
    private Map<String, String> headers;
    private String requestBody;
    private int expectedStatusCode;
    private String expectedResponse;
    private String testType; // POSITIVE, NEGATIVE, EDGE_CASE
    
    // Getters and Setters
    public String getTestId() { return testId; }
    public void setTestId(String testId) { this.testId = testId; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    
    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    
    public int getExpectedStatusCode() { return expectedStatusCode; }
    public void setExpectedStatusCode(int expectedStatusCode) { this.expectedStatusCode = expectedStatusCode; }
    
    public String getExpectedResponse() { return expectedResponse; }
    public void setExpectedResponse(String expectedResponse) { this.expectedResponse = expectedResponse; }
    
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
}



