package apicross.demo.common.utils;

import apicross.demo.common.models.ProblemDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
public class ExceptionsHandler {

    public static final String APPLICATION_PROBLEM_JSON_CHARSET_UTF_8 = "application/problem+json; charset=utf-8";
    private final ValidationErrorsFactory validationErrorsFactory = new ValidationErrorsFactory();

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ProblemDescription> handle(NoHandlerFoundException e, HttpServletRequest request) {
        return resourceNotFoundResponse(request);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ProblemDescription> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Map<String, Object> validationErrors = validationErrorsFactory.create(e);
        ProblemDescription problemDescription = new ProblemDescription()
                .withType("https://api.demoapp.com/problems/invalid-request-data")
                .withTitle("Invalid request's data")
                .withMessage("Some request body payload data, headers or query parameters have validation errors")
                .withHttpMethod(request.getMethod())
                .withInstance(request.getRequestURI())
                .addExtendedDescription("validationErrors", validationErrors);
        return toResponseEntity(problemDescription, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ProblemDescription> handle(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn(e.getMessage(), e);
        String problemMessage = ExceptionUtils.indexOfType(e, JsonProcessingException.class) >= 0 ? "Request body has unexpected format" : "Invalid or missed request body";
        ProblemDescription problem = new ProblemDescription()
                .withType("https://api.myapp.com/problems/malformed-request-body")
                .withHttpMethod(request.getMethod())
                .withInstance(request.getRequestURI())
                .withTitle("Malformed request body")
                .withMessage(problemMessage);
        return toResponseEntity(problem, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ProblemDescription> handleOthers(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        ProblemDescription problem = new ProblemDescription()
                .withType("https://api.myapp.com/problems/internal-server-error")
                .withHttpMethod(request.getMethod())
                .withInstance(request.getRequestURI())
                .withTitle("Internal server error");
        return toResponseEntity(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<ProblemDescription> resourceNotFoundResponse(HttpServletRequest request) {
        return resourceNotFoundResponse(request, null);
    }

    protected ResponseEntity<ProblemDescription> resourceNotFoundResponse(HttpServletRequest request, String message) {
        return toResponseEntity(new ProblemDescription()
                .withType("https://api.myapp.com/problems/resource-not-found")
                .withTitle("Resource not found")
                .withMessage(message)
                .withInstance(request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

    protected ResponseEntity<ProblemDescription> toResponseEntity(ProblemDescription problem, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus)
                .header("Content-Type", APPLICATION_PROBLEM_JSON_CHARSET_UTF_8)
                .body(problem.withHttpStatus(httpStatus.value()));
    }
}
