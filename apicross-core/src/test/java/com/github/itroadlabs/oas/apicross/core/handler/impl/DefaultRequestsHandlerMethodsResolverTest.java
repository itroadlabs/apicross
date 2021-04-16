package com.github.itroadlabs.oas.apicross.core.handler.impl;

import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;
import com.github.itroadlabs.oas.apicross.core.data.DataModelResolver;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModel;
import com.github.itroadlabs.oas.apicross.core.handler.impl.DefaultOperationRequestAndResponseResolver;
import com.github.itroadlabs.oas.apicross.core.handler.impl.DefaultRequestsHandlerMethodsResolver;
import com.github.itroadlabs.oas.apicross.core.handler.impl.OperationRequestAndResponse;
import com.github.itroadlabs.oas.apicross.core.handler.model.HttpOperation;
import com.github.itroadlabs.oas.apicross.core.handler.ParameterNameResolver;
import com.github.itroadlabs.oas.apicross.core.handler.model.RequestsHandlerMethod;
import com.github.itroadlabs.oas.apicross.core.handler.RequestsHandlerMethodNameResolver;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultRequestsHandlerMethodsResolverTest {
    private DefaultRequestsHandlerMethodsResolver resolver;

    @Mock
    private DefaultOperationRequestAndResponseResolver operationRequestAndResponseResolver;
    @Mock
    private RequestsHandlerMethodNameResolver methodNameResolver;
    @Mock
    private DataModelResolver dataModelResolver;
    @Mock
    private ParameterNameResolver parameterNameResolver;

    @BeforeEach
    public void setUp() {
        when(methodNameResolver.resolve(any(Operation.class), anyString(), anyString(), anyString()))
                .thenAnswer(invocationOnMock -> ((Operation) invocationOnMock.getArgument(0)).getOperationId());

        resolver = new DefaultRequestsHandlerMethodsResolver(operationRequestAndResponseResolver, dataModelResolver);
    }

    @Test
    public void resolveMethods() {
        Operation operation = new Operation().operationId("test")
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse()));

        OperationRequestAndResponse operationRequestAndResponse = new OperationRequestAndResponse()
                .setRequestMediaType("application/json")
                .setRequestContentSchema(new ObjectSchema().name("TestRequest").$ref("TestRequest"))
                .setResponseMediaType("application/xml")
                .setResponseContentSchema(new ObjectSchema().name("TestResponse").$ref("TestResponse")); //

        when(operationRequestAndResponseResolver.resolve(any(Operation.class)))
                .thenReturn(Collections.singletonList(operationRequestAndResponse));

        when(dataModelResolver.resolve(any(Schema.class)))
                .thenAnswer((Answer<DataModel>) invocationOnMock -> {
                    ObjectSchema objectSchema = invocationOnMock.getArgument(0);
                    return DataModel.newObjectDataModel(objectSchema, objectSchema.getName());
                });

        List<RequestsHandlerMethod> methods = resolver.resolve(new HttpOperation("/test", PathItem.HttpMethod.GET, operation), methodNameResolver, parameterNameResolver);

        assertEquals(1, methods.size());
        RequestsHandlerMethod requestsHandlerMethod = methods.get(0);
        assertEquals("test", requestsHandlerMethod.getMethodName());
        assertEquals("GET", requestsHandlerMethod.getHttpMethod());
        assertEquals("application/xml", requestsHandlerMethod.getResponseBody().getMediaType());
        assertEquals("application/json", requestsHandlerMethod.getRequestBody().getMediaType());
        assertEquals("TestRequest", ((ObjectDataModel)requestsHandlerMethod.getRequestBody().getContent()).getTypeName());
        assertEquals("TestResponse", ((ObjectDataModel)requestsHandlerMethod.getResponseBody().getContent()).getTypeName());
    }

    private static final ParameterNameResolver PARAMETER_NAME_RESOLVER = new ParameterNameResolver() {
        @Nonnull
        @Override
        public String resolveParameterName(@Nonnull Schema<?> parameterSchema, @Nonnull String apiParameterName) {
            return apiParameterName;
        }
    };
}
