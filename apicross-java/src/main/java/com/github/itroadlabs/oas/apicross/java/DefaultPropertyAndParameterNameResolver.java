package com.github.itroadlabs.oas.apicross.java;

import com.github.itroadlabs.oas.apicross.core.data.PropertyNameResolver;
import com.github.itroadlabs.oas.apicross.core.handler.ParameterNameResolver;
import io.swagger.v3.oas.models.media.Schema;

import javax.annotation.Nonnull;

public class DefaultPropertyAndParameterNameResolver implements PropertyNameResolver, ParameterNameResolver {
    public DefaultPropertyAndParameterNameResolver() {
    }

    @Override
    @Nonnull
    public String resolvePropertyName(@Nonnull Schema<?> propertySchema, @Nonnull String apiPropertyName) {
        return StringToJavaIdentifierUtil.resolve(apiPropertyName);
    }

    @Nonnull
    @Override
    public String resolveParameterName(@Nonnull Schema<?> parameterSchema, @Nonnull String apiParameterName) {
        return StringToJavaIdentifierUtil.resolve(apiParameterName);
    }

}
