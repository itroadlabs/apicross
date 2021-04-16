package com.github.itroadlabs.oas.apicross.core.data;

import com.github.itroadlabs.oas.apicross.core.data.model.ArrayDataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModelProperty;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InlineDataModelResolver {
    @Nonnull
    public static List<ObjectDataModel> resolveInlineModels(InlineModelTypeNameResolver resolver, ObjectDataModel source) {
        List<ObjectDataModel> result = new ArrayList<>();
        doResolveInlineModels(result, resolver, source);
        return result;
    }

    @Nonnull
    public static List<ObjectDataModel> resolveInlineModels(InlineModelTypeNameResolver resolver, ArrayDataModel arrayDataModel) {
        List<ObjectDataModel> result = new ArrayList<>();
        DataModel arrayItemType = arrayDataModel.getItemsDataModel();
        if (arrayItemType.isObject() && ((ObjectDataModel) arrayItemType).getTypeName() == null) {
            ObjectDataModel objectArrayItemType = (ObjectDataModel) arrayItemType;
            objectArrayItemType.setTypeName(resolver.resolveArrayItemTypeName(((ObjectDataModel) arrayItemType).getTypeName(), ""));
            result.add(objectArrayItemType); // TODO: really needed?
            List<ObjectDataModel> objectDataModels = resolveInlineModels(resolver, objectArrayItemType);
            result.addAll(objectDataModels);
        }
        return result;
    }

    private static void doResolveInlineModels(List<ObjectDataModel> collectTo, InlineModelTypeNameResolver resolver,
                                              ObjectDataModel source) {
        Collection<ObjectDataModelProperty> properties = source.getProperties();
        for (ObjectDataModelProperty property : properties) {
            DataModel type = property.getType();
            if (type.isObject() && ((ObjectDataModel) type).getTypeName() == null) {
                ObjectDataModel propertyType = (ObjectDataModel) type;
                propertyType.setTypeName(resolver.resolveTypeName(source.getTypeName(), property.getResolvedName()));
                collectTo.add(propertyType);
                doResolveInlineModels(collectTo, resolver, propertyType);
            } else if (type.isArray()) {
                ArrayDataModel propertyType = (ArrayDataModel) type;
                DataModel arrayItemType = propertyType.getItemsDataModel();
                if (arrayItemType.isObject() && ((ObjectDataModel) arrayItemType).getTypeName() == null) {
                    ObjectDataModel objectArrayItemType = (ObjectDataModel) arrayItemType;
                    objectArrayItemType.setTypeName(resolver.resolveArrayItemTypeName(source.getTypeName(), property.getResolvedName()));
                    collectTo.add(objectArrayItemType);
                    doResolveInlineModels(collectTo, resolver, objectArrayItemType);
                }
            }
        }

        DataModel additionalPropertiesType = source.getAdditionalPropertiesDataModel();
        if (additionalPropertiesType != null && additionalPropertiesType.isObject()) {
            if (((ObjectDataModel) additionalPropertiesType).getTypeName() == null) {
                ObjectDataModel objectType = (ObjectDataModel) additionalPropertiesType;
                objectType.setTypeName(resolver.resolveTypeName(source.getTypeName(), "additionalProperties"));
                collectTo.add(objectType);
                doResolveInlineModels(collectTo, resolver, objectType);
            }
        }
    }
}
