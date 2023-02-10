
![Java CI with Maven](https://github.com/itroadlabs/apicross/actions/workflows/maven-oss-release.yml/badge.svg)

# Purpose
APICROSS is a tool to generate source code from OpenAPI 3.0 API specification.

# Features
- Generates API Models with [Jackson](https://github.com/FasterXML/jackson) metadata and [JsonNullable Jackson module](https://github.com/OpenAPITools/jackson-databind-nullable) for [optional fields](docs/OptionalFields.md). 
  Supports following tricky OpenAPI models features:
  * `additionalProperties` (See [here](docs/AdditionalProperties.md))
  * `oneOf`, `allOf`  (See [here](docs/ComposedSchemas.md))
  * `minProperties`, `maxProperties` and `required` properties [validation support](docs/MinMaxRequiredPropertiesValidation.md).  
- Generates API Requests [Handlers](docs/APIHandler.md) (as interfaces for [Spring Web MVC](https://github.com/spring-projects/spring-framework/tree/main/spring-webmvc) controllers):
  * Basic Spring Security [support](docs/SpringSecurityAuthentication.md)
  * Query [parameters](docs/QueryParameters.md) objects
  * `IRead*` interfaces to support Hexagonal architecture (See [here](docs/IReadInterfaces.md))
- Files upload/download support (See [here](docs/HandlingFiles.md))
- Customization:
  * Customize API handler method names (See [here](docs/APIHandlerMethodName.md))
  * Customize Java class property name for model fields 
  * Grouping operations into API Handlers  

# Minimal Maven Plugin setup
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>io.github.itroadlabs</groupId>
                <artifactId>apicross-maven-plugin</artifactId>
                <version>${project.version}</version>
                <executions>
                    <execution>
                        <id>generate-api-classes</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate-code</goal>
                        </goals>
                        <configuration>
                            <specUrl>file:///${project.basedir}/../api-specifications/api.yaml</specUrl>
                            <generatorClassName>io.github.itroadlabs.apicross.springmvc.SpringMvcCodeGenerator</generatorClassName>
                            <generatorOptions implementation="io.github.itroadlabs.apicross.springmvc.SpringMvcCodeGeneratorOptions">
                                <apiHandlerPackage>com.myapp.web.handlers</apiHandlerPackage>
                                <apiModelPackage>com.myapp.web.models</apiModelPackage>
                                <writeSourcesTo>${project.build.directory}/generated-sources/java</writeSourcesTo>
                            </generatorOptions>
                        </configuration>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>io.github.itroadlabs</groupId>
                        <artifactId>apicross-springmvc</artifactId>
                        <version>${project.version}</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```
## Maven Plugin Configuration Options
### Common options
| Option             | Type | Description |
|--------------------| ---- | --- |
| writeSourcesTo     | String | Directory to write generated sources |
| generateOnlyModels | boolean | Generated  only API models (if `true`) but not requests handlers. Default - `false` |
| skipTags           | Set | Set of OpenAPI tags to be skipped from source code generation. Operations with such tags will be skipped within code generation |
| generateOnlyTags   | Set | Set of OpenAPI tags to be included int source code generation. Only operations with such tags will be included into code generation | 

### Spring MVC generator options
| Option                                     | Type | Description                                                                                                                                                                                                                                                                                                               |
|--------------------------------------------| ---- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| apiHandlerPackage                          | String | Java package name for API handlers (Java interfaces for Spring MVC Controllers)                                                                                                                                                                                                                                           |
| apiModelPackage                            | String | Java package name for API models (request/response)                                                                                                                                                                                                                                                                       |
| modelClassNameSuffix                       | String | Suffix for API model Java class name                                                                                                                                                                                                                                                                                      |
| modelClassNamePrefix                       | String | Prefix for API model Java class name                                                                                                                                                                                                                                                                                      |
| requestsHandlerMethodNameResolverClassName | String | Class implementing `RequestsHandlerMethodNameResolver` interface to resolve API Handler class name. See details [here](docs/APIHandlerMethodName.md). Default is [DefaultRequestsHandlerMethodNameResolver](apicross-java/src/main/java/io/github/itroadlabs/apicross/java/DefaultRequestsHandlerMethodNameResolver.java) |
| requestsHandlerTypeNameResolverClassName   | String | Class implementing `RequestsHandlerTypeNameResolver` interface to resolve API Handler method name. Default is [DefaultRequestsHandlerTypeNameResolver](apicross-java/src/main/java/io/github/itroadlabs/apicross/java/DefaultRequestsHandlerTypeNameResolver.java)                                                        |
| propertyNameResolverClassName              | String | Class implementing `PropertyNameResolver` interface to resolve data model class property name. Default is [DefaultPropertyAndParameterNameResolver](apicross-java/src/main/java/io/github/itroadlabs/apicross/java/DefaultPropertyAndParameterNameResolver.java)                                                          |
| parameterNameResolverClassName             | String | Class implementing `PropertyNameResolver` interface to resolve URI parameter name. Default is [DefaultPropertyAndParameterNameResolver](apicross-java/src/main/java/io/github/itroadlabs/apicross/java/DefaultPropertyAndParameterNameResolver.java)                                                                      |
| dataModelsExternalTypesMap                 | Map | Defines replacement for data model schema by specified Java class. See details [here](docs/ExternalTypes.md)                                                                                                                                                                                                              |
| dataModelsInterfacesMap                    | Map | Defines Java interface for class generated to represend specified schema. See details [here](docs/DataModelInterfaces.md)                                                                                                                                                                                                 |
| queryObjectsInterfacesMap                  | Map | Java interfaces to be implemented for specified query object. See details [here](docs/QueryParameters.md)                                                                                                                                                                                                                 |
| globalQueryObjectsInterfaces               | Set | Java interfaces to be implemented for all generated query opject classes. See details [here](docs/QueryParameters.md).                                                                                                                                                                                                    |
| useJsonNullable                            | boolean | Generate Java classes for API models with `JsonNullable` class usage (`true`) or not (`false`). The default is `true`. [Feature description here](docs/OptionalFields.md)                                                                                                                                                 |
| enableApicrossJavaBeanValidationSupport    | boolean | Enable option to add APICROSS custom annotation to the api model class to perform `required` fields validation. Default is `false`. See details [here](docs/MinMaxRequiredPropertiesValidation.md).                                                                                                                       |
| enableDataModelReadInterfaces              | boolean | Enable generation for `IRead*` interfaces. Default is `false`.                                                                                                                                                                                                                                                            |
| apiModelReadInterfacesPackage              | String | Java package name for `IRead*` interfaces                                                                                                                                                                                                                                                                                 |
| enableSpringSecurityAuthPrincipal          | boolean | Enable for API Handlers methods Spring Security `Authorization` parameter                                                                                                                                                                                                                                                 |
| alternativeTemplatesPath                   | List | Path to files with alternative templates. Please read about feature [here](docs/AlternativeTemplates.md)                                                                                                                                                                                                                  |
