# Additional properties
Take a look at following model schema:
```yaml
MyModel:
    type: object
    additionalProperties:
      type: string
```
Java class representing that model will be:
```java
public class MyModel {
    private final Map<String, String> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, String> getAdditionalProperties() {
        return this.additionalProperties;
    }
    
    @JsonAnySetter
    public void setAdditionalProperty(String name, String value) {
        this.additionalProperties.put(name, value);
    }
}
```