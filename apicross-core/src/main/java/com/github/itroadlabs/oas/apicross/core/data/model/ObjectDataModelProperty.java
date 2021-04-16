package com.github.itroadlabs.oas.apicross.core.data.model;

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.models.media.Schema;
import com.github.itroadlabs.oas.apicross.core.NamedDatum;
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

    public void markRequired() {
        setOptional(false);
    }

    public void changeTypeToExternal(String newTypeName) {
        Preconditions.checkState(getType().isObject());
        ((ObjectDataModel) this.getType()).changeTypeToExternal(newTypeName);
    }
}
