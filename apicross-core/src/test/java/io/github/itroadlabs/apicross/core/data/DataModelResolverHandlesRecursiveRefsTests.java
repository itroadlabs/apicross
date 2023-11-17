package io.github.itroadlabs.apicross.core.data;

import io.github.itroadlabs.apicross.core.data.model.ArrayDataModel;
import io.github.itroadlabs.apicross.core.data.model.DataModel;
import io.github.itroadlabs.apicross.core.data.model.ObjectDataModel;
import io.github.itroadlabs.apicross.core.data.model.ObjectDataModelProperty;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataModelResolverHandlesRecursiveRefsTests extends DataModelResolverTestsBase {
    @BeforeEach
    void setup() throws IOException {
        init("DataModelResolverHandlesRecursiveRefsTests.yaml");
    }

    @Test
    void resolveSimpleReferences() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("Model1");

        ObjectDataModel model1 = (ObjectDataModel) resolver.resolve(schema);
        ObjectDataModelProperty parent = model1.getProperty("parent");
        DataModel parentPropertyType = parent.getType();
        assertSame(parentPropertyType, model1);
    }

    @Test
    void resolveTransitiveReferences() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("Model2");

        ObjectDataModel model2 = (ObjectDataModel) resolver.resolve(schema);
        ObjectDataModelProperty bProperty = model2.getProperty("b");
        ObjectDataModel bPropertyType = (ObjectDataModel) bProperty.getType();
        ObjectDataModelProperty parentProperty = bPropertyType.getProperty("parent");
        DataModel parentPropertyType = parentProperty.getType();

        assertSame(parentPropertyType, model2);
    }

    @Test
    void resolveArray() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("Model4");

        ObjectDataModel model4 = (ObjectDataModel) resolver.resolve(schema);
        ObjectDataModelProperty aProperty = model4.getProperty("a");
        ArrayDataModel arrayDataModel = (ArrayDataModel) aProperty.getType();
        assertSame(model4, arrayDataModel.getItemsDataModel());
    }

    @Test
    void resolveArrayTransitiveRef() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("Model5");

        ObjectDataModel model5 = (ObjectDataModel) resolver.resolve(schema);
        ObjectDataModelProperty aProperty = model5.getProperty("a");
        ArrayDataModel arrayDataModel = (ArrayDataModel) aProperty.getType();
        ObjectDataModel model6 = (ObjectDataModel) arrayDataModel.getItemsDataModel();
        ObjectDataModelProperty bProperty = model6.getProperty("b");
        DataModel bPropertyType = bProperty.getType();
        assertSame(bPropertyType, model5);
    }

    @Test
    void resolveRecursiveRefsInArrays() {
        Schema<?> model7schema = openAPIComponentsIndex.schemaByName("Model7");
        ObjectDataModel model7 = (ObjectDataModel) resolver.resolve(model7schema);
        ObjectDataModelProperty rootsProperty = model7.getProperty("roots");
        assertTrue(rootsProperty.getType() instanceof ArrayDataModel);
        ArrayDataModel arrayDataModel = (ArrayDataModel) rootsProperty.getType();
        ObjectDataModel itemsDataModel = (ObjectDataModel) arrayDataModel.getItemsDataModel();
        ObjectDataModelProperty children = itemsDataModel.getProperty("children");
        ArrayDataModel childrenArrayType = (ArrayDataModel) children.getType();
        DataModel childType = childrenArrayType.getItemsDataModel();
        assertSame(childType, itemsDataModel);
    }
}
