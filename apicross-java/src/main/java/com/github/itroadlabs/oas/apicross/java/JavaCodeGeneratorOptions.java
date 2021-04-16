package com.github.itroadlabs.oas.apicross.java;

import com.github.itroadlabs.oas.apicross.CodeGeneratorOptions;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class JavaCodeGeneratorOptions extends CodeGeneratorOptions {
    private String apiHandlerPackage;
    private String apiModelPackage;
    private String modelClassNameSuffix;
    private String modelClassNamePrefix;
    private Map<String, String> dataModelsExternalTypesMap = new HashMap<>();
    private Map<String, String> dataModelsInterfacesMap = new HashMap<>();
    private Map<String, String> queryObjectsInterfacesMap = new HashMap<>();
    private Set<String> globalQueryObjectsInterfaces = new LinkedHashSet<>();
    private boolean useJsonNullable = true;
}
