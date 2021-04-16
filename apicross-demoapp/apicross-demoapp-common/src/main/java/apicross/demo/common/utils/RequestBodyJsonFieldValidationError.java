package apicross.demo.common.utils;

public class RequestBodyJsonFieldValidationError {
    private String code;
    private String fieldPath;
    private Object invalidValue;
    private String message;

    public RequestBodyJsonFieldValidationError(String code, String fieldPath, Object invalidValue, String message) {
        this.code = code;
        this.fieldPath = fieldPath;
        this.invalidValue = invalidValue;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public String getMessage() {
        return message;
    }
}
