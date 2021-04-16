package com.github.itroadlabs.oas.apicross.java;

import com.github.itroadlabs.oas.apicross.java.DefaultPropertyAndParameterNameResolver;
import io.swagger.v3.oas.models.media.StringSchema;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultPropertyAndParameterNameResolverTest {
    private DefaultPropertyAndParameterNameResolver resolver;

    @BeforeEach
    public void setup() {
        resolver = new DefaultPropertyAndParameterNameResolver();
    }

    @Test
    public void validJavaIdentifiersResolvedForProperties() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolvePropertyName(stringProperty, "myProperty123");
        assertThat(resolvePropertyName, CoreMatchers.is("myProperty123"));
    }

    @Test
    public void underscoreResolvedForProperties() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolvePropertyName(stringProperty, "my_property_123");
        assertThat(resolvePropertyName, CoreMatchers.is("myProperty123"));
    }

    @Test
    public void noneJavaIdentifiersResolvedForProperties() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolvePropertyName(stringProperty, "123ab4~`!@#$%^&*()_+:;'\"|\\/<>");
        assertThat(resolvePropertyName, CoreMatchers.is("ab4$"));
    }

    @Test
    public void validJavaIdentifiersResolvedForParameters() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolveParameterName(stringProperty, "myProperty123");
        assertThat(resolvePropertyName, CoreMatchers.is("myProperty123"));
    }

    @Test
    public void underscoreResolvedForParameters() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolveParameterName(stringProperty, "my_property_123");
        assertThat(resolvePropertyName, CoreMatchers.is("myProperty123"));
    }

    @Test
    public void noneJavaIdentifiersResolvedForParameters() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolveParameterName(stringProperty, "123ab4~`!@#$%^&*()_+:;'\"|\\/<>");
        assertThat(resolvePropertyName, CoreMatchers.is("ab4$"));
    }
}
