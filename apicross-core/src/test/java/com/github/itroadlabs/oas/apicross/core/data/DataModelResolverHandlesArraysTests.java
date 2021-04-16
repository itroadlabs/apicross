package com.github.itroadlabs.oas.apicross.core.data;

import com.github.itroadlabs.oas.apicross.core.data.model.ArrayDataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.PrimitiveDataModel;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class DataModelResolverHandlesArraysTests extends DataModelSchemaResolverTestsBase {
    @Test
    public void simpleArrayResolved() throws IOException {
        init("DataModelSchemaResolverTest.simpleArrayResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("SimpleArray");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolver.resolve(schema);

        assertTrue(resolvedSchema.isArray());
        assertTrue(((PrimitiveDataModel) resolvedSchema.getItemsDataModel()).isString());
    }

    @Test
    public void arrayWithRefOntoPrimitiveTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.arrayWithRefOntoPrimitiveTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoPrimitiveType");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolver.resolve(schema);

        assertTrue(resolvedSchema.isArray());
        assertTrue(((PrimitiveDataModel) resolvedSchema.getItemsDataModel()).isString());
    }

    @Test
    public void arrayWithRefOntoObjectTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.arrayWithRefOntoObjectTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoObjectType");

        ArrayDataModel resolvedSchema = (ArrayDataModel) resolver.resolve(schema);

        assertTrue(resolvedSchema.isArray());
        assertEquals("SimpleObject", ((ObjectDataModel) resolvedSchema.getItemsDataModel()).getTypeName());
    }

    @Test
    public void arrayConstraintsResolved() throws IOException {
        init("DataModelSchemaResolverTest.arrayConstraintsResolved.yaml");

        // 1)
        Schema<?> schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoSimpleType");
        ArrayDataModel resolvedSchema = (ArrayDataModel) resolver.resolve(schema);
        arrayConstraintsResolvedVerification(resolvedSchema);
        schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoSimpleTypeV2");
        resolvedSchema = (ArrayDataModel) resolver.resolve(schema);
        arrayConstraintsResolvedVerification(resolvedSchema);

        // 2)
        schema = openAPIComponentsIndex.schemaByName("ArrayWithRefOntoObjectType");

        resolvedSchema = (ArrayDataModel) resolver.resolve(schema);

        assertTrue(resolvedSchema.isArray());
        assertEquals("SimpleObject", ((ObjectDataModel) resolvedSchema.getItemsDataModel()).getTypeName());

        assertEquals(1, resolvedSchema.getMinItems().intValue());
        assertEquals(10, resolvedSchema.getMaxItems().intValue());
        assertTrue(resolvedSchema.isUniqueItems());

        assertFalse(resolvedSchema.getItemsDataModel().isNullable());
    }

    private void arrayConstraintsResolvedVerification(ArrayDataModel resolvedSchema) {
        assertTrue(resolvedSchema.isArray());
        assertTrue(((PrimitiveDataModel) resolvedSchema.getItemsDataModel()).isString());

        assertEquals(1, resolvedSchema.getMinItems().intValue());
        assertEquals(10, resolvedSchema.getMaxItems().intValue());
        assertTrue(resolvedSchema.isUniqueItems());

        Integer maxLength = ((PrimitiveDataModel) resolvedSchema.getItemsDataModel()).getMaxLength();
        assertEquals(100, (int) maxLength);
        assertTrue(resolvedSchema.getItemsDataModel().isNullable());
    }
}
