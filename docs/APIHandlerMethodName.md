# API Handler method name

For Spring MVC code generation API Handler method names could be customized by following
plugin configuration option:

```xml
    <generatorOptions implementation="com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGeneratorOptions">
        ...
        <requestsHandlerMethodNameResolverClassName>com.myapp.godegen.MyMethodNameResolver</requestsHandlerMethodNameResolverClassName>
        ...
    </generatorOptions>
```

Here `com.myapp.godegen.MyMethodNameResolver` is a class which implements `com.github.itroadlabs.oas.apicross.core.handler.RequestsHandlerMethodNameResolver` interface.
Some standard implementations exists:

| Implementation class name | Description |
| ---- | ---- |
| `com.github.itroadlabs.oas.apicross.java.DefaultRequestsHandlerMethodNameResolver` | **This is default implementation**, which is used in case when no `requestsHandlerMethodNameResolverClassName` plugin configuration option defined. It makes method name on base of `operationId` (from Open API specification) and media-type consumed and produced by API operation. <br/> For example, if `operationId` is `myOperation` and operation consumes `application/json` then method name will be `myOperationConsumesJson`. When operation also produces `image/png` content then method name will be `myOperationConsumesJsonProducesImagePng`|
| `com.github.itroadlabs.oas.apicross.java.SimpleRequestsHandlerMethodNameResolver` | This implementation just uses `operationId` (adopted to Java identifiers legal characters) as a method name |

Lets imagine how `com.myapp.godegen.MyMethodNameResolver` could look. 
Take a look at following API specification snippet and implementation code below:
```yaml
    get:
      summary: List catalog items
      operationId: listCatalogItems
      tags:
        - CatalogContent
      responses:
        '200':
          description: Catalog items in the response body
          content:
            'application/vnd.eshop.v1+json':
              x-apicross: 
                operationIdSuffex: V1
              schema:
                $ref: '#/components/schemas/CatalogProductsListV1'
            'application/vnd.eshop.v2+json':
              x-apicross:
                operationIdSuffex: V2
              schema:
                $ref: '#/components/schemas/CatalogProductsListV2'
```

```java
package com.myapp.godegen;

import com.github.itroadlabs.oas.apicross.core.handler.RequestsHandlerMethodNameResolver;
import io.swagger.v3.oas.models.Operation;

public class MyMethodNameResolver implements RequestsHandlerMethodNameResolver {
    @Override
    public String resolve(@Nonnull Operation operation, @Nonnull String uriPath, String consumesMediaType, String producesMediaType) {
        String operationIdSuffix = lookupOperationIdSuffix(operation, producesMediaType);
        return StringToJavaIdentifierUtil.resolve(operation.getOperationId() + operationIdSuffix);
    }
    
    private String lookupOperationIdSuffix(Operation operation, String producesMediaType) {
        // implementation here
    }
}
```