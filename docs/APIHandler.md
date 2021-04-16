#API handler
API Handler is an object handling API requests. For SpringWebMVC - handlers are just `@Controller` classes.

APICROSS generates Java interfaces with spring MVC metadata. So application's `@Controller`s have to implement these interfaces.
Consider API specification fragment bellow:
```yaml
  '/my':
    post:
      operationId: create
      tags:
        - MyApi
      requestBody:
        required: true
        content:
          'application/json':
            schema:
              $ref: '#/components/schemas/CreateMyModelRepresentation'
      responses:
        '201':
          description: Success. Resource created.
  '/my/{id}':
    get:
      operationId: get
      tags:
        - MyApi
      parameters:
        - $ref: '#/components/parameters/IdPathParameter'
      responses:
        '200':
          description: Success. Resource representation in the response body.
          content:
            'application/json':
              schema:
                $ref: '#/components/schemas/MyModelRepresentation'          
          
```
Generated interface will looks like:
```java
public interface MyApiHandler  {
    @RequestMapping(path = "/my", method = RequestMethod.POST, consumes = "application/json")
    ResponseEntity<Void> create(@RequestBody(required = true) CreateMyModelRepresentation model, 
                                                        @RequestHeader HttpHeaders headers) throws Exception;

    @RequestMapping(path = "/my/{id}", method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<MyModelRepresentation> get(@PathVariable("id") String id, 
                                                        @RequestHeader HttpHeaders headers) throws Exception;
}
```

and Spring MVC controller (manually coded):
```java
@RestController
class MyApiHandlerController implements MyApiHandler {
    private final MyAppService myAppService;
    
    @Autowired
    MyApiHandlerController(MyAppService myAppService) {
        this.myAppService = myAppService;
    }

    @Override
    public ResponseEntity<Void> create(CreateMyModelRepresentation model, HttpHeaders headers) {
        CreateMyModelResult result = myAppService.create(new CreateModelCommand(model));
        return created(result);
    }

    @Override
    public ResponseEntity<MyModelRepresentation> get(String id, HttpHeaders headers) {
        ModelObject model = myAppService.get(id);
        return ResponseEntity.ok(MyModelRepresentationFactory.create(model));
    }
}
```