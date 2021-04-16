package com.github.itroadlabs.oas.apicross.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class OpenApiSpecificationParser {
    public static OpenAPI parse(final String url) throws IOException {
        return parse((parser, options) -> parser.readLocation(url, null, options));
    }

    public static OpenAPI parse(final InputStream in) throws IOException {
        return parse((parser, options) -> parser.readContents(IOUtils.toString(in, StandardCharsets.UTF_8), null, options));
    }

    private static OpenAPI parse(DoParse call) throws IOException {
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        SwaggerParseResult swaggerParseResult = call.parse(parser, options);
        handleParseResult(swaggerParseResult);
        return swaggerParseResult.getOpenAPI();
    }

    @FunctionalInterface
    private interface DoParse {
        SwaggerParseResult parse(OpenAPIV3Parser parser, ParseOptions options) throws IOException;
    }

    private static void handleParseResult(SwaggerParseResult swaggerParseResult) {
        if (swaggerParseResult.getMessages() != null) {
            if (!swaggerParseResult.getMessages().isEmpty()) {
                for (String message : swaggerParseResult.getMessages()) {
                    log.error(message);
                }
                throw new OpenApiSpecificationParseException("OpenAPI specification has a problems", swaggerParseResult);
            }
        }
    }
}
