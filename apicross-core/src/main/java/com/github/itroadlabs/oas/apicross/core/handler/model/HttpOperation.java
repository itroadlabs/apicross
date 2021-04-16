package com.github.itroadlabs.oas.apicross.core.handler.model;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import lombok.NonNull;

import java.util.Objects;

public class HttpOperation {
    private final String uriPath;
    private final PathItem.HttpMethod httpMethod;
    private final Operation operation;

    public HttpOperation(@NonNull String uriPath, @NonNull PathItem.HttpMethod httpMethod, @NonNull Operation operation) {
        this.uriPath = uriPath;
        this.httpMethod = httpMethod;
        this.operation = operation;
    }

    public String getUriPath() {
        return uriPath;
    }

    public PathItem.HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpOperation that = (HttpOperation) o;
        return uriPath.equals(that.uriPath) &&
                httpMethod.equals(that.httpMethod) &&
                operation.getOperationId().equals(that.operation.getOperationId()); // operationId must be unique within API spec!
    }

    @Override
    public int hashCode() {
        return Objects.hash(uriPath, httpMethod, operation.getOperationId());
    }

    @Override
    public String toString() {
        return "HttpOperation{" +
                "httpMethod='" + httpMethod + '\'' +
                ", uriPath='" + uriPath + '\'' +
                ", operation=" + operation.getOperationId() +
                '}';
    }
}
