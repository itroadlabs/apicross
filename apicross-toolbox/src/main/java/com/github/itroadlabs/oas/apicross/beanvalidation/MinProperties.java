package com.github.itroadlabs.oas.apicross.beanvalidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {MinPropertiesValidator.class}
)
public @interface MinProperties {
    int value();

    String message() default "{com.github.itroadlabs.oas.apicross.beanvalidation.MinProperties.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
