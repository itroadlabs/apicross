package com.github.itroadlabs.oas.apicross.beanvalidation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class MinPropertiesValidator implements ConstraintValidator<MinProperties, HasSpecifiedProperties> {
    private int minProperties;

    @Override
    public void initialize(MinProperties constraintAnnotation) {
        this.minProperties = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(HasSpecifiedProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Set<String> specifiedProperties = new HashSet<>(value.$specifiedProperties());
        if (specifiedProperties.size() < minProperties) {
            HibernateConstraintValidatorContext hibernateConstraintValidatorContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateConstraintValidatorContext.addMessageParameter("minProperties", minProperties);
            hibernateConstraintValidatorContext.addMessageParameter("specifiedProperties", specifiedProperties);
            return false;
        } else {
            return true;
        }
    }
}
