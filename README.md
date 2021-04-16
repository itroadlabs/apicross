[![Build Status](https://travis-ci.com/kasseopy/apicross.svg?branch=master)](https://travis-ci.com/kasseopy/apicross)

# Purpose
APICROSS is a tool to generate source code from OpenAPI 3.0 API specification.

# Features
- Generates API Models with [Jackson](https://github.com/FasterXML/jackson) metadata and [JsonNullable Jackson module](https://github.com/OpenAPITools/jackson-databind-nullable) for [optional fields](docs/OptionalFields.md). 
  Supports following tricky OpenAPI models features:
  * `additionalProperties` (See [here](docs/AdditionalProperties.md))
  * `oneOf`, `allOf`  (See [here](docs/OneOfAllOf.md))
  * `minProperties`, `maxProperties` and `required` properties [validation support](docs/MinMaxRequiredPropertiesValidation.md).  
- Generates API Requests [Handlers](docs/APIHandler.md) (as interfaces for [Spring Web MVC](https://github.com/spring-projects/spring-framework/tree/main/spring-webmvc) controllers):
  * Basic Spring Security [support](docs/SpringSecurityAuthentication.md)
  * Query parameters objects
  * `IRead*` interfaces to support Hexagonal architecture (See [here](docs/IReadInterfaces.md))
- Files upload/download support  
- HATEOAS/Hypermedia support (Planned)  
- Customization:
  * Customize API handler method names (See [here](docs/APIHandlerMethodName.md))
  * Customize Java class property name for model fields 
  * Grouping operations into API Handlers  

# Minimal Maven Plugin setup
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.itroadlabs.oas</groupId>
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
                            <generatorClassName>com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGenerator</generatorClassName>
                            <generatorOptions implementation="com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGeneratorOptions">
                                <apiHandlerPackage>com.myapp.web.handlers</apiHandlerPackage>
                                <apiModelPackage>com.myapp.web.models</apiModelPackage>
                                <writeSourcesTo>${project.build.directory}/generated-sources/java</writeSourcesTo>
                            </generatorOptions>
                        </configuration>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>com.github.itroadlabs.oas</groupId>
                        <artifactId>apicross-springmvc</artifactId>
                        <version>${project.version}</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```
## Maven Plugin Configuration Options

| Generator option | Type | Description |
| ---- | ---- | --- |
| apiHandlerPackage | String | Java package name for API handlers (Java interfaces for Spring MVC Controllers) |
| apiModelPackage | String | Java package name for API models (request/response) |
| modelClassNameSuffix | String | Suffix for API model Java class name |
| modelClassNamePrefix | String | Prefix for API model Java class name  |
| requestsHandlerMethodNameResolverClassName | String | |
| requestsHandlerTypeNameResolverClassName | String | |
| propertyNameResolverClassName | String | |
| parameterNameResolverClassName | String | |
| dataModelsExternalTypesMap | Map | TBD... |
| dataModelsInterfacesMap | Map | TBD...  |
| queryObjectsInterfacesMap | Map | TBD...  |
| globalQueryObjectsInterfaces | Set | TBD... |
| useJsonNullable | boolean | Generate Java classes for API models with `JsonNullable` class usage (`true`) or not (`false`). [Feature description here](docs/OptionalFields.md)  |
| enableApicrossJavaBeanValidationSupport | boolean | TBD... |
| enableDataModelReadInterfaces | boolean | Enable generation for `IRead*` interfaces |
| apiModelReadInterfacesPackage | String | Java package name for `IRead*` interfaces |
| enableSpringSecurityAuthPrincipal | boolean | Enable for API Handlers methods Spring Security `Authorization` parameter |
| alternativeTemplatesPath | List | TBD... |