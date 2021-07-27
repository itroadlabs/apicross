package io.github.itroadlabs.apicross.java;

import io.github.itroadlabs.apicross.CodeGeneratorOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
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
