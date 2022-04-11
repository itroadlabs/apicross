package io.github.itroadlabs.apicross.core.data.model;

import com.google.common.base.Preconditions;
import io.github.itroadlabs.apicross.core.NamedDatum;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.BooleanUtils;

public class ObjectDataModelProperty extends NamedDatum {
    /**
     * Schema from OpenAPI specification, which declares this property
     */
    private final Schema<?> schemaThatDeclaresThisProperty;

    public ObjectDataModelProperty(String name, String resolvedName, String description, DataModel dataModel,
                                   Schema<?> schemaThatDeclaresThisProperty, boolean required, boolean deprecated) {
        super(name, resolvedName, description, dataModel, required, deprecated);
        this.schemaThatDeclaresThisProperty = schemaThatDeclaresThisProperty;
    }

    public boolean isReadOnly() {
        return BooleanUtils.isTrue(getSchema().getReadOnly());
    }

    public boolean isWriteOnly() {
        return BooleanUtils.isTrue(getSchema().getWriteOnly());
    }

    String getOriginSchemaName() {
        return schemaThatDeclaresThisProperty.getName();
    }

    @Deprecated // TODO: make class immutable
    public void markRequired() {
        setOptional(false);
    }

    @Deprecated // TODO: make class immutable
    public void changeTypeToExternal(String newTypeName) {
        Preconditions.checkState(getType().isObject());
        ((ObjectDataModel) this.getType()).changeTypeToExternal(newTypeName);
    }

    public ObjectDataModelProperty copy() {
        return new ObjectDataModelProperty(
                this.getName(), this.getResolvedName(),
                this.getDescription(), this.getType(),
                this.schemaThatDeclaresThisProperty,
                this.isRequired(), this.isDeprecated());
    }
}
