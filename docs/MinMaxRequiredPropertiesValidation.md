# Min/Max/Required properties validation
Validating such features within java code is tricky because JSON and Java are different.
OpenAPI specification states:
- minProperties - minimum number of fields must present in JSON document,
- maxProperties - maximum number of fields must present in JSON document,
- required - such fields must present in JSON document (it doesn't matter what value fields have, nulls or not).

After JSON deserialization into Java object there is no information about field's presence in the JSON document.
APICROSS by default represents optional schema fields as `JsonNullable`.
To validate such data models there is a simple JSR-380 toolkit.
Take a look at the `com.github.itroadlabs.oas.apicross.beanvalidation.*` classes within `apicross-support` module.

Bellow is an example of generated code:
```java
@MinProperties(
        value = 2,
        groups = {BeanPropertiesValidationGroup.class})
@MaxProperties(
        value = 3,
        groups = {BeanPropertiesValidationGroup.class})
@RequiredProperties(
        value = {"a"},
        groups = {BeanPropertiesValidationGroup.class})
public class MyModel implements HasPopulatedProperties {
    private final Set<String> $populatedProperties = new HashSet<>();

    private JsonNullable<String> a = JsonNullable.undefined();
    private JsonNullable<String> b = JsonNullable.undefined();
    private JsonNullable<String> c = JsonNullable.undefined();
    private JsonNullable<String> d = JsonNullable.undefined();
    ...

    @JsonSetter("a")
    public void setA(String a) {
        this.$populatedProperties.add("a");
        this.a = JsonNullable.of(a);
    }
    
    ...
    
    @Override
    public Set<String> get$populatedProperties() {
        return Collections.unmodifiableSet(this.$populatedProperties);
    }
}
```
Validation group above can be used for validation sequence needs.

To enable/disable this feature use following configuration option:
```xml
    <generatorOptions implementation="com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGeneratorOptions">
        ...
        <enableApicrossJavaBeanValidationSupport>true</enableApicrossJavaBeanValidationSupport>
        ...
    </generatorOptions>
```
By default this feature is disabled.