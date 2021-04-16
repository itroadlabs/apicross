package com.github.itroadlabs.oas.apicross.utils;

import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class OpenApiSpecificationParseException extends RuntimeException {
    private final SwaggerParseResult swaggerParseResult;

    public OpenApiSpecificationParseException(String s, SwaggerParseResult swaggerParseResult) {
        super(s);
        this.swaggerParseResult = swaggerParseResult;
    }

    public SwaggerParseResult getSwaggerParseResult() {
        return swaggerParseResult;
    }
}
