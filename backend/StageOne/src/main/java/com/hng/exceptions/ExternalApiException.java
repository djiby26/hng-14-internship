package com.hng.exceptions;


public class ExternalApiException extends RuntimeException {

    private final String apiName;

    public ExternalApiException(String apiName) {
        super(apiName + " returned an invalid response");
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }
}