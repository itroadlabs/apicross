package com.github.itroadlabs.oas.apicross.beanvalidation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeanPropertiesValidationTests {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void whenBeanHasPropertiesDefinedLessThenMinProperties_thenViolationsOccurs() {
        MinMaxPropertiesTestBean testBean = new MinMaxPropertiesTestBean();
        Set<ConstraintViolation<MinMaxPropertiesTestBean>> violations = validator.validate(testBean);
        assertEquals(1, violations.size());
    }

    @Test
    public void whenBeanHasPropertiesDefinedGreaterThenMaxProperties_thenViolationsOccurs() {
        MinMaxPropertiesTestBean testBean = new MinMaxPropertiesTestBean();
        testBean.setA("a");
        testBean.setB("b");
        testBean.setC("c");
        Set<ConstraintViolation<MinMaxPropertiesTestBean>> violations = validator.validate(testBean);
        assertEquals(1, violations.size());
    }

    @Test
    public void whenBeanValid_thenNoViolationsOccurs() {
        MinMaxPropertiesTestBean testBean = new MinMaxPropertiesTestBean();
        testBean.setA("a");
        testBean.setB("b");
        Set<ConstraintViolation<MinMaxPropertiesTestBean>> violations = validator.validate(testBean);
        assertEquals(0, violations.size());
    }

    @Test
    public void whenBeanHasNoRequiredPropertiesSpecified_thenViolationsOccurs() {
        RequiredPropertiesTestBean testBean = new RequiredPropertiesTestBean();
        testBean.setA("a");
        Set<ConstraintViolation<RequiredPropertiesTestBean>> violations = validator.validate(testBean);
        assertEquals(1, violations.size());
    }

    @Test
    public void whenBenHasRequiredProperties_thenNoViolationsOccurs() {
        RequiredPropertiesTestBean testBean = new RequiredPropertiesTestBean();
        testBean.setA("a");
        testBean.setB("b");
        Set<ConstraintViolation<RequiredPropertiesTestBean>> violations = validator.validate(testBean);
        assertEquals(0, violations.size());
    }
}
