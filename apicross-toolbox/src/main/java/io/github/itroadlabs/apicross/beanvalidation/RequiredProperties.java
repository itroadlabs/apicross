package io.github.itroadlabs.apicross.beanvalidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {RequiredPropertiesValidator.class}
)
public @interface RequiredProperties {
    String[] value();

    String message() default "{io.github.itroadlabs.apicross.beanvalidation.RequiredProperties.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
