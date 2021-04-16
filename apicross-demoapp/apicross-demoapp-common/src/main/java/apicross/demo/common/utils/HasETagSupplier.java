package apicross.demo.common.utils;

import lombok.NonNull;

import java.util.function.Supplier;

public class HasETagSupplier implements HasETag {
    private final Supplier<String> etagSupplier;

    public HasETagSupplier(@NonNull Supplier<String> etagSupplier) {
        this.etagSupplier = etagSupplier;
    }

    @Override
    public String etag() {
        return etagSupplier.get();
    }
}
