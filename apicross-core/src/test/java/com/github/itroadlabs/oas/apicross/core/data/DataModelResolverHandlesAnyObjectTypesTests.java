package com.github.itroadlabs.oas.apicross.core.data;

import com.github.itroadlabs.oas.apicross.core.data.model.ArrayDataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModelProperty;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DataModelResolverHandlesAnyObjectTypesTests extends DataModelSchemaResolverTestsBase {
    @BeforeEach
    public void setup() throws IOException {
        init("DataModelSchemaResolverTest.anyObjectLikeSchemaResolved.yaml");
    }

    @Test
    public void modelResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ObjectType1");

        DataModel resolvedSchema = resolver.resolve(schema);
        assertTrue(resolvedSchema instanceof ObjectDataModel);
    }

    @Test
    public void propertyResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ObjectType2");

        DataModel resolvedSchema = resolver.resolve(schema);
        assertTrue(resolvedSchema instanceof ObjectDataModel);
        ObjectDataModelProperty property1 = ((ObjectDataModel) resolvedSchema).getProperty("prop1");
        assertNotNull(property1);
        assertTrue(property1.getType().isAnyType());
        assertEquals("Anytype", property1.getDescription());
    }

    @Test
    public void arrayItemResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ArrayType1");

        DataModel resolvedSchema = resolver.resolve(schema);
        assertTrue(resolvedSchema instanceof ArrayDataModel);
        DataModel itemsDataModel = ((ArrayDataModel) resolvedSchema).getItemsDataModel();
        assertTrue(itemsDataModel.isAnyType());
    }
}
