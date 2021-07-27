package io.github.itroadlabs.apicross.springmvc;

import io.github.itroadlabs.apicross.java.JavaCodeGeneratorOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class SpringMvcCodeGeneratorOptions extends JavaCodeGeneratorOptions {
    private boolean enableApicrossJavaBeanValidationSupport = false;
    private boolean enableDataModelReadInterfaces = false;
    private boolean enableSpringSecurityAuthPrincipal = false;
    private boolean useQueryStringParametersObject = true;
    private String apiModelReadInterfacesPackage;
    private List<String> alternativeTemplatesPath;
}
