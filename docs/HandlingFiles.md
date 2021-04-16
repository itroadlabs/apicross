# Downloading files

Take a look at the following API specification snippet:
```yaml
  '/pictures/{pictureId}':
    parameters:
      - $ref: '#/components/parameters/PictureId'
    get:
      operationId: getPicturePyId
      tags:
        - Pictures
      responses:
        '200':
          content:
            'image/jpeg':
              schema:
                $ref: '#/components/schemas/BinaryContent'
    ...
    
    BinaryContent:
      type: string
      format: binary
```
Then resulting Spring MVC controller methods will look like:
```java
  @RequestMapping(
      method = RequestMethod.GET,
      path = "/pictures/{pictureId}",
      produces = "image/jpeg")
  ResponseEntity<org.springframework.core.io.InputStreamResource> getPicturePyIdProduceImageJpeg(
      @PathVariable("pictureId") String pictureId,
      @RequestHeader HttpHeaders headers) throws Exception;
```

# Uploading files
Let's take a following API specification snippet:
```yaml
  '/pictures':
    post:
      operationId: uploadImage
      requestBody:
        required: true
        content:
          'image/jpeg':
            schema:
              $ref: '#/components/schemas/BinaryContent'
```
Then resulting Spring MVC controller methods will look like:
```java
  @RequestMapping(
      method = RequestMethod.POST,
      path = "/pictures",
      consumes = "image/jpeg")
  ResponseEntity<?> uploadImageConsumeImageJpeg(
      @RequestHeader HttpHeaders headers,
      @RequestBody(required = true) java.io.InputStream requestBody) throws Exception;
```