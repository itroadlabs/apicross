package com.github.itroadlabs.oas.apicross.core.handler.impl;

import com.github.itroadlabs.oas.apicross.core.handler.model.HttpOperationsGroup;
import com.github.itroadlabs.oas.apicross.core.handler.HttpOperationsGroupsResolver;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Groups API operations by URI path first segment, i.e. /{first path segment}/{seconds path segment}/...
 */
public class UriPathHttpOperationsGroupsResolver implements HttpOperationsGroupsResolver {
    @Override
    public Collection<HttpOperationsGroup> resolve(Map<String, PathItem> pathItems) {
        Map<String, HttpOperationsGroup> result = new HashMap<>();
        for (String uriPath : pathItems.keySet()) {
            int endIndex = uriPath.lastIndexOf('/');
            String firstPathSegment = endIndex > 0 ? uriPath.substring(1, endIndex - 1) : uriPath;
            PathItem pathItem = pathItems.get(uriPath);
            Map<PathItem.HttpMethod, Operation> operationsByHttpMethod = pathItem.readOperationsMap();
            for (PathItem.HttpMethod httpMethod : operationsByHttpMethod.keySet()) {
                Operation operation = operationsByHttpMethod.get(httpMethod);
                result.computeIfAbsent(firstPathSegment, HttpOperationsGroup::new)
                        .add(operation, uriPath, httpMethod);
            }
        }
        return result.values();
    }
}
