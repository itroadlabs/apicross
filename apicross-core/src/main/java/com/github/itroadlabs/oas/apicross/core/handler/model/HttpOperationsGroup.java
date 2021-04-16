package com.github.itroadlabs.oas.apicross.core.handler.model;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import lombok.NonNull;

import java.util.*;

/**
 * Group of operations to be implemented in single RequestsHandler
 */
public class HttpOperationsGroup {
    private final String name;
    private final List<HttpOperation> httpOperations = new ArrayList<>();

    public HttpOperationsGroup(@NonNull String name) {
        this.name = name;
    }

    public void add(@NonNull Operation operation, @NonNull String uriPath, @NonNull PathItem.HttpMethod httpMethod) {
        httpOperations.add(new HttpOperation(uriPath, httpMethod, operation));
    }

    public Collection<HttpOperation> operations() {
        return Collections.unmodifiableList(httpOperations);
    }

    public int size() {
        return httpOperations.size();
    }

    public String getName() {
        return name;
    }

}
