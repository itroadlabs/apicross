package com.github.itroadlabs.oas.apicross.core.handler;

import com.github.itroadlabs.oas.apicross.core.handler.model.HttpOperationsGroup;

public interface RequestsHandlerTypeNameResolver {
    String resolve(HttpOperationsGroup requestsHandlerOperations);
}
