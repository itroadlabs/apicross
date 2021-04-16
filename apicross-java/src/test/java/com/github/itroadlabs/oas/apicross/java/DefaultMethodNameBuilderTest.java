package com.github.itroadlabs.oas.apicross.java;

import com.github.itroadlabs.oas.apicross.java.DefaultMethodNameBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DefaultMethodNameBuilderTest {
    @Test
    public void whenNoOperationPayloads_thenMethodNameIsOperationId() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .build();
        assertEquals("getResource", methodName);
    }

    @Test
    public void whenOperationProducesContent_thenMethodNameIsOperationIdAndProduceMediaType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .producingMediaType("text/plain")
                .build();
        assertEquals("getResourceProducePlainText", methodName);

        methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .producingMediaType("application/vnd.content.v1+json")
                .build();
        assertEquals("getResourceProduceVndContentV1Json", methodName);
    }

    @Test
    public void whenOperationConsumesContent_thenMethodNameIsOperationIdAndConsumeMediaType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .consumingsMediaType("text/plain")
                .build();
        assertEquals("getResourceConsumePlainText", methodName);

        methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("application/vnd.content.v1+json")
                .build();
        assertEquals("putResourceConsumeVndContentV1Json", methodName);
    }

    @Test
    public void whenOperationConsumesAndProducesContent_thenMethodNameIsOperationIdAndConsumeProduceMediaType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("search")
                .consumingsMediaType("application/x.query.v1+json")
                .producingMediaType("application/x.content.v2+json")
                .build();
        assertEquals("searchConsumeXQueryV1JsonProduceXContentV2Json", methodName);
    }

    @Test
    public void masksIsNotAllowedInProducingMediaType() {
        try {
            new DefaultMethodNameBuilder()
                    .operationId("getResource")
                    .producingMediaType("application/*")
                    .build();
            fail();
        } catch (IllegalArgumentException e) {
            // it's ok
        }

        try {
            new DefaultMethodNameBuilder()
                    .operationId("getResource")
                    .producingMediaType("*/json")
                    .build();
            fail();
        } catch (IllegalArgumentException e) {
            // it's ok
        }

        try {
            new DefaultMethodNameBuilder()
                    .operationId("getResource")
                    .producingMediaType("*/*")
                    .build();
            fail();
        } catch (IllegalArgumentException e) {
            // it's ok
        }
    }

    @Test
    public void nullsAreAllowedInPropducingMediaType() {
        new DefaultMethodNameBuilder()
                .operationId("getResource")
                .producingMediaType(null)
                .build();
    }

    @Test
    public void masksIsAllowedInConsumingMediaType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("application/*")
                .build();
        assertEquals("putResourceConsumeApplicationAnySubType", methodName);

        methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("*/json")
                .producingMediaType("application/json")
                .build();
        assertEquals("putResourceConsumeAnyTypeJsonProduceJson", methodName);

        methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("*/*")
                .producingMediaType("application/json")
                .build();
        assertEquals("putResourceProduceJson", methodName);
    }
}
