package apicross.demo.common.utils;

import com.github.itroadlabs.oas.apicross.java.DefaultPropertyAndParameterNameResolver;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class DemoAppApiModelPropertyNameResolver extends DefaultPropertyAndParameterNameResolver {
    @Nonnull
    @Override
    public String resolvePropertyName(@Nonnull Schema<?> propertySchema, @Nonnull String apiPropertyName) {
        if (apiPropertyName.startsWith("https://rels")) {
            String rel = apiPropertyName.substring(apiPropertyName.lastIndexOf('/') + 1);
            String[] parts = rel.split("-");
            StringBuilder builder = new StringBuilder(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                builder.append(StringUtils.capitalize(part));
            }
            return builder.toString();
        } else {
            return super.resolvePropertyName(propertySchema, apiPropertyName);
        }
    }
}
