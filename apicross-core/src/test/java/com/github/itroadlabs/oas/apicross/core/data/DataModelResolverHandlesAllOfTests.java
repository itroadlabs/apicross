package com.github.itroadlabs.oas.apicross.core.data;

import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModel;
import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModelProperty;
import com.github.itroadlabs.oas.apicross.core.data.model.PrimitiveDataModel;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataModelResolverHandlesAllOfTests extends DataModelSchemaResolverTestsBase {
    @Test
    public void allOfSchemaTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.allOfSchemaTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfSchemaType");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);

        assertEquals(4, resolvedSchema.getProperties().size());
        assertTrue(((PrimitiveDataModel) resolvedSchema.getProperty("a").getType()).isString());
        assertTrue(((PrimitiveDataModel) resolvedSchema.getProperty("b").getType()).isString());
        assertEquals("AllOfSchemaTypePart1", ((ObjectDataModel)resolvedSchema.getProperty("d").getType()).getTypeName());
    }

    @Test
    public void allOfSchemaTypeResolved_withConstraints() throws IOException {
        init("DataModelSchemaResolverTest.allOfSchemaTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfSchemaTypeOnlyConstraints");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);

        assertEquals(3, resolvedSchema.getProperties().size());
        assertTrue(resolvedSchema.getProperty("a").isRequired());
        assertTrue(resolvedSchema.getProperty("b").isOptional());
        assertTrue(resolvedSchema.getProperty("c").isRequired());
        assertEquals("Test", resolvedSchema.getDescription());
    }

    @Test
    public void allOfInFieldWithOnlyExampleIsTreatedAsSimpleRef() throws IOException {
        init("DataModelSchemaResolverTest.allOfSchemaTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfInField1");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertEquals(1, resolvedSchema.getProperties().size());
        assertEquals("AllOfSchemaTypePart1", ((ObjectDataModel)resolvedSchema.getProperty("c").getType()).getTypeName());
    }

    @Test
    public void allOfInFieldWithOnlyDescriptionIsTreatedAsSimpleRef() throws IOException {
        init("DataModelSchemaResolverTest.allOfSchemaTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfInField2");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertEquals(1, resolvedSchema.getProperties().size());
        assertEquals("AllOfSchemaTypePart1", ((ObjectDataModel)resolvedSchema.getProperty("c").getType()).getTypeName());
    }

    @Test
    public void allOfInFieldWithConstraints() throws IOException {
        init("DataModelSchemaResolverTest.allOfSchemaTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfInField3");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertEquals(1, resolvedSchema.getProperties().size());
        ObjectDataModelProperty property = resolvedSchema.getProperty("c");
        DataModel propertyType = property.getType();
        assertTrue(propertyType.isPrimitive());
        assertTrue(propertyType.isNullable());
        PrimitiveDataModel primitiveDataModel = (PrimitiveDataModel) propertyType;
        assertEquals(100, primitiveDataModel.getMaxLength().intValue());
        assertEquals(10, primitiveDataModel.getMinLength().intValue());
    }
}
