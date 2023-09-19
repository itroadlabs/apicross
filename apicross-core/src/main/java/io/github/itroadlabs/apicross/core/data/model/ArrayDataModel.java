package io.github.itroadlabs.apicross.core.data.model;

import io.swagger.v3.oas.models.media.ArraySchema;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Objects;
import java.util.function.Supplier;

public class ArrayDataModel extends DataModel {
    private final Supplier<DataModel> itemsDataModel;

    ArrayDataModel(Supplier<DataModel> itemsDataModel, ArraySchema source) {
        super(source);
        this.itemsDataModel = Objects.requireNonNull(itemsDataModel);
    }

    public boolean isUniqueItems() {
        return BooleanUtils.isTrue(getSource().getUniqueItems());
    }

    public Integer getMinItems() {
        return getSource().getMinItems();
    }

    public Integer getMaxItems() {
        return getSource().getMaxItems();
    }

    public DataModel getItemsDataModel() {
        return itemsDataModel.get();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ArrayDataModel)) {
            return false;
        }
        ArrayDataModel dataModel = (ArrayDataModel) object;
        if (!dataModel.getClass().equals(this.getClass())) {
            return false;
        }
        if (!this.itemsDataModel.equals(dataModel.itemsDataModel)) {
            return false;
        }
        return this.getSource().getName() != null
                && this.getSource().getName().equals(dataModel.getSource().getName());
    }

    @Override
    public int hashCode() {
        return this.getSource().getType() != null ? this.getSource().getType().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ArrayDataModel{" +
                "itemsDataModel=" + itemsDataModel.toString() +
                '}';
    }
}
