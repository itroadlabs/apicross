package com.github.itroadlabs.oas.apicross.core.handler.model;

import com.github.itroadlabs.oas.apicross.core.HasCustomModelAttributes;
import io.swagger.v3.oas.models.Operation;
import com.github.itroadlabs.oas.apicross.core.NamedDatum;
import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModel;

import java.util.Collection;
import java.util.Map;

public class RequestsHandlerMethod extends HasCustomModelAttributes {
    private String httpMethod;
    private MediaTypeContentModel responseBody;
    private MediaTypeContentModel requestBody;
    private Collection<RequestQueryParameter> queryParameters;
    private Collection<RequestUriPathParameter> pathParameters;
    private String uriPath;
    private String methodName;
    private String documentation;
    private String requestDocumentation;
    private String responseDocumentation;
    private Operation operation;
    private boolean requestBodyRequired;

    public String getHttpMethod() {
        return httpMethod;
    }

    public RequestsHandlerMethod setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public MediaTypeContentModel getResponseBody() {
        return responseBody;
    }

    public RequestsHandlerMethod setResponseBody(MediaTypeContentModel responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public DataModel getResponseBodyContent() {
        return responseBody != null ? responseBody.getContent() : null;
    }

    public MediaTypeContentModel getRequestBody() {
        return requestBody;
    }

    public RequestsHandlerMethod setRequestBody(MediaTypeContentModel requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public DataModel getRequestBodyContent() {
        return requestBody != null ? requestBody.getContent() : null;
    }

    public Collection<RequestQueryParameter> getQueryParameters() {
        return queryParameters;
    }

    public Collection<RequestUriPathParameter> getPathParameters() {
        return pathParameters;
    }

    public RequestsHandlerMethod setQueryParameters(Collection<RequestQueryParameter> queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public String getUriPath() {
        return uriPath;
    }

    public RequestsHandlerMethod setUriPath(String uriPath) {
        this.uriPath = uriPath;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public RequestsHandlerMethod setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getDocumentation() {
        return documentation;
    }

    public RequestsHandlerMethod setDocumentation(String documentation) {
        this.documentation = documentation;
        return this;
    }

    public String getRequestDocumentation() {
        return requestDocumentation;
    }

    public RequestsHandlerMethod setRequestDocumentation(String requestDocumentation) {
        this.requestDocumentation = requestDocumentation;
        return this;
    }

    public String getResponseDocumentation() {
        return responseDocumentation;
    }

    public RequestsHandlerMethod setResponseDocumentation(String responseDocumentation) {
        this.responseDocumentation = responseDocumentation;
        return this;
    }

    public RequestsHandlerMethod setPathParameters(Collection<RequestUriPathParameter> pathParameters) {
        this.pathParameters = pathParameters;
        return this;
    }

    public RequestsHandlerMethod setOperation(Operation operation) {
        this.operation = operation;
        return this;
    }

    public boolean hasQueryParameters() {
        return (this.queryParameters != null) && !this.queryParameters.isEmpty();
    }

    public String getConsumesMediaType() {
        return requestBody != null ? requestBody.getMediaType() : null;
    }

    public String getProducesMediaType() {
        return responseBody != null ? responseBody.getMediaType() : null;
    }

    public String getOperationId() {
        return operation.getOperationId();
    }

    public boolean isAnyOptionalQueryParameter() {
        return queryParameters != null && queryParameters.stream().anyMatch(NamedDatum::isOptional);
    }

    public boolean hasSecurityOptions() {
        return operation.getSecurity() != null && !operation.getSecurity().isEmpty();
    }

    public void replaceModelTypesByExternalTypesMap(Map<String, String> externalTypesMap) {
        if (requestBody != null) {
            replaceMediaTypeContentModelByExternalTypesMap(externalTypesMap, requestBody);
        }
        if (responseBody != null) {
            replaceMediaTypeContentModelByExternalTypesMap(externalTypesMap, responseBody);
        }
    }

    private void replaceMediaTypeContentModelByExternalTypesMap(Map<String, String> externalTypesMap, MediaTypeContentModel contentModel) {
        for (String internalSchemaName : externalTypesMap.keySet()) {
            DataModel content = contentModel.getContent();
            if (content instanceof ObjectDataModel) {
                if (((ObjectDataModel) content).getTypeName().equals(internalSchemaName)) {
                    ((ObjectDataModel) content).changeTypeToExternal(externalTypesMap.get(internalSchemaName));
                }
            }
        }
    }

    public RequestsHandlerMethod setRequestBodyRequired(boolean requestBodyRequired) {
        this.requestBodyRequired = requestBodyRequired;
        return this;
    }

    public boolean isRequestBodyRequired() {
        return requestBodyRequired;
    }


    @Override
    public String toString() {
        return "RequestsHandlerMethod{" +
                "httpMethod='" + httpMethod + '\'' +
                ", uriPath='" + uriPath + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
