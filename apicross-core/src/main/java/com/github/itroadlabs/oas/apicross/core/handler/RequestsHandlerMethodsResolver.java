package com.github.itroadlabs.oas.apicross.core.handler;

import com.github.itroadlabs.oas.apicross.core.handler.model.HttpOperation;
import com.github.itroadlabs.oas.apicross.core.handler.model.RequestsHandlerMethod;

import javax.annotation.Nonnull;
import java.util.List;

public interface RequestsHandlerMethodsResolver {
    @Nonnull
    List<RequestsHandlerMethod> resolve(@Nonnull HttpOperation httpOperation,
                                        @Nonnull RequestsHandlerMethodNameResolver methodNameResolver,
                                        @Nonnull ParameterNameResolver parameterNameResolver);
}
