package com.github.itroadlabs.oas.apicross.core.handler;

import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;
import com.github.itroadlabs.oas.apicross.core.handler.*;
import com.github.itroadlabs.oas.apicross.core.handler.model.*;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestsHandlersResolverTest {
    @Mock
    private HttpOperationsGroupsResolver grouper;
    @Mock
    private RequestsHandlerMethodsResolver requestsHandlerMethodsResolver;
    @Mock
    private RequestsHandlerTypeNameResolver requestsHandlerTypeNameResolver;
    @Mock
    private RequestsHandlerMethodNameResolver requestsHandlerMethodNameResolver;
    @Mock
    private ParameterNameResolver parameterNameResolver;

    @Test
    public void test() {
        String requestsHandlerOperationId = "getResource";
        PathItem.HttpMethod requestsHandlerOperationHttpMethod = PathItem.HttpMethod.GET;
        String requestsHandlerOperationProducesMediaType = "text/html";
        String requestsHandlerOperationUriPath = "/test";
        String requestsHandlerTypeName = "TestResourceController";
        Operation fixtureOperation = new Operation().operationId(requestsHandlerOperationId);

        when(grouper.resolve(any(Paths.class)))
                .thenAnswer((Answer<Collection<HttpOperationsGroup>>) invocationOnMock -> {
                    HttpOperationsGroup testTagGroup = new HttpOperationsGroup("TestTag");
                    testTagGroup.add(fixtureOperation, requestsHandlerOperationUriPath, requestsHandlerOperationHttpMethod);
                    return Collections.singleton(testTagGroup);
                });

        when(requestsHandlerMethodsResolver.resolve(eq(new HttpOperation(requestsHandlerOperationUriPath, requestsHandlerOperationHttpMethod, fixtureOperation)), eq(requestsHandlerMethodNameResolver), eq(parameterNameResolver)))
                .thenReturn(Collections.singletonList(new RequestsHandlerMethod()
                        .setHttpMethod(requestsHandlerOperationHttpMethod.name())
                        .setMethodName(requestsHandlerOperationId)
                        .setUriPath(requestsHandlerOperationUriPath)
                        .setOperation(fixtureOperation)
                        .setResponseBody(new MediaTypeContentModel(mock(DataModel.class), requestsHandlerOperationProducesMediaType))));

        when(requestsHandlerTypeNameResolver.resolve(any(HttpOperationsGroup.class))).thenReturn(requestsHandlerTypeName);

        RequestsHandlersResolver resolver = new RequestsHandlersResolver(grouper, requestsHandlerTypeNameResolver, requestsHandlerMethodNameResolver, requestsHandlerMethodsResolver, parameterNameResolver);

        List<RequestsHandler> handlers = resolver.resolve(new Paths());

        assertEquals(1, handlers.size());
        RequestsHandler handler = handlers.get(0);
        assertEquals(requestsHandlerTypeName, handler.getTypeName());
        assertEquals(1, handler.getMethods().size());
        RequestsHandlerMethod requestsHandlerMethod = handler.getMethods().get(0);
        assertEquals(requestsHandlerOperationId, requestsHandlerMethod.getMethodName());
        assertEquals(requestsHandlerOperationHttpMethod.name(), requestsHandlerMethod.getHttpMethod());
        assertEquals(requestsHandlerOperationProducesMediaType, requestsHandlerMethod.getResponseBody().getMediaType());
        assertEquals(requestsHandlerOperationUriPath, requestsHandlerMethod.getUriPath());
    }
}
