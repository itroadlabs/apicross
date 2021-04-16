package com.github.itroadlabs.oas.apicross.core.data;

public interface InlineModelTypeNameResolver {
    String resolveTypeName(String typeName, String propertyResolvedName);

    default String resolveArrayItemTypeName(String typeName, String arrayPropertyResolvedName) {
        return resolveTypeName(typeName, arrayPropertyResolvedName + "Item");
    }
}
