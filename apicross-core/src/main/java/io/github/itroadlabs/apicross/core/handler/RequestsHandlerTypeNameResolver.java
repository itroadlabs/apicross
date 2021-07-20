package io.github.itroadlabs.apicross.core.handler;

import io.github.itroadlabs.apicross.core.handler.model.HttpOperationsGroup;

public interface RequestsHandlerTypeNameResolver {
    String resolve(HttpOperationsGroup requestsHandlerOperations);
}
