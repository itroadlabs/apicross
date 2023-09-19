package io.github.itroadlabs.apicross.core.data;

import io.github.itroadlabs.apicross.utils.OpenApiComponentsIndex;
import io.github.itroadlabs.apicross.utils.OpenApiSpecificationParser;
import io.swagger.v3.oas.models.OpenAPI;

import java.io.IOException;

public abstract class DataModelResolverTestsBase {
    protected OpenApiComponentsIndex openAPIComponentsIndex;
    protected DataModelResolver resolver;

    protected void init(String apiSpecificationResource) throws IOException {
        OpenAPI openAPI = OpenApiSpecificationParser.parse(getClass()
                .getResourceAsStream(apiSpecificationResource));
        openAPIComponentsIndex = new OpenApiComponentsIndex(openAPI);
        resolver = new DataModelResolver(openAPIComponentsIndex, (propertySchema, apiPropertyName) -> apiPropertyName);
    }
}
