package com.github.itroadlabs.oas.apicross.core.handler.model;

import com.github.itroadlabs.oas.apicross.core.HasCustomModelAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Models object implementing group of operations declared in API specification
 */
public class RequestsHandler extends HasCustomModelAttributes {
    private final String typeName;
    private final List<RequestsHandlerMethod> methods;

    public RequestsHandler(String typeName, List<RequestsHandlerMethod> methods) {
        this.typeName = typeName;
        this.methods = Collections.unmodifiableList(methods);
    }

    public String getTypeName() {
        return typeName;
    }

    public List<RequestsHandlerMethod> getMethods() {
        return methods;
    }

    public void replaceHandlerParametersByExternalTypesMap(Map<String, String> externalTypesMap) {
        for (RequestsHandlerMethod method : methods) {
            method.replaceModelTypesByExternalTypesMap(externalTypesMap);
        }
    }

    @Override
    public String toString() {
        return "RequestsHandler{" +
                "typeName='" + typeName + '\'' +
                '}';
    }
}
