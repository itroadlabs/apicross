package com.github.itroadlabs.oas.apicross.utils;

import com.github.itroadlabs.oas.apicross.core.data.model.ArrayDataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModelProperty;
import com.github.itroadlabs.oas.apicross.core.handler.model.MediaTypeContentModel;
import com.github.itroadlabs.oas.apicross.core.handler.model.RequestsHandler;
import com.github.itroadlabs.oas.apicross.core.handler.model.RequestsHandlerMethod;

import java.util.*;
import java.util.stream.Collectors;

public class UnusedSchemasCleaner {
    public Collection<ObjectDataModel> clean(Collection<ObjectDataModel> source, List<RequestsHandler> requestHandlers) {
        Set<ObjectDataModel> referencedSchemas = new HashSet<>();
        for (RequestsHandler requestHandler : requestHandlers) {
            List<RequestsHandlerMethod> methods = requestHandler.getMethods();
            for (RequestsHandlerMethod method : methods) {
                MediaTypeContentModel requestBody = method.getRequestBody();
                if (requestBody != null) {
                    DataModel content = requestBody.getContent();
                    collectRefs(content, referencedSchemas);
                }
                MediaTypeContentModel responseBody = method.getResponseBody();
                if (responseBody != null) {
                    DataModel content = responseBody.getContent();
                    collectRefs(content, referencedSchemas);
                }
            }
        }

        Set<ObjectDataModel> copy = new HashSet<>(referencedSchemas);
        for (ObjectDataModel objectDataModel : copy) {
            ObjectDataModel inheritanceParent = objectDataModel.getInheritanceParent();
            if (inheritanceParent != null) {
                referencedSchemas.add(inheritanceParent);
            }
            Map<String, ObjectDataModel> inheritanceChildModels = objectDataModel.getInheritanceChildModelsMap();
            if (inheritanceChildModels != null) {
                for (ObjectDataModel childModel : inheritanceChildModels.values()) {
                    referencedSchemas.add(childModel);
                    collectRefsFromProperties(childModel.getProperties(), referencedSchemas);
                }
            }
            if (objectDataModel.getAdditionalPropertiesDataModel() != null) {
                DataModel additionalPropertiesType = objectDataModel.getAdditionalPropertiesDataModel();
                if (additionalPropertiesType != null && additionalPropertiesType.isObject()) {
                    referencedSchemas.add((ObjectDataModel) additionalPropertiesType);
                    collectRefsFromProperties(((ObjectDataModel) additionalPropertiesType).getProperties(), referencedSchemas);
                }
            }
        }

        return source.stream()
                .filter(referencedSchemas::contains)
                .collect(Collectors.toSet());
    }

    private void collectRefs(DataModel dataModel, Set<ObjectDataModel> collectTo) {
        if (dataModel instanceof ObjectDataModel) {
            ObjectDataModel objectDataModel = (ObjectDataModel) dataModel;
            collectTo.add(objectDataModel);
            collectRefsFromProperties(objectDataModel.getProperties(), collectTo);
        } else if (dataModel instanceof ArrayDataModel) {
            ArrayDataModel arrayDataModel = (ArrayDataModel) dataModel;
            DataModel itemsDataModel = arrayDataModel.getItemsDataModel();
            if (itemsDataModel instanceof ObjectDataModel) {
                collectTo.add((ObjectDataModel) itemsDataModel);
                collectRefsFromProperties(((ObjectDataModel) itemsDataModel).getProperties(), collectTo);
            }
        }
    }

    private void collectRefsFromProperties(Set<ObjectDataModelProperty> properties, Set<ObjectDataModel> collectTo) {
        for (ObjectDataModelProperty property : properties) {
            DataModel type = property.getType();
            collectRefs(type, collectTo);
        }
    }
}
