package com.github.itroadlabs.oas.apicross.springmvc.params;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping(path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TestParamsObject> test(TestParamsObject paramsObject) {
        return ResponseEntity.ok(paramsObject);
    }
}
