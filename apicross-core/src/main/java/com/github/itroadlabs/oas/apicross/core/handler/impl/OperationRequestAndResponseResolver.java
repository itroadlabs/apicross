package com.github.itroadlabs.oas.apicross.core.handler.impl;

import io.swagger.v3.oas.models.Operation;

import javax.annotation.Nonnull;
import java.util.List;

interface OperationRequestAndResponseResolver {
    @Nonnull
    List<OperationRequestAndResponse> resolve(@Nonnull Operation operation);
}
