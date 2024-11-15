package io.github.itroadlabs.apicross.beanvalidation.springmvc.params;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import java.util.Map;

public class ParamNameDataBinder extends ExtendedServletRequestDataBinder {
    private final Map<String, String> renameMapping;

    public ParamNameDataBinder(Object target, String objectName, Map<String, String> renameMapping) {
        super(target, objectName);
        this.renameMapping = renameMapping;
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, jakarta.servlet.ServletRequest request) {
        super.addBindValues(mpvs, request);
        for (Map.Entry<String, String> entry : renameMapping.entrySet()) {
            String parameterName = entry.getKey();
            String propertyName = entry.getValue();
            if (mpvs.contains(parameterName)) {
                mpvs.add(propertyName, mpvs.getPropertyValue(parameterName).getValue());
            }
        }
    }
}
