package com.github.itroadlabs.oas.apicross.core.handler.impl;

import com.github.itroadlabs.oas.apicross.utils.OpenApiComponentsIndex;
import com.github.itroadlabs.oas.apicross.utils.OpenApiSpecificationParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DefaultOperationRequestAndResponseResolverTest {
    private OpenAPI openAPI;
    private DefaultOperationRequestAndResponseResolver splitter;

    @BeforeEach
    public void loadOpenAPI() throws IOException {
        openAPI = OpenApiSpecificationParser.parse(getClass()
                .getResourceAsStream("DefaultOperationInputOutputResolverTest.yaml"));
        splitter = new DefaultOperationRequestAndResponseResolver(new OpenApiComponentsIndex(openAPI));
    }

    @Test
    public void splitByResponses() {
        Operation getOperation = openAPI.getPaths().get("/resource1").getGet();
        List<OperationRequestAndResponse> splitResult = splitter.resolve(getOperation);

        assertEquals(3, splitResult.size());

        Set<String> responseMediaTypes = splitResult.stream()
                .map(OperationRequestAndResponse::getResponseMediaType)
                .collect(Collectors.toSet());
        assertTrue(responseMediaTypes.contains("application/json"));
        assertTrue(responseMediaTypes.contains("application/pdf"));
        assertTrue(responseMediaTypes.contains("application/msword"));
    }

    @Test
    public void splitByRequests() {
        Operation postOperation = openAPI.getPaths().get("/resource2").getPost();
        List<OperationRequestAndResponse> splitResult = splitter.resolve(postOperation);

        assertEquals(3, splitResult.size());

        Set<String> requestsMediaTypes = splitResult.stream()
                .map(OperationRequestAndResponse::getRequestMediaType)
                .collect(Collectors.toSet());
        assertTrue(requestsMediaTypes.contains("application/json"));
        assertTrue(requestsMediaTypes.contains("application/pdf"));
        assertTrue(requestsMediaTypes.contains("application/msword"));
    }

    @Test
    public void splitByRequestsAndResponses() {
        Operation putOperation = openAPI.getPaths().get("/resource3").getPut();
        List<OperationRequestAndResponse> splitResult = splitter.resolve(putOperation);

        assertEquals(4, splitResult.size());

        class Filter implements Predicate<OperationRequestAndResponse> {
            private String consumesMediaType;
            private String producesMediaType;

            private Filter(String consumesMediaType, String producesMediaType) {
                this.consumesMediaType = consumesMediaType;
                this.producesMediaType = producesMediaType;
            }

            @Override
            public boolean test(OperationRequestAndResponse operationPayloadsDescription) {
                return consumesMediaType.equals(operationPayloadsDescription.getRequestMediaType()) &&
                        producesMediaType.equals(operationPayloadsDescription.getResponseMediaType());
            }
        }

        assertTrue(splitResult.stream().anyMatch(new Filter("application/vnd.mayapp.v1+json", "application/pdf")));
        assertTrue(splitResult.stream().anyMatch(new Filter("application/vnd.mayapp.v1+json", "application/msword")));
        assertTrue(splitResult.stream().anyMatch(new Filter("application/vnd.mayapp.v2+json", "application/pdf")));
        assertTrue(splitResult.stream().anyMatch(new Filter("application/vnd.mayapp.v2+json", "application/msword")));
    }

    @Test
    public void testByRequestResponseReusableModels() {
        Operation putOperation = openAPI.getPaths().get("/resource4").getPost();
        List<OperationRequestAndResponse> splitResult = splitter.resolve(putOperation);

        assertEquals(4, splitResult.size());

        class Filter implements Predicate<OperationRequestAndResponse> {
            private String consumesMediaType;
            private String producesMediaType;

            private Filter(String consumesMediaType, String producesMediaType) {
                this.consumesMediaType = consumesMediaType;
                this.producesMediaType = producesMediaType;
            }

            @Override
            public boolean test(OperationRequestAndResponse operationPayloadsDescription) {
                return consumesMediaType.equals(operationPayloadsDescription.getRequestMediaType()) &&
                        producesMediaType.equals(operationPayloadsDescription.getResponseMediaType());
            }
        }

        assertTrue(splitResult.stream().anyMatch(new Filter("application/vnd.mayapp.v1+json", "application/vnd.mayapp.v1+json")));
        assertTrue(splitResult.stream().anyMatch(new Filter("application/vnd.mayapp.v1+json", "image/jpeg")));
        assertTrue(splitResult.stream().anyMatch(new Filter("image/jpeg", "application/vnd.mayapp.v1+json")));
        assertTrue(splitResult.stream().anyMatch(new Filter("image/jpeg", "image/jpeg")));
    }
}
