package com.github.itroadlabs.oas.apicross.springmvc.params;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.Map;

public class ParamNameDataBinder extends ExtendedServletRequestDataBinder {
    private final Map<String, String> renameMapping;

    public ParamNameDataBinder(Object target, String objectName, Map<String, String> renameMapping) {
        super(target, objectName);
        this.renameMapping = renameMapping;
    }

    @Override
    protected void addBindValues(MutablePropertyValues valuesFromRequest, ServletRequest request) {
        super.addBindValues(valuesFromRequest, request);
        for (Map.Entry<String, String> entry : renameMapping.entrySet()) {
            String parameterName = entry.getKey();
            String propertyName = entry.getValue();
            if (valuesFromRequest.contains(parameterName)) {
                valuesFromRequest.add(propertyName, valuesFromRequest.getPropertyValue(parameterName).getValue());
            }
        }
    }
}
