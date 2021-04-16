package com.github.itroadlabs.oas.apicross.java;

import com.github.itroadlabs.oas.apicross.java.StringToJavaIdentifierUtil;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class StringToJavaIdentifierUtilTest {
    @Test
    public void validJavaIdentifiersResolvedForProperties() {
        String resolvePropertyName = StringToJavaIdentifierUtil.resolve("myProperty123");
        assertThat(resolvePropertyName, CoreMatchers.is("myProperty123"));
    }

    @Test
    public void underscoreResolvedForProperties() {
        String resolvePropertyName = StringToJavaIdentifierUtil.resolve("my_property_123");
        assertThat(resolvePropertyName, CoreMatchers.is("myProperty123"));
    }

    @Test
    public void noneJavaIdentifiersResolvedForProperties() {
        String resolvePropertyName = StringToJavaIdentifierUtil.resolve("123ab4~`!@#$%^&*()_+:;'\"|\\/<>");
        assertThat(resolvePropertyName, CoreMatchers.is("ab4$"));
    }

    @Test
    public void validJavaIdentifiersResolvedForParameters() {
        String resolvePropertyName = StringToJavaIdentifierUtil.resolve("myProperty123");
        assertThat(resolvePropertyName, CoreMatchers.is("myProperty123"));
    }

    @Test
    public void underscoreResolvedForParameters() {
        String resolvePropertyName = StringToJavaIdentifierUtil.resolve("my_property_123");
        assertThat(resolvePropertyName, CoreMatchers.is("myProperty123"));
    }

    @Test
    public void noneJavaIdentifiersResolvedForParameters() {
        String resolvePropertyName = StringToJavaIdentifierUtil.resolve("123ab4~`!@#$%^&*()_+:;'\"|\\/<>");
        assertThat(resolvePropertyName, CoreMatchers.is("ab4$"));
    }
}
