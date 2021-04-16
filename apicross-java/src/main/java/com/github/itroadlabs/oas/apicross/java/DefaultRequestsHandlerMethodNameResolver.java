package com.github.itroadlabs.oas.apicross.java;

import io.swagger.v3.oas.models.Operation;
import com.github.itroadlabs.oas.apicross.core.handler.RequestsHandlerMethodNameResolver;

import javax.annotation.Nonnull;

public class DefaultRequestsHandlerMethodNameResolver implements RequestsHandlerMethodNameResolver {
    @Nonnull
    @Override
    public String resolve(@Nonnull Operation operation, @Nonnull String uriPath,
                          String consumesMediaType, String producesMediaType) {
        return new DefaultMethodNameBuilder()
                .operationId(operation.getOperationId())
                .consumingsMediaType(consumesMediaType)
                .producingMediaType(producesMediaType)
                .build();
    }

}
