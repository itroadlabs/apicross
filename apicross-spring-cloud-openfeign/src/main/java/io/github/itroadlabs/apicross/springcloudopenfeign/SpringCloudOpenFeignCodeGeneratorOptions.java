package io.github.itroadlabs.apicross.springcloudopenfeign;

import io.github.itroadlabs.apicross.java.JavaCodeGeneratorOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class SpringCloudOpenFeignCodeGeneratorOptions extends JavaCodeGeneratorOptions {
    private boolean enableApicrossJavaBeanValidationSupport = false;
}
