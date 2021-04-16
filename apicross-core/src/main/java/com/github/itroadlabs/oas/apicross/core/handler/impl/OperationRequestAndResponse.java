package com.github.itroadlabs.oas.apicross.core.handler.impl;

import io.swagger.v3.oas.models.media.Schema;

/**
 * API Operation's request and response bodies metadata
 */
class OperationRequestAndResponse {
    private String requestMediaType;
    private Schema<?> requestContentSchema;
    private boolean requestBodyRequired;
    private String requestDescription;

    private String responseMediaType;
    private Schema<?> responseContentSchema;
    private String responseDescription;

    public String getRequestMediaType() {
        return requestMediaType;
    }

    public OperationRequestAndResponse setRequestMediaType(String requestMediaType) {
        this.requestMediaType = requestMediaType;
        return this;
    }

    public Schema<?> getRequestContentSchema() {
        return requestContentSchema;
    }

    public OperationRequestAndResponse setRequestContentSchema(Schema<?> requestContentSchema) {
        this.requestContentSchema = requestContentSchema;
        return this;
    }

    public boolean isRequestBodyRequired() {
        return requestBodyRequired;
    }

    public OperationRequestAndResponse setRequestBodyRequired(boolean requestBodyRequired) {
        this.requestBodyRequired = requestBodyRequired;
        return this;
    }

    public String getResponseMediaType() {
        return responseMediaType;
    }

    public OperationRequestAndResponse setResponseMediaType(String responseMediaType) {
        this.responseMediaType = responseMediaType;
        return this;
    }

    public Schema<?> getResponseContentSchema() {
        return responseContentSchema;
    }

    public OperationRequestAndResponse setResponseContentSchema(Schema<?> responseContentSchema) {
        this.responseContentSchema = responseContentSchema;
        return this;
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public OperationRequestAndResponse setRequestDescription(String requestDescription) {
        this.requestDescription = requestDescription;
        return this;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public OperationRequestAndResponse setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
        return this;
    }
}
