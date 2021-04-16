package com.github.itroadlabs.oas.apicross.core.data.model;

import com.github.itroadlabs.oas.apicross.core.HasCustomModelAttributes;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.BooleanUtils;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class DataModel extends HasCustomModelAttributes {
    private final Schema<?> source;

    protected DataModel(@Nonnull Schema<?> source) {
        this.source = Objects.requireNonNull(source);
    }

    public Schema<?> getSource() {
        return source;
    }

    public static PrimitiveDataModel newPrimitiveDataModel(@Nonnull Schema<?> source) {
        return new PrimitiveDataModel(source);
    }

    public static ArrayDataModel newArrayDataModel(@Nonnull ArraySchema source, @Nonnull DataModel arrayItemType) {
        return new ArrayDataModel(arrayItemType, source);
    }

    public static ObjectDataModel newObjectDataModel(@Nonnull Schema<?> source, @Nonnull String typeName) {
        return new ObjectDataModel(typeName, source);
    }

    public static ObjectDataModel newObjectDataModel(@Nonnull Schema<?> source, @Nonnull String typeName,
                                                     @Nonnull Set<ObjectDataModelProperty> properties,
                                                     DataModel additionalPropertiesDataModel) {
        return new ObjectDataModel(typeName, source, properties, additionalPropertiesDataModel);
    }

    public static ObjectDataModel newObjectDataModel(@Nonnull Schema<?> source, @Nonnull String typeName,
                                                     @Nonnull Map<String, ObjectDataModel> childSchemas,
                                                     @Nonnull String discriminatorPropertyName, @Nonnull Map<String, String> mapping) {
        return new ObjectDataModel(typeName, source, childSchemas, discriminatorPropertyName, mapping);
    }

    public static DataModel anyType(Schema<?> source) {
        return new AnyTypeDataModel(source);
    }

    public final boolean isAnyType() {
        return getClass().equals(AnyTypeDataModel.class);
    }

    public boolean isObject() {
        return (this instanceof ObjectDataModel);
    }

    public boolean isPrimitive() {
        return (this instanceof PrimitiveDataModel);
    }

    public boolean isArray() {
        return (this instanceof ArrayDataModel);
    }

    public boolean isNullable() {
        return BooleanUtils.isTrue(source.getNullable());
    }

    public String getDescription() {
        return source.getDescription();
    }

    public Object getDefaultValue() {
        return source.getDefault();
    }

    public boolean isDefaultValueNotNull() {
        return source.getDefault() != null;
    }

    private static class AnyTypeDataModel extends DataModel {
        public AnyTypeDataModel(@Nonnull Schema<?> source) {
            super(source);
        }

        @Override
        public String toString() {
            return "DataModelSchema{AnyType}";
        }

        @Override
        public boolean equals(Object object) {
            if (object.getClass().equals(AnyTypeDataModel.class)) {
                return ((AnyTypeDataModel) object).getSource().equals(this.getSource());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
