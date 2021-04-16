package com.github.itroadlabs.oas.apicross.core.data.model;

import io.swagger.v3.oas.models.media.*;
import org.apache.commons.lang3.BooleanUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PrimitiveDataModel extends DataModel {
    PrimitiveDataModel(Schema<?> source) {
        super(source);
    }

    public String getType() {
        return getSource().getType();
    }

    public String getFormat() {
        return getSource().getFormat();
    }

    public Integer getMinLength() {
        return getSource().getMinLength();
    }

    public Integer getMaxLength() {
        return getSource().getMaxLength();
    }

    public String getPattern() {
        return getSource().getPattern();
    }

    @Nullable
    public Set<String> getEnumValues() {
        List<?> anEnum = getSource().getEnum();
        return anEnum == null ? null : anEnum.stream()
                .map((Function<Object, String>) Object::toString)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Number getMinimum() {
        BigDecimal minimum = getSource().getMinimum();
        return minimum == null ? null : toNumberAccordingType(minimum);
    }

    public Number getMaximum() {
        BigDecimal maximum = getSource().getMaximum();
        return maximum == null ? null : toNumberAccordingType(maximum);
    }

    public boolean isExclusiveMinimum() {
        return BooleanUtils.isTrue(getSource().getExclusiveMinimum());
    }

    public boolean isExclusiveMaximum() {
        return BooleanUtils.isTrue(getSource().getExclusiveMaximum());
    }

    public boolean isString() {
        return isPrimitive() && "string".equals(getSource().getType());
    }

    public boolean isInteger() {
        return isPrimitive() && "integer".equals(getSource().getType());
    }

    private Number toNumberAccordingType(BigDecimal value) {
        Schema<?> source = getSource();
        if (source instanceof IntegerSchema && "int32".equals(getFormat())) {
            return value.intValue();
        } else if (source instanceof IntegerSchema && "int64".equals(getFormat())) {
            return value.longValue();
        } else {
            return value;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PrimitiveDataModel)) {
            return false;
        }
        PrimitiveDataModel dataModel = (PrimitiveDataModel) object;
        if (!dataModel.getClass().equals(this.getClass())) {
            return false;
        }
        return this.getSource().getType() != null && this.getSource().getType().equals(dataModel.getSource().getType());
    }

    @Override
    public int hashCode() {
        return this.getSource().getType() != null ? this.getSource().getType().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PrimitiveDataModel{" + getType() + "}";
    }
}
