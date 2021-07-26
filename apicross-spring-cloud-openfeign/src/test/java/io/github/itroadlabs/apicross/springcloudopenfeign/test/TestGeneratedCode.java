package io.github.itroadlabs.apicross.springcloudopenfeign.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import feign.FeignException;
import io.github.itroadlabs.apicross.springcloudopenfeign.test.adapters.web.TestRequestsHandlerClient;
import io.github.itroadlabs.apicross.springcloudopenfeign.test.adapters.web.model.CreateTestDataRepresentationApiModel;
import io.github.itroadlabs.apicross.springcloudopenfeign.test.adapters.web.model.ListTestDataRepresentationApiModel;
import io.github.itroadlabs.apicross.springcloudopenfeign.test.adapters.web.model.TestDataApiModel;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(classes = {TestGeneratedCodeConfiguration.class, FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
class TestGeneratedCode {
    @Autowired
    private TestRequestsHandlerClient client;
    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void resetWireMockServer() {
        wireMockServer.resetAll();
    }

    @Test
    void postWithJsonRequestBody() {
        wireMockServer.givenThat(post("/test-data")
                .withHeader("Content-type", equalTo("application/json"))
                .withRequestBody(equalToJson("{\"id\":\"hello\",\"property1\":\"test1\"}", true, false))
                .willReturn(status(201)));

        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<?> response = client.createTestDataConsumeJson(headers,
                new CreateTestDataRepresentationApiModel()
                        .withId("hello")
                        .withProperty1("test1"));

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    void workWithHeaders() {
        wireMockServer.givenThat(post("/test-data")
                .withHeader("x-idempotency-key", equalTo("test"))
                .withHeader("Content-type", equalTo("application/json"))
                .willReturn(status(201).withHeader("Location", "/test-data/hello")));

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-idempotency-key", "test");
        ResponseEntity<?> response = client.createTestDataConsumeJson(headers,
                new CreateTestDataRepresentationApiModel()
                        .withId("hello")
                        .withProperty1("test1"));

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getHeaders()).containsKey("Location");
    }

    @Test
    void postWithBinaryRequestBody() {
        wireMockServer.givenThat(post("/test-data").withHeader("Content-type", equalTo("application/octet-stream"))
                .willReturn(status(201)));

        byte[] bytes = new byte[20];
        new Random().nextBytes(bytes);

        InputStreamResource requestBody = new InputStreamResource(new ByteArrayInputStream(bytes));

        ResponseEntity<?> response = client.createTestDataConsumeApplicationOctetStream(new HttpHeaders(), requestBody);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    void getForJsonResponseBody() {
        wireMockServer.givenThat(get("/test-data/hello").withHeader("Accept", equalTo("application/json"))
                .willReturn(
                        status(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":\"hello\", \"property1\":\"test\"}")));

        ResponseEntity<TestDataApiModel> response = client.findTestDataByIdProduceJson("hello", new HttpHeaders());

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        TestDataApiModel responseModel = response.getBody();
        assertThat(responseModel).isNotNull();
        assertThat(responseModel.getId()).isEqualTo("hello");
        assertThat(responseModel.getProperty1()).isEqualTo("test");
        assertThat(responseModel.isProperty2Present()).isFalse();
    }

    @Test
    void getForBinaryDataResponseBody() throws IOException {
        byte[] originalBytes = new byte[20];
        new Random().nextBytes(originalBytes);

        wireMockServer.givenThat(get("/test-data/hello").withHeader("Accept", equalTo("application/octet-stream"))
                .willReturn(
                        status(200)
                                .withHeader("Content-Type", "application/octet-stream")
                                .withBody(originalBytes)));

        ResponseEntity<Resource> response = client.findTestDataByIdProduceApplicationOctetStream("hello", new HttpHeaders());

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        assertThat(response.getBody()).isNotNull();
        byte[] responseBytes = IOUtils.toByteArray(response.getBody().getInputStream());
        assertThat(responseBytes).isEqualTo(originalBytes);
    }

    @Test
    void getWithQueryParameters() {
        wireMockServer.givenThat(
                get(urlPathEqualTo("/test-data"))
                        .withHeader("Accept", equalTo("application/json"))
                        .withQueryParam("page", equalTo("1"))
                        .withQueryParam("page_size", equalTo("20"))
                        .willReturn(
                                status(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("{}")));

        ResponseEntity<ListTestDataRepresentationApiModel> response = client.listTestDataProduceJson(1, 20, new HttpHeaders());
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void getWithQueryParametersMissed() {
        wireMockServer.givenThat(
                get(urlPathEqualTo("/test-data"))
                        .withHeader("Accept", equalTo("application/json"))
                        .willReturn(
                                status(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("{}")));

        ResponseEntity<ListTestDataRepresentationApiModel> response = client.listTestDataProduceJson(null, null, new HttpHeaders());
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void error404handling() {
        wireMockServer.givenThat(get("/test-data/hello").withHeader("Accept", equalTo("application/json"))
                .willReturn(status(404)));

        try {
            client.findTestDataByIdProduceJson("hello", new HttpHeaders());
            fail("must be exception");
        } catch (FeignException e) {
            assertThat(e.status()).isEqualTo(404);
        }
    }
}
