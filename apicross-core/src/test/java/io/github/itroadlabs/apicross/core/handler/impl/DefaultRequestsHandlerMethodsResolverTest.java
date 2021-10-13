package io.github.itroadlabs.apicross.core.handler.impl;

import io.github.itroadlabs.apicross.core.data.DataModelResolver;
import io.github.itroadlabs.apicross.core.data.PropertyNameResolver;
import io.github.itroadlabs.apicross.core.data.model.DataModel;
import io.github.itroadlabs.apicross.core.data.model.ObjectDataModel;
import io.github.itroadlabs.apicross.core.data.model.PrimitiveDataModel;
import io.github.itroadlabs.apicross.core.handler.ParameterNameResolver;
import io.github.itroadlabs.apicross.core.handler.RequestsHandlerMethodNameResolver;
import io.github.itroadlabs.apicross.core.handler.model.HttpOperation;
import io.github.itroadlabs.apicross.core.handler.model.RequestQueryParameter;
import io.github.itroadlabs.apicross.core.handler.model.RequestsHandlerMethod;
import io.github.itroadlabs.apicross.utils.OpenApiComponentsIndex;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @Mock
    private OpenApiComponentsIndex openApiComponentsIndex;

    @Test
    public void resolveMethods() {
        when(methodNameResolver.resolve(any(Operation.class), anyString(), anyString(), anyString()))
                .thenAnswer(invocationOnMock -> ((Operation) invocationOnMock.getArgument(0)).getOperationId());
        resolver = new DefaultRequestsHandlerMethodsResolver(operationRequestAndResponseResolver, dataModelResolver);

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
        assertEquals("TestRequest", ((ObjectDataModel) requestsHandlerMethod.getRequestBody().getContent()).getTypeName());
        assertEquals("TestResponse", ((ObjectDataModel) requestsHandlerMethod.getResponseBody().getContent()).getTypeName());
    }

    @Test
    public void resolveJsonEncodedQueryParameters() {
        resolver = new DefaultRequestsHandlerMethodsResolver(new DefaultOperationRequestAndResponseResolver(openApiComponentsIndex), new DataModelResolver(openApiComponentsIndex, propertyNameResolver()));

        Operation operation = new Operation()
                .operationId("test")
                .addParametersItem(new QueryParameter()
                        .name("filter")
                        .required(true)
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new ObjectSchema()
                                                .$ref("#/components/schemas/QueryFilterType")))))
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse()));

        when(openApiComponentsIndex.schemaBy$ref("#/components/schemas/QueryFilterType")).thenReturn(
                new ObjectSchema()
                        .name("QueryFilterType")
                        .addProperties("q", new StringSchema()));

        List<RequestsHandlerMethod> methods = resolver.resolve(new HttpOperation("/test", PathItem.HttpMethod.GET, operation), methodNameResolver(), parameterNameResolver());

        assertEquals(1, methods.size());
        RequestsHandlerMethod requestsHandlerMethod = methods.get(0);
        Collection<RequestQueryParameter> queryParameters = requestsHandlerMethod.getQueryParameters();
        assertEquals(1, queryParameters.size());
        RequestQueryParameter queryParameter = queryParameters.iterator().next();
        assertTrue(queryParameter.isJsonEncoding());
        DataModel parameterType = queryParameter.getType();
        assertTrue(parameterType instanceof ObjectDataModel);
        assertEquals("QueryFilterType", ((ObjectDataModel) parameterType).getTypeName());
        assertEquals(1, ((ObjectDataModel) parameterType).getProperties().size());
        assertTrue(((ObjectDataModel) parameterType).getProperty("q").getType() instanceof PrimitiveDataModel);
    }

    @Test
    public void resolveInlinedJsonEncodedQueryParameters() {
        resolver = new DefaultRequestsHandlerMethodsResolver(new DefaultOperationRequestAndResponseResolver(openApiComponentsIndex), new DataModelResolver(openApiComponentsIndex, propertyNameResolver()));

        Operation operation = new Operation()
                .operationId("test")
                .addParametersItem(new QueryParameter()
                        .name("filter")
                        .required(true)
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new ObjectSchema()
                                                .addProperties("q", new StringSchema())))))
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse()));

        List<RequestsHandlerMethod> methods = resolver.resolve(new HttpOperation("/test", PathItem.HttpMethod.GET, operation), methodNameResolver(), parameterNameResolver());

        assertEquals(1, methods.size());
        RequestsHandlerMethod requestsHandlerMethod = methods.get(0);
        Collection<RequestQueryParameter> queryParameters = requestsHandlerMethod.getQueryParameters();
        assertEquals(1, queryParameters.size());
        RequestQueryParameter queryParameter = queryParameters.iterator().next();
        assertTrue(queryParameter.isJsonEncoding());
        DataModel parameterType = queryParameter.getType();
        assertTrue(parameterType instanceof ObjectDataModel);
        assertEquals(1, ((ObjectDataModel) parameterType).getProperties().size());
        assertTrue(((ObjectDataModel) parameterType).getProperty("q").getType() instanceof PrimitiveDataModel);
    }

    private PropertyNameResolver propertyNameResolver() {
        return new PropertyNameResolver() {
            @Nonnull
            @Override
            public String resolvePropertyName(@Nonnull Schema<?> propertySchema, @Nonnull String apiPropertyName) {
                return apiPropertyName;
            }
        };
    }

    private ParameterNameResolver parameterNameResolver() {
        return new ParameterNameResolver() {
            @Nonnull
            @Override
            public String resolveParameterName(@Nonnull Schema<?> parameterSchema, @Nonnull String apiParameterName) {
                return apiParameterName;
            }
        };
    }

    private RequestsHandlerMethodNameResolver methodNameResolver() {
        return new RequestsHandlerMethodNameResolver() {
            @Nonnull
            @Override
            public String resolve(@Nonnull Operation operation, @Nonnull String uriPath, String consumesMediaType, String producesMediaType) {
                return operation.getOperationId();
            }
        };
    }
}
