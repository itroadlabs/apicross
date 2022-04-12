package io.github.itroadlabs.apicross.core.data;

import io.github.itroadlabs.apicross.core.data.model.DataModel;
import io.github.itroadlabs.apicross.core.data.model.ObjectDataModel;
import io.github.itroadlabs.apicross.core.data.model.ObjectDataModelProperty;
import io.github.itroadlabs.apicross.core.data.model.PrimitiveDataModel;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataModelResolverHandlesAllOfTests extends DataModelSchemaResolverTestsBase {
    @BeforeEach
    public void setup() throws IOException {
        init("DataModelResolverHandlesAllOfTests.yaml");
    }

    @Test
    public void allOfSchemaTypeResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfSchemaType");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);

        assertEquals(4, resolvedSchema.getProperties().size());
        assertTrue(((PrimitiveDataModel) resolvedSchema.getProperty("a").getType()).isString());
        assertTrue(((PrimitiveDataModel) resolvedSchema.getProperty("b").getType()).isString());
        assertEquals("AllOfSchemaTypePart1", ((ObjectDataModel)resolvedSchema.getProperty("d").getType()).getTypeName());
    }

    @Test
    public void allOfSchemaTypeResolved_withConstraints() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfSchemaTypeOnlyConstraints");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);

        assertEquals(3, resolvedSchema.getProperties().size());
        assertTrue(resolvedSchema.getProperty("a").isRequired());
        assertTrue(resolvedSchema.getProperty("b").isOptional());
        assertTrue(resolvedSchema.getProperty("c").isRequired());
        assertEquals("Test", resolvedSchema.getDescription());
    }

    @Test
    public void allOfInFieldWithOnlyExampleIsTreatedAsSimpleRef() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfInField1");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertEquals(1, resolvedSchema.getProperties().size());
        assertEquals("AllOfSchemaTypePart1", ((ObjectDataModel)resolvedSchema.getProperty("c").getType()).getTypeName());
    }

    @Test
    public void allOfInFieldWithOnlyDescriptionIsTreatedAsSimpleRef() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("AllOfInField2");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertEquals(1, resolvedSchema.getProperties().size());
        assertEquals("AllOfSchemaTypePart1", ((ObjectDataModel)resolvedSchema.getProperty("c").getType()).getTypeName());
    }

    @Test
    public void allOfInFieldWithConstraints() {
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
