package apicross.demo.common.utils;

import com.github.itroadlabs.oas.apicross.beanvalidation.MaxProperties;
import com.github.itroadlabs.oas.apicross.beanvalidation.MinProperties;
import com.github.itroadlabs.oas.apicross.beanvalidation.RequiredProperties;
import apicross.demo.common.models.QueryObjectMarker;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.lang.annotation.Annotation;
import java.util.*;

public class ValidationErrorsFactory {
    public Map<String, Object> create(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<ConstraintViolation<?>> queryObjectViolations = new ArrayList<>();
        List<ConstraintViolation<?>> commandObjectViolations = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            if (isQueryObjectViolation(constraintViolation)) {
                queryObjectViolations.add(constraintViolation);
            } else {
                commandObjectViolations.add(constraintViolation);
            }
        }
        Map<String, Object> validationErrors = new LinkedHashMap<>();
        if (!commandObjectViolations.isEmpty()) {
            validationErrors.put("requestBody", collectRequestBodyValidationErrors(commandObjectViolations));
        }
        if (!queryObjectViolations.isEmpty()) {
            validationErrors.put("queryParameters", collectQueryParametersValidationErrors(queryObjectViolations));
        }
        return validationErrors;
    }

    private boolean isQueryObjectViolation(ConstraintViolation<?> constraintViolation) {
        return QueryObjectMarker.class.isAssignableFrom(constraintViolation.getLeafBean().getClass());
    }

    private List<RequestBodyJsonFieldValidationError> collectRequestBodyValidationErrors(List<ConstraintViolation<?>> violations) {
        List<RequestBodyJsonFieldValidationError> result = new ArrayList<>();
        for (ConstraintViolation<?> violation : violations) {
            String code = detectCode(violation);
            result.add(new RequestBodyJsonFieldValidationError(code, prepareJsonPath(violation.getPropertyPath()),
                    violation.getInvalidValue(), violation.getMessage()));
        }
        return result;
    }

    private String detectCode(ConstraintViolation<?> violation) {
        Class<? extends Annotation> annotationType = violation.getConstraintDescriptor().getAnnotation().annotationType();
        if (annotationType.equals(RequiredProperties.class)) {
            return "MissingRequiredFields";
        } else if (annotationType.equals(MinProperties.class)) {
            return "TooFewFields";
        } else if (annotationType.equals(MaxProperties.class)) {
            return "TooManyFields";
        } else {
            return "InvalidValue";
        }
    }

    private List<QueryParameterValidationError> collectQueryParametersValidationErrors(List<ConstraintViolation<?>> violations) {
        List<QueryParameterValidationError> result = new ArrayList<>();
        for (ConstraintViolation<?> violation : violations) {
            result.add(new QueryParameterValidationError(prepareQueryObjectPropertyPath(violation.getPropertyPath()),
                    violation.getInvalidValue(), violation.getMessage()));
        }
        return result;
    }

    private String prepareJsonPath(Path jsr380propertyPath) {
        Iterator<Path.Node> pathNodes = jsr380propertyPath.iterator();
        StringBuilder jsonPath = new StringBuilder("$");
        return collectPath(pathNodes, jsonPath);
    }

    private String prepareQueryObjectPropertyPath(Path jsr380propertyPath) {
        Iterator<Path.Node> pathNodes = jsr380propertyPath.iterator();
        StringBuilder propertyPath = new StringBuilder();
        return collectPath(pathNodes, propertyPath);
    }

    private String collectPath(Iterator<Path.Node> pathNodes, StringBuilder pathStringBuilder) {
        int i = 0;
        while (pathNodes.hasNext()) {
            Path.Node node = pathNodes.next();
            if (node.getKind() == ElementKind.PROPERTY) {
                pathStringBuilder.append(".").append(node.toString());
            }
        }
        return pathStringBuilder.toString();
    }
}
