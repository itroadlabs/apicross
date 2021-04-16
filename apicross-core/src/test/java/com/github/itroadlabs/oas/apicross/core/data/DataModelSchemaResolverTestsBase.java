package com.github.itroadlabs.oas.apicross.core.data;

import com.github.itroadlabs.oas.apicross.core.data.DataModelResolver;
import com.github.itroadlabs.oas.apicross.utils.OpenApiComponentsIndex;
import com.github.itroadlabs.oas.apicross.utils.OpenApiSpecificationParser;
import io.swagger.v3.oas.models.OpenAPI;

import java.io.IOException;

public abstract class DataModelSchemaResolverTestsBase {
    protected OpenApiComponentsIndex openAPIComponentsIndex;
    protected DataModelResolver resolver;

    protected void init(String apiSpecificationResource) throws IOException {
        OpenAPI openAPI = OpenApiSpecificationParser.parse(getClass()
                .getResourceAsStream(apiSpecificationResource));
        openAPIComponentsIndex = new OpenApiComponentsIndex(openAPI);
        resolver = new DataModelResolver(openAPIComponentsIndex, (propertySchema, apiPropertyName) -> apiPropertyName);
    }
}
