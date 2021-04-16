package com.github.itroadlabs.oas.apicross.core.handler.impl;

import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;
import com.github.itroadlabs.oas.apicross.core.data.DataModelResolver;
import com.github.itroadlabs.oas.apicross.core.handler.ParameterNameResolver;
import com.github.itroadlabs.oas.apicross.core.handler.RequestsHandlerMethodNameResolver;
import com.github.itroadlabs.oas.apicross.core.handler.RequestsHandlerMethodsResolver;
import com.github.itroadlabs.oas.apicross.core.handler.model.*;
import com.github.itroadlabs.oas.apicross.utils.OpenApiComponentsIndex;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.BooleanUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultRequestsHandlerMethodsResolver implements RequestsHandlerMethodsResolver {
    private final OperationRequestAndResponseResolver operationRequestAndResponseResolver;
    private final DataModelResolver dataModelResolver;

    public DefaultRequestsHandlerMethodsResolver(DataModelResolver dataModelResolver,
                                                 OpenApiComponentsIndex apiComponentsIndex) {
        this(new DefaultOperationRequestAndResponseResolver(apiComponentsIndex), dataModelResolver);
    }

    DefaultRequestsHandlerMethodsResolver(OperationRequestAndResponseResolver operationRequestAndResponseResolver,
                                          DataModelResolver dataModelResolver) {
        this.operationRequestAndResponseResolver = operationRequestAndResponseResolver;
        this.dataModelResolver = dataModelResolver;
    }

    @Nonnull
    @Override
    public List<RequestsHandlerMethod> resolve(@Nonnull HttpOperation httpOperation,
                                               @Nonnull RequestsHandlerMethodNameResolver methodNameResolver,
                                               @Nonnull ParameterNameResolver parameterNameResolver) {
        Operation operation = Objects.requireNonNull(httpOperation).getOperation();

        List<OperationRequestAndResponse> requestAndResponses = operationRequestAndResponseResolver.resolve(operation);

        List<Parameter> allParameters = operation.getParameters();
        List<RequestQueryParameter> queryParameters = allParameters == null ? Collections.emptyList() : resolveQueryStringParameters(allParameters, parameterNameResolver);
        List<RequestUriPathParameter> pathParameters = allParameters == null ? Collections.emptyList() : resolvePathParameters(allParameters, parameterNameResolver);

        return requestAndResponses.stream()
                .map(operationInputOutput ->
                        createRequestsHandlerMethod(httpOperation, operationInputOutput, queryParameters, pathParameters, methodNameResolver))
                .collect(Collectors.toList());
    }

    private RequestsHandlerMethod createRequestsHandlerMethod(HttpOperation httpOperation,
                                                              OperationRequestAndResponse requestAndResponse,
                                                              Collection<RequestQueryParameter> queryParameters,
                                                              Collection<RequestUriPathParameter> pathParameters,
                                                              RequestsHandlerMethodNameResolver methodNameResolver) {
        Operation operation = httpOperation.getOperation();
        String uriPath = httpOperation.getUriPath();
        PathItem.HttpMethod httpMethod = httpOperation.getHttpMethod();
        String methodName = methodNameResolver.resolve(operation, uriPath, requestAndResponse.getRequestMediaType(),
                requestAndResponse.getResponseMediaType());

        MediaTypeContentModel requestBody = resolveRequestBody(requestAndResponse);

        return new RequestsHandlerMethod()
                .setOperation(operation)
                .setUriPath(uriPath)
                .setHttpMethod(httpMethod.name())
                .setMethodName(methodName)
                .setDocumentation(operation.getSummary())
                .setRequestDocumentation(requestAndResponse.getRequestDescription())
                .setResponseDocumentation(requestAndResponse.getResponseDescription())
                .setQueryParameters(queryParameters)
                .setPathParameters(pathParameters)
                .setRequestBodyRequired(requestAndResponse.isRequestBodyRequired())
                .setRequestBody(requestBody)
                .setResponseBody(resolveResponseBody(requestAndResponse));
    }

    private MediaTypeContentModel resolveRequestBody(OperationRequestAndResponse requestAndResponse) {
        return requestAndResponse.getRequestContentSchema() != null ?
                resolveContentModel(requestAndResponse.getRequestContentSchema(), requestAndResponse.getRequestMediaType()) : null;
    }

    private MediaTypeContentModel resolveResponseBody(OperationRequestAndResponse requestAndResponse) {
        return requestAndResponse.getResponseContentSchema() != null ?
                resolveContentModel(requestAndResponse.getResponseContentSchema(), requestAndResponse.getResponseMediaType()) : null;
    }

    private MediaTypeContentModel resolveContentModel(Schema<?> contentSchema, String mediaType) {
        Preconditions.checkArgument(contentSchema.get$ref() != null, "contentSchema doesn't have $ref: " + contentSchema);
        DataModel dataModel = dataModelResolver.resolve(contentSchema);
        return new MediaTypeContentModel(dataModel, mediaType);
    }

    private List<RequestQueryParameter> resolveQueryStringParameters(List<Parameter> parameters, ParameterNameResolver parameterNameResolver) {
        return parametersOf("query", parameters)
                .map((Function<Parameter, RequestQueryParameter>) parameter -> {
                    Preconditions.checkArgument(parameter != null);
                    DataModel dataModel = dataModelResolver.resolve(parameter.getSchema());
                    String resolvedName = parameterNameResolver.resolveParameterName(parameter.getSchema(), parameter.getName());
                    return new RequestQueryParameter(
                            parameter.getName(),
                            resolvedName,
                            parameter.getDescription(),
                            dataModel,
                            BooleanUtils.isTrue(parameter.getRequired()),
                            BooleanUtils.isTrue(parameter.getDeprecated()));
                })
                .collect(Collectors.toList());
    }

    private List<RequestUriPathParameter> resolvePathParameters(List<Parameter> parameters, ParameterNameResolver parameterNameResolver) {
        return parametersOf("path", parameters)
                .map((Function<Parameter, RequestUriPathParameter>) parameter -> {
                    Preconditions.checkArgument(parameter != null);
                    DataModel dataModel = dataModelResolver.resolve(parameter.getSchema());
                    String resolvedName = parameterNameResolver.resolveParameterName(parameter.getSchema(), parameter.getName());
                    return new RequestUriPathParameter(
                            parameter.getName(),
                            resolvedName,
                            parameter.getDescription(),
                            dataModel,
                            BooleanUtils.isTrue(parameter.getRequired()),
                            BooleanUtils.isTrue(parameter.getDeprecated()));
                })
                .collect(Collectors.toList());
    }

    private Stream<Parameter> parametersOf(@Nonnull String inType, @Nonnull List<Parameter> source) {
        return Objects.requireNonNull(source).stream()
                .filter(parameter -> inType.equals(parameter.getIn()));
    }
}
