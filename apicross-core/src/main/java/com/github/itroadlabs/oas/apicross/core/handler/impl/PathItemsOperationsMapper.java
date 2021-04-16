package com.github.itroadlabs.oas.apicross.core.handler.impl;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.util.HashMap;
import java.util.Map;

class PathItemsOperationsMapper {
    static Map<String, Operation> mapOperationsByHttpMethod(PathItem pathItem) {
        Map<String, Operation> result = new HashMap<>();
        if (pathItem.getGet() != null) {
            result.put("GET", pathItem.getGet());
        }
        if (pathItem.getPost() != null) {
            result.put("POST", pathItem.getPost());
        }
        if (pathItem.getPut() != null) {
            result.put("PUT", pathItem.getPut());
        }
        if (pathItem.getDelete() != null) {
            result.put("DELETE", pathItem.getDelete());
        }
        if (pathItem.getPatch() != null) {
            result.put("PATCH", pathItem.getPatch());
        }
        if (pathItem.getHead() != null) {
            result.put("HEAD", pathItem.getHead());
        }
        if (pathItem.getTrace() != null) {
            result.put("TRACE", pathItem.getTrace());
        }
        if (pathItem.getOptions() != null) {
            result.put("OPTIONS", pathItem.getOptions());
        }
        return result;
    }
}
