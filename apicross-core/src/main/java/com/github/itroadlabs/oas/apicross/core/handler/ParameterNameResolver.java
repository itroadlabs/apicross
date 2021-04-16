package com.github.itroadlabs.oas.apicross.core.handler;

import io.swagger.v3.oas.models.media.Schema;

import javax.annotation.Nonnull;

public interface ParameterNameResolver {
    @Nonnull
    String resolveParameterName(@Nonnull Schema<?> parameterSchema, @Nonnull String apiParameterName);
}
