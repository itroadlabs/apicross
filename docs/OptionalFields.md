# Optional fields
By default, OpenAPI specification states all model fields are not nullable (`nullable: false`).
Required fields are fields must be present in a valid JSON  document representing API data models (request/response payloads).
So field may be not required to be in JSON document, but it's value must not be null. For example, consider following schema
```yaml
MyModel:
    type: object
    properties:
        a: string
```
Then JSON document like
```json
{
  "a": null
}
```
is not vallid, but following JSON document is valid:
```json
{}
```
And following JSON is valid:
```json
{
  "a": "abc"
}
```

Optional fields (which are not required) become `JsonNullable<Type>` fields for internal data model and Jackson serialization/deserialization.
For example, for the schema above generated Java class will be:
```java
public class MyModel {
    private JsonNullable<String> a = JsonNullable.undefined();

    @JsonIgnore
    public String getA() throws NoSuchElementException {
        return this.a.get();
    }

    @JsonGetter("a")
    protected JsonNullable<String> aJson() {
        return this.a;
    }

    @JsonSetter("a")
    public void setA(String a) {
        this.a = JsonNullable.of(a);
    }
}
```

It is possible to disable `JsonNullable` usage by maven plugin configuration option:
```xml
    <generatorOptions implementation="com.github.itroadlabs.oas.apicross.springmvc.SpringMvcCodeGeneratorOptions">
        ...
        <useJsonNullable>false</useJsonNullable>
        ...
    </generatorOptions>
```