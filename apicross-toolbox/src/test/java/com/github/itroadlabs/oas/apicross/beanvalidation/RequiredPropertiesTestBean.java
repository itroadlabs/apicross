package com.github.itroadlabs.oas.apicross.beanvalidation;

import java.util.HashSet;
import java.util.Set;

@RequiredProperties({"a", "b"})
public class RequiredPropertiesTestBean implements HasSpecifiedProperties {
    private final Set<String> $specifiedProperties = new HashSet<>();
    private String a;
    private String b;
    private String c;
    private String d;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        $specifiedProperties.add("a");
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        $specifiedProperties.add("b");
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        $specifiedProperties.add("c");
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        $specifiedProperties.add("d");
        this.d = d;
    }

    @Override
    public Set<String> $specifiedProperties() {
        return $specifiedProperties;
    }
}
