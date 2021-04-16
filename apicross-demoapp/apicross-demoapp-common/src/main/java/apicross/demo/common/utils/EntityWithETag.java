package apicross.demo.common.utils;

import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class EntityWithETag<T> extends HasETagSupplier {
    private final T entity;

    public EntityWithETag(@NonNull T entity, @NonNull Supplier<String> etagSupplier) {
        super(etagSupplier);
        this.entity = Objects.requireNonNull(entity);
    }

    @Nonnull
    public T getEntity() {
        return entity;
    }
}
