package apicross.demo.common.utils;

import com.github.itroadlabs.oas.apicross.java.DefaultRequestsHandlerMethodNameResolver;
import io.swagger.v3.oas.models.Operation;

import javax.annotation.Nonnull;

public class DemoAppApiHandlerMethodNameResolver extends DefaultRequestsHandlerMethodNameResolver {
    @Nonnull
    @Override
    public String resolve(@Nonnull Operation operation, @Nonnull String uriPath, String consumesMediaType, String producesMediaType) {
        if (((producesMediaType == null) || producesMediaType.endsWith("v1+json"))
                && ((consumesMediaType == null) || consumesMediaType.endsWith("v1+json"))) {
            return operation.getOperationId();
        } else {
            return super.resolve(operation, uriPath, consumesMediaType, producesMediaType);
        }
    }
}
