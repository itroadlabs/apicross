package com.github.itroadlabs.oas.apicross.core.data;

import io.swagger.v3.oas.models.media.Schema;

import javax.annotation.Nonnull;

public interface PropertyNameResolver {
    @Nonnull
    String resolvePropertyName(@Nonnull Schema<?> propertySchema, @Nonnull String apiPropertyName);
}
