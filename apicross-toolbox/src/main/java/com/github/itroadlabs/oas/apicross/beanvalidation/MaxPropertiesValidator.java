package com.github.itroadlabs.oas.apicross.beanvalidation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class MaxPropertiesValidator implements ConstraintValidator<MaxProperties, HasSpecifiedProperties> {
    private int maxProperties;

    @Override
    public void initialize(MaxProperties constraintAnnotation) {
        this.maxProperties = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(HasSpecifiedProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Set<String> specifiedProperties = new HashSet<>(value.$specifiedProperties());
        if (specifiedProperties.size() > maxProperties) {
            HibernateConstraintValidatorContext hibernateConstraintValidatorContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateConstraintValidatorContext.addMessageParameter("maxProperties", maxProperties);
            hibernateConstraintValidatorContext.addMessageParameter("specifiedProperties", specifiedProperties);
            return false;
        } else {
            return true;
        }
    }
}
