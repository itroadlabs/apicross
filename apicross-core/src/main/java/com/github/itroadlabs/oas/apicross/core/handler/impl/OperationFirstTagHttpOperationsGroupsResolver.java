package com.github.itroadlabs.oas.apicross.core.handler.impl;

import com.github.itroadlabs.oas.apicross.core.handler.model.HttpOperationsGroup;
import com.github.itroadlabs.oas.apicross.core.handler.HttpOperationsGroupsResolver;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Groups API operations by first tag
 */
@Slf4j
public class OperationFirstTagHttpOperationsGroupsResolver implements HttpOperationsGroupsResolver {
    private final Set<String> onlyTags;
    private final Set<String> skipTags;

    public OperationFirstTagHttpOperationsGroupsResolver(Set<String> onlyTags, Set<String> skipTags) {
        this.onlyTags = onlyTags;
        this.skipTags = skipTags;
    }

    @Override
    public Collection<HttpOperationsGroup> resolve(Map<String, PathItem> pathItems) {
        log.debug("Grouping operations by first tag");
        Map<String, HttpOperationsGroup> byTagMap = new HashMap<>();
        for (String uriPath : pathItems.keySet()) {
            PathItem pathItem = pathItems.get(uriPath);
            Map<PathItem.HttpMethod, Operation> operationsByHttpMethod = pathItem.readOperationsMap();
            log.debug("For uri '{}' were detected following HTTP methods: {}", uriPath, operationsByHttpMethod.keySet());
            for (PathItem.HttpMethod httpMethod : operationsByHttpMethod.keySet()) {
                Operation operation = operationsByHttpMethod.get(httpMethod);
                if (needsToBoSkipped(operation)) {
                    continue;
                }
                String firstTag = operation.getTags().get(0);
                byTagMap.computeIfAbsent(firstTag, HttpOperationsGroup::new)
                        .add(operation, uriPath, httpMethod);
            }
        }
        return byTagMap.values();
    }

    private boolean needsToBoSkipped(Operation operation) {
        if (this.skipTags != null && !this.skipTags.isEmpty()) {
            return !Collections.disjoint(this.skipTags, operation.getTags());
        } else if (this.onlyTags != null && !this.onlyTags.isEmpty()) {
            return Collections.disjoint(this.onlyTags, operation.getTags());
        }
        return false;
    }
}
