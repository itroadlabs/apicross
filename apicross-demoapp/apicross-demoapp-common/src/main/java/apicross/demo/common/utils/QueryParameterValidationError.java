package apicross.demo.common.utils;

public class QueryParameterValidationError {
    private String parameterName;
    private Object invalidValue;
    private String message;

    public QueryParameterValidationError(String parameterName, Object invalidValue, String message) {
        this.parameterName = parameterName;
        this.invalidValue = invalidValue;
        this.message = message;
    }

    public String getParameterName() {
        return parameterName;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public String getMessage() {
        return message;
    }
}
