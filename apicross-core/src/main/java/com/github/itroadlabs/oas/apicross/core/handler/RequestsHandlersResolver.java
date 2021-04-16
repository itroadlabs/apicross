package com.github.itroadlabs.oas.apicross.core.handler;

import com.github.itroadlabs.oas.apicross.core.handler.model.HttpOperation;
import com.github.itroadlabs.oas.apicross.core.handler.model.HttpOperationsGroup;
import com.github.itroadlabs.oas.apicross.core.handler.model.RequestsHandler;
import com.github.itroadlabs.oas.apicross.core.handler.model.RequestsHandlerMethod;
import io.swagger.v3.oas.models.Paths;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
public class RequestsHandlersResolver {
    private final HttpOperationsGroupsResolver httpOperationsGroupsResolver;
    private final RequestsHandlerTypeNameResolver requestsHandlerTypeNameResolver;
    private final RequestsHandlerMethodNameResolver requestsHandlerMethodNameResolver;
    private final RequestsHandlerMethodsResolver requestsHandlerMethodsResolver;
    private final ParameterNameResolver parameterNameResolver;

    public RequestsHandlersResolver(@Nonnull HttpOperationsGroupsResolver httpOperationsGroupsResolver,
                                    @Nonnull RequestsHandlerTypeNameResolver requestsHandlerTypeNameResolver,
                                    @Nonnull RequestsHandlerMethodNameResolver requestsHandlerMethodNameResolver,
                                    @Nonnull RequestsHandlerMethodsResolver requestsHandlerMethodsResolver,
                                    @Nonnull ParameterNameResolver parameterNameResolver) {
        this.httpOperationsGroupsResolver = Objects.requireNonNull(httpOperationsGroupsResolver);
        this.requestsHandlerTypeNameResolver = Objects.requireNonNull(requestsHandlerTypeNameResolver);
        this.requestsHandlerMethodNameResolver = Objects.requireNonNull(requestsHandlerMethodNameResolver);
        this.requestsHandlerMethodsResolver = Objects.requireNonNull(requestsHandlerMethodsResolver);
        this.parameterNameResolver = Objects.requireNonNull(parameterNameResolver);
    }

    @Nonnull
    public List<RequestsHandler> resolve(@Nonnull Paths paths) {
        Objects.requireNonNull(paths);
        Collection<HttpOperationsGroup> requestsHandlersOperationsGroups = httpOperationsGroupsResolver.resolve(paths);

        List<RequestsHandler> result = new ArrayList<>();

        for (HttpOperationsGroup requestsHandlerOperationsGroup : requestsHandlersOperationsGroups) {
            log.debug("Resolve requests handler for operations group: {}...", requestsHandlerOperationsGroup.getName());

            String handlerTypeName = requestsHandlerTypeNameResolver.resolve(requestsHandlerOperationsGroup);

            List<RequestsHandlerMethod> methods = resolveMethodsOf(requestsHandlerOperationsGroup);
            RequestsHandler handler = new RequestsHandler(handlerTypeName, methods);

            log.debug("Request handler resolved: {}", handler);

            result.add(handler);
        }

        return result;
    }

    private List<RequestsHandlerMethod> resolveMethodsOf(HttpOperationsGroup requestsHandlerOperationsGroup) {
        List<RequestsHandlerMethod> methods = new ArrayList<>();

        for (HttpOperation httpOperation : requestsHandlerOperationsGroup.operations()) {
            log.info("Resolve methods for: {}", httpOperation);
            List<RequestsHandlerMethod> operationMethods =
                    requestsHandlerMethodsResolver.resolve(httpOperation, this.requestsHandlerMethodNameResolver, this.parameterNameResolver);
            methods.addAll(operationMethods);
        }
        return methods;
    }
}
