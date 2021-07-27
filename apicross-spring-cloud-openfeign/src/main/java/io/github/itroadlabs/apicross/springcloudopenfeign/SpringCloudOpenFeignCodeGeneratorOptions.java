package io.github.itroadlabs.apicross.springcloudopenfeign;

import io.github.itroadlabs.apicross.java.JavaCodeGeneratorOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class SpringCloudOpenFeignCodeGeneratorOptions extends JavaCodeGeneratorOptions {
    private boolean enableApicrossJavaBeanValidationSupport = false;
    private boolean useQueryStringParametersObject = true;
    private List<String> alternativeTemplatesPath;
}
