package io.github.itroadlabs.apicross.core.data;

import io.github.itroadlabs.apicross.core.data.model.ObjectDataModel;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class DataModelResolverHandlesInlineModelsTests extends DataModelSchemaResolverTestsBase {
    @BeforeEach
    public void setup() throws IOException {
        init("DataModelResolverHandlesInlineModelsTests.yaml");
    }

    @Test
    public void inlineModelResolved1() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("Model1");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        List<ObjectDataModel> inlineModels = InlineDataModelResolver.resolveInlineModels((typeName, propertyName) -> typeName + "_" + propertyName, resolvedSchema);
        assertEquals(2, inlineModels.size());
        Map<String, ObjectDataModel> inlineModelsMap = inlineModels.stream()
                .collect(Collectors.toMap(ObjectDataModel::getTypeName, dataModelSchema -> dataModelSchema));
        ObjectDataModel model1P1 = inlineModelsMap.get("Model1_p1");
        assertNotNull(model1P1);
        ObjectDataModel model1P2 = inlineModelsMap.get("Model1_p2");
        assertNotNull(model1P2);
    }

    @Test
    public void inlineModelResolved_stack() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("Model3");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        List<ObjectDataModel> inlineModels = InlineDataModelResolver.resolveInlineModels((typeName, propertyName) -> typeName + "_" + propertyName, resolvedSchema);
        Map<String, ObjectDataModel> inlineModelsMap = inlineModels.stream()
                .collect(Collectors.toMap(ObjectDataModel::getTypeName, dataModelSchema -> dataModelSchema));
        ObjectDataModel model3_p1 = inlineModelsMap.get("Model3_p1");
        assertNotNull(model3_p1);
        ObjectDataModel model3_p1_p2 = inlineModelsMap.get("Model3_p1_p2");
        assertNotNull(model3_p1_p2);
    }

    @Test
    public void inlineModelResolved_additionalProperties() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("Model4");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        List<ObjectDataModel> inlineModels = InlineDataModelResolver.resolveInlineModels((typeName, propertyName) -> typeName + "_" + propertyName, resolvedSchema);
        Map<String, ObjectDataModel> inlineModelsMap = inlineModels.stream()
                .collect(Collectors.toMap(ObjectDataModel::getTypeName, dataModelSchema -> dataModelSchema));
        ObjectDataModel model3_p1 = inlineModelsMap.get("Model4_additionalProperties");
        assertNotNull(model3_p1);
    }

    @Test
    public void inlineModelResolved_array() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("Model5");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        List<ObjectDataModel> inlineModels = InlineDataModelResolver.resolveInlineModels((typeName, propertyName) -> typeName + "_" + propertyName, resolvedSchema);
        Map<String, ObjectDataModel> inlineModelsMap = inlineModels.stream()
                .collect(Collectors.toMap(ObjectDataModel::getTypeName, dataModelSchema -> dataModelSchema));
        ObjectDataModel model5_array = inlineModelsMap.get("Model5_arrayItem");
        assertNotNull(model5_array);
    }
}
