package com.github.itroadlabs.oas.apicross.beanvalidation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RequiredPropertiesValidator implements ConstraintValidator<RequiredProperties, HasSpecifiedProperties> {
    private String[] requiredProperties;

    @Override
    public void initialize(RequiredProperties constraintAnnotation) {
        this.requiredProperties = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(HasSpecifiedProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Set<String> specifiedProperties = new HashSet<>(value.$specifiedProperties());
        Set<String> unspecifiedProperties = new HashSet<>(Arrays.asList(this.requiredProperties));
        unspecifiedProperties.removeAll(specifiedProperties);
        if (!unspecifiedProperties.isEmpty()) {
            HibernateConstraintValidatorContext hibernateConstraintValidatorContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateConstraintValidatorContext.addMessageParameter("requiredProperties", this.requiredProperties);
            hibernateConstraintValidatorContext.addMessageParameter("specifiedProperties", specifiedProperties);
            return false;
        } else {
            return true;
        }
    }
}
