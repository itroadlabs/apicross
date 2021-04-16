# Spring Security Authentication parameter
When API operation definition contains security option, then it is possible to add `Authentication` parameter to the
API Handler interface. For example, take a look at following specification:
```yaml
  '/my-resource':
    patch:
      operationId: updateMyResource
      requestBody:
        required: true
        content:
          'application/json':
            schema:
              $ref: '#/components/schemas/MyModel'
      security:
        - Basic: [ ]
```
Then API Handler interface will look like:
```java
  ...
  @RequestMapping(
      method = RequestMethod.PATCH,
      path = "/my-resource",
      consumes = "application/json")
  ResponseEntity<?> updateMyResource(
      @RequestHeader HttpHeaders headers,
      @CurrentSecurityContext(expression = "authentication") Authentication authentication,
      @RequestBody(required = true) MyModel requestBody) throws Exception;
```
To enable this option use following configuration option:
```xml
    <generatorOptions implementation="com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGeneratorOptions">
        ...
        <enableSpringSecurityAuthPrincipal>true</enableSpringSecurityAuthPrincipal>
        ...
    </generatorOptions>
```