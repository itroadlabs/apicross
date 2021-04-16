package com.github.itroadlabs.oas.apicross.java;

import com.github.itroadlabs.oas.apicross.core.handler.RequestsHandlerMethodNameResolver;
import io.swagger.v3.oas.models.Operation;

import javax.annotation.Nonnull;

public class SimpleRequestsHandlerMethodNameResolver implements RequestsHandlerMethodNameResolver {
    @Nonnull
    @Override
    public String resolve(@Nonnull Operation operation, @Nonnull String uriPath, String consumesMediaType, String producesMediaType) {
        return StringToJavaIdentifierUtil.resolve(operation.getOperationId());
    }
}
