package com.github.itroadlabs.oas.apicross.core.handler;

import com.github.itroadlabs.oas.apicross.core.handler.model.HttpOperationsGroup;
import io.swagger.v3.oas.models.PathItem;

import java.util.Collection;
import java.util.Map;

public interface HttpOperationsGroupsResolver {
    Collection<HttpOperationsGroup> resolve(Map<String, PathItem> pathItems);
}
