package io.github.itroadlabs.apicross.springcloudopenfeign.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.github.itroadlabs.apicross.springcloudopenfeign.test.adapters.web.TestRequestsHandlerClient;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.SocketUtils;

@EnableFeignClients
@SpringBootConfiguration
@Slf4j
public class TestGeneratedCodeConfiguration {
    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonNullableModule());
        return objectMapper;
    }

    @Bean
    WireMockConfiguration wireMockConfiguration() {
        return new WireMockConfiguration().port(SocketUtils.findAvailableTcpPort()).notifier(new Slf4jNotifier(false));
    }

    @Bean(destroyMethod = "stop")
    WireMockServer wireMockServer(WireMockConfiguration configuration) {
        WireMockServer wireMockServer = new WireMockServer(configuration);
        wireMockServer.start();
        return wireMockServer;
    }

    @Bean
    TestRequestsHandlerClient testRequestsHandlerClient(ApplicationContext applicationContext, WireMockConfiguration wireMockConfiguration) {
        return new FeignClientBuilder(applicationContext)
                .forType(TestRequestsHandlerClient.class, "test")
                .url("http://localhost:" + wireMockConfiguration.portNumber())
                .build();
    }
}
