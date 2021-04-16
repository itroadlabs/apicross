package com.github.itroadlabs.oas.apicross.core.handler.impl;

import com.github.itroadlabs.oas.apicross.utils.OpenApiComponentsIndex;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Slf4j
class DefaultOperationRequestAndResponseResolver implements OperationRequestAndResponseResolver {
    private final OpenApiComponentsIndex componentsSchemas;

    public DefaultOperationRequestAndResponseResolver(@Nonnull OpenApiComponentsIndex componentsSchemas) {
        this.componentsSchemas = Objects.requireNonNull(componentsSchemas);
    }

    @Override
    @Nonnull
    public List<OperationRequestAndResponse> resolve(@Nonnull Operation operation) {
        RequestBody requestBody = Objects.requireNonNull(operation).getRequestBody();
        if (requestBody != null && requestBody.get$ref() != null) {
            requestBody = componentsSchemas.requestBodyBy$ref(requestBody.get$ref());
        }

        List<OperationRequestAndResponse> result = new ArrayList<>();

        ApiResponse resultResponse = find2xxApiResponseOrDefault(operation.getResponses());

        Map<String, MediaType> responsesMap = null;
        String responseDescription = null;
        if (resultResponse != null) {
            responseDescription = resultResponse.getDescription();
            responsesMap = resultResponse.getContent();
        }

        Map<String, MediaType> requestsMap = null;
        String requestDescription = null;
        boolean requestBodyRequired = false;
        if (requestBody != null) {
            requestsMap = requestBody.getContent();
            requestDescription = requestBody.getDescription();
            requestBodyRequired = BooleanUtils.isTrue(requestBody.getRequired());
        }

        if (responsesMap != null && requestsMap != null) {
            for (String requestMediaType : requestsMap.keySet()) {
                for (String responseMediaType : responsesMap.keySet()) {
                    result.add(new OperationRequestAndResponse()
                            .setRequestBodyRequired(requestBodyRequired)
                            .setRequestMediaType(requestMediaType)
                            .setResponseMediaType(responseMediaType)
                            .setRequestContentSchema(requestsMap.get(requestMediaType).getSchema())
                            .setResponseContentSchema(responsesMap.get(responseMediaType).getSchema())
                            .setResponseDescription(responseDescription)
                            .setRequestDescription(requestDescription));
                }
            }
        } else if (responsesMap != null) {
            for (String responseMediaType : responsesMap.keySet()) {
                result.add(new OperationRequestAndResponse()
                        .setRequestMediaType(null)
                        .setResponseMediaType(responseMediaType)
                        .setRequestContentSchema(null)
                        .setResponseContentSchema(responsesMap.get(responseMediaType).getSchema())
                        .setResponseDescription(responseDescription)
                        .setRequestDescription(null));
            }
        } else if (requestsMap != null) {
            for (String requestMediaType : requestsMap.keySet()) {
                result.add(new OperationRequestAndResponse()
                        .setRequestBodyRequired(requestBodyRequired)
                        .setRequestMediaType(requestMediaType)
                        .setResponseMediaType(null)
                        .setRequestContentSchema(requestsMap.get(requestMediaType).getSchema())
                        .setResponseContentSchema(null)
                        .setResponseDescription(null)
                        .setRequestDescription(requestDescription));
            }
        } else if (resultResponse != null) {
            result.add(new OperationRequestAndResponse()
                    .setRequestMediaType(null)
                    .setRequestContentSchema(null)
                    .setRequestBodyRequired(false)
                    .setRequestDescription(null)
                    .setResponseMediaType(null)
                    .setResponseDescription(resultResponse.getDescription())
                    .setResponseContentSchema(null)
            );
        }

        return result;
    }

    @Nullable
    private ApiResponse find2xxApiResponseOrDefault(ApiResponses responses) {
        Set<String> responsesDeclaredHttpStatuses = responses.keySet();
        for (int httpStatus = 200; httpStatus < 299; httpStatus++) {
            String httpStatusStr = String.valueOf(httpStatus);
            if (responsesDeclaredHttpStatuses.contains(httpStatusStr)) {
                return responses.get(httpStatusStr);
            }
        }
        return responses.getDefault();
    }
}
