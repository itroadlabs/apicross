package com.github.itroadlabs.oas.apicross.springmvc.params;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@ParamNameBindingSupport
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestParamsObject {
    @ParamName("param-1")
    private String param1;
    @ParamName("param-2")
    private List<Integer> param2;

    public String getParam1() {
        return param1;
    }

    public TestParamsObject setParam1(String param1) {
        this.param1 = param1;
        return this;
    }

    public List<Integer> getParam2() {
        return param2;
    }

    public TestParamsObject setParam2(List<Integer> param2) {
        this.param2 = param2;
        return this;
    }
}
