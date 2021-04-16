package apicross.demo.common.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDescription {
    private String type;
    private String title;
    private String instance;
    private String httpMethod;
    private int httpStatus;
    private String message;
    private Map<String, Object> extendedDescription = new LinkedHashMap<>();

    public String getType() {
        return type;
    }

    public ProblemDescription withType(String type) {
        this.type = type;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ProblemDescription withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getInstance() {
        return instance;
    }

    public ProblemDescription withInstance(String instance) {
        this.instance = instance;
        return this;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public ProblemDescription withHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public ProblemDescription withHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ProblemDescription withMessage(String message) {
        this.message = message;
        return this;
    }

    @JsonAnyGetter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Map<String, Object> getExtendedDescription() {
        return extendedDescription;
    }

    public ProblemDescription addExtendedDescription(String name, Object value) {
        extendedDescription.put(name, value);
        return this;
    }
}
