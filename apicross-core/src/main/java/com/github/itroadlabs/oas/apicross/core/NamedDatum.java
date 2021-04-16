package com.github.itroadlabs.oas.apicross.core;

import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Objects;

public abstract class NamedDatum extends HasCustomModelAttributes {
    private final String name;
    private final String resolvedName;
    private final String description;
    private final DataModel type;
    private final boolean deprecated;
    private boolean required;

    public NamedDatum(String name, String resolvedName, String description, DataModel dataModel, boolean required, boolean deprecated) {
        this.name = name;
        this.resolvedName = resolvedName;
        this.description = description;
        this.type = dataModel;
        this.required = required;
        this.deprecated = deprecated;
    }

    public String getName() {
        return name;
    }

    public String getResolvedName() {
        return resolvedName;
    }

    public String getDescription() {
        return description;
    }

    public DataModel getType() {
        return type;
    }

    public boolean isOptional() {
        return !isRequired();
    }

    public boolean isRequired() {
        return required;
    }

    protected void setOptional(boolean optional) {
        this.required = !optional;
    }

    public boolean isDefaultValueNotNull() {
        return getType().isDefaultValueNotNull();
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public boolean isObject() {
        return getType().isObject();
    }

    public boolean isArray() {
        return getType().isArray();
    }

    public boolean isPrimitive() {
        return getType().isPrimitive();
    }

    public boolean isNullable() {
        return BooleanUtils.isTrue(getSchema().getNullable());
    }

    protected Schema<?> getSchema() {
        return getType().getSource();
    }

    public boolean isOptionalWithoutDefaultValue() {
        return this.isOptional() && !this.isDefaultValueNotNull();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedDatum)) return false;
        NamedDatum that = (NamedDatum) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
