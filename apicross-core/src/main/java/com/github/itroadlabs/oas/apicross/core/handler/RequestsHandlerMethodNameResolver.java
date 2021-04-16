package com.github.itroadlabs.oas.apicross.core.handler;

import io.swagger.v3.oas.models.Operation;

import javax.annotation.Nonnull;

public interface RequestsHandlerMethodNameResolver {
    @Nonnull
    String resolve(@Nonnull Operation operation, @Nonnull String uriPath, String consumesMediaType, String producesMediaType);
}
