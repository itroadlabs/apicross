# Composed schemas
## allOf schema support
In case of following schema definition
```yaml
...
components:
  schemas:
    MyModel:
      allOf:
        - $ref: '#/components/schemas/Part'
        - type: object
          properties:
            c:
              type: string
            d:
              type: integer
              format: int32
          required:
            - c
    
    Part:
      type: object
      properties:
        a:
          type: string
        b:
          type: integer
          format: int32
      required:
        - a

```
Then java class representing `MyModel` will look like:
```java
public class MyModel {
    private String a;
    private JsonNullable<Integer> b;
    private String c;
    private JsonNullable<Integer> d;
    ...
}
```

## oneOf schemas support
In case of following schema definition
```yaml
components:
  schemas:
    MyModel:
      oneOf:
        - $ref: '#/components/schemas/Part1'
        - $ref: '#/components/schemas/Part2'
      discriminator:
        propertyName: type

    
    Part1:
      type: object
      properties:
        _type:
          type: string
        a:
          type: string
        b:
          type: integer
          format: int32
        flag:
          type: boolean
      required:
        - _type
        - a

    Part2:
      type: object
      properties:
        _type:
          type: string
        c:
          type: string
        d:
          type: integer
          format: int32
        flag:
          type: boolean
      required:
        - _type
        - c 
```
Then java class representing `MyModel` will look like
```java
import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Part1.class, name = "Part1"),
        @JsonSubTypes.Type(value = Part2.class, name = "Part2")
})
public abstract class MyModel {
    private JsonNullable<Boolean> flag;
    ...
} 

public class Part1 extends MyModel {
    private String a;
    private JsonNullable<Integer> b;
    ...
}

public class Part2 extends MyModel {
    private String c;
    private JsonNullable<Integer> d;
    ...
}
```
### Limitations
It is not possible to use anonymous `object` schemas definitions as a part of `oneOf` definition - you can compose schemas only via `$ref`.
For example for following schema definition source code generator will give an error:
```yaml
components:
  schemas:
    MyModel:
      oneOf:
        - $ref: '#/components/schemas/Part'
        - type: object
          properties:
            c:
              type: string
```
## anyOf schemas support
Currently APICROSS **does not support** `anyOf` schemas definition.