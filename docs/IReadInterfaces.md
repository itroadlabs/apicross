# IRead* interfaces feature
This feature is for Hexagonal architecture purists.

Generated java classes for API models (mainly request/response models) actually belong to adapters.
It is not possible to use such models inside application "core", because adapters can use ports from application "core",
but not vise-versa.

With APICROSS it is possible to generate interface (a-la `IRead*` interface) for API models,
and locate these interfaces inside application "core" packages. Take a look at example:
```java
package com.myapp.ports.adapters.web;

...
@javax.annotation.Generated(value = "com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGenerator")
public class CreateMyResourceRepresentation implements IReadCreateMyResourceRepresentation {
    ...
}
```
```java
package com.myapp.application;
...
@javax.annotation.Generated(value = "com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGenerator")
public interface IReadCreateMyResourceRepresentation {
    String getSomeProperty();
    ...
}
```
```java
package com.myapp.ports.adapters.web;
...
@javax.annotation.Generated(value = "com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGenerator")
public interface MyApiHandler {
    @RequestMapping(path = "/my-resource", method = RequestMethod.POST, consumes = "application/json")
    ResponseEntity<?> createMyResource(@RequestBody(required = true) CreateMyResourceRepresentation model,
                             @RequestHeader HttpHeaders headers) throws Exception;
}
```

```java
package com.myapp.ports.adapters.web;
...
@RestController
public class MyApiHandlerController implements MyApyHandler {
    private final MyService myService;
    ...
    @Override
    public ResponseEntity<?> createMyResource(CreateMyResourceRepresentation model, HttpHeaders headers) throws Exception {
        myService.create(model);
        return ResponseEntity.status(204).build();
    }
}
```

```java
package com.myapp.application;
...
@Service
@Validated
public class MyService {
    public void create(@Valid IReadCreateMyResourceRepresentation model) {
        ...
    }
}
```

To enable this feature use following configuration:
```xml
    <generatorOptions implementation="com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGeneratorOptions">
        ...
        <apiHandlerPackage>com.myapp.ports.adapters.web</apiHandlerPackage>
        <apiModelPackage>com.myapp.ports.adapters.web</apiModelPackage>
        <enableDataModelReadInterfaces>true</enableDataModelReadInterfaces>
        <apiModelReadInterfacesPackage>com.myapp.application</apiModelReadInterfacesPackage>
        ...
    </generatorOptions>
```