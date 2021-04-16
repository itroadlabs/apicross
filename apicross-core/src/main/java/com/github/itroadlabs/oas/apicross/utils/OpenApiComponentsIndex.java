package com.github.itroadlabs.oas.apicross.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class OpenApiComponentsIndex {
    private final Map<String, RequestBody> requestBodies;
    private final Map<String, Schema> schemas;

    public OpenApiComponentsIndex(OpenAPI openAPI) {
        this.schemas = openAPI.getComponents().getSchemas();
        this.requestBodies = openAPI.getComponents().getRequestBodies();
    }

    public Set<String> schemasNames() {
        return schemas.keySet();
    }

    @Nonnull
    public Schema<?> schemaByName(String schemaName) {
        return lookup(schemaName);
    }

    @Nonnull
    public Schema<?> schemaBy$ref(String $ref) {
        return resolveRef($ref);
    }

    @Nullable
    public RequestBody requestBodyBy$ref(String $ref) {
        String componentName = resolveComponentName($ref);
        return requestBodies.get(componentName);
    }

    @Nonnull
    private Schema<?> resolveRef(String $ref) {
        String schemaName = resolveComponentName($ref);
        return lookup(schemaName);
    }

    @Nonnull
    private Schema<?> lookup(String schemaName) {
        Schema schema = schemas.get(schemaName);
        if (schema != null) {
            schema.setName(schemaName);
            return resolve(schema);
        } else {
            throw new IllegalArgumentException("No schema with name '" + schemaName + "' found");
        }
    }

    @Nonnull
    private Schema<?> resolve(Schema<?> schema) {
        String $ref = schema.get$ref();
        if ($ref != null) {
            Schema<?> target = resolveRef($ref);
            return resolve(target);
        } else {
            return schema;
        }
    }

    @Nonnull
    private String resolveComponentName(String $ref) {
        return SchemaHelper.schemaNameFromRef($ref);
    }
}
