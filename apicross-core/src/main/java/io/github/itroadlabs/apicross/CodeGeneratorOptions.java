package io.github.itroadlabs.apicross;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

@Getter
@Setter
@ToString
public class CodeGeneratorOptions {
    private String writeSourcesTo;
    private boolean generateOnlyModels = false;
    private boolean cleanupUnusedModels = false;
    private Set<String> skipTags = Collections.emptySet();
    private Set<String> generateOnlyTags = Collections.emptySet();
    private String requestsHandlerMethodNameResolverClassName;
    private String requestsHandlerTypeNameResolverClassName;
    private String propertyNameResolverClassName;
    private String parameterNameResolverClassName;
}
