package com.github.itroadlabs.oas.apicross.core.data;

import com.github.itroadlabs.oas.apicross.core.data.model.ObjectDataModel;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class DataModelResolverHandlesOneOfTests extends DataModelSchemaResolverTestsBase {
    @Test
    public void oneOfSchemaTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.oneOfSchemaTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("OneOfSchemaType");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertNotNull(resolvedSchema.getProperty("commonProperty1"));
        assertNotNull(resolvedSchema.getProperty("commonProperty2"));
        assertTrue(resolvedSchema.getProperty("commonProperty1").isRequired());

        assertEquals("type", resolvedSchema.getInheritanceDiscriminatorPropertyName());
        Map<String, ObjectDataModel> childTypes = resolvedSchema.getInheritanceChildModelsMap();
        for (ObjectDataModel value : childTypes.values()) {
            assertEquals(value.getInheritanceParent(), resolvedSchema);
            assertEquals(value.getTypeName(), value.getInheritanceDiscriminatorValue());
        }
        assertEquals(2, childTypes.size());

        assertTrue(childTypes.containsKey("OneOfSchemaTypePart1"));
        assertTrue(childTypes.containsKey("OneOfSchemaTypePart2"));

        ObjectDataModel oneOfSchemaTypePart1 = childTypes.get("OneOfSchemaTypePart1");
        assertEquals("OneOfSchemaTypePart1", oneOfSchemaTypePart1.getTypeName());

        ObjectDataModel oneOfSchemaTypePart2 = childTypes.get("OneOfSchemaTypePart2");
        assertEquals("OneOfSchemaTypePart2", oneOfSchemaTypePart2.getTypeName());
    }

    @Test
    public void oneOfSchemaWithMappingTypeResolved() throws IOException {
        init("DataModelSchemaResolverTest.oneOfSchemaTypeResolved.yaml");
        Schema<?> schema = openAPIComponentsIndex.schemaByName("OneOfSchemaTypeWithMaping");

        ObjectDataModel resolvedSchema = (ObjectDataModel) resolver.resolve(schema);
        assertNotNull(resolvedSchema.getProperty("commonProperty1"));
        assertNotNull(resolvedSchema.getProperty("commonProperty2"));
        assertTrue(resolvedSchema.getProperty("commonProperty1").isRequired());

        assertEquals("type", resolvedSchema.getInheritanceDiscriminatorPropertyName());
        Map<String, ObjectDataModel> childTypes = resolvedSchema.getInheritanceChildModelsMap();
        assertEquals(2, childTypes.size());
        for (ObjectDataModel value : childTypes.values()) {
            assertEquals(value.getInheritanceParent(), resolvedSchema);
        }

        assertTrue(childTypes.containsKey("OneOfSchemaTypePart1"));
        assertTrue(childTypes.containsKey("OneOfSchemaTypePart2"));
        assertEquals("part1", childTypes.get("OneOfSchemaTypePart1").getInheritanceDiscriminatorValue());
        assertEquals("part2", childTypes.get("OneOfSchemaTypePart2").getInheritanceDiscriminatorValue());

        ObjectDataModel oneOfSchemaTypePart1 = childTypes.get("OneOfSchemaTypePart1");
        assertEquals("OneOfSchemaTypePart1", oneOfSchemaTypePart1.getTypeName());

        ObjectDataModel oneOfSchemaTypePart2 = childTypes.get("OneOfSchemaTypePart2");
        assertEquals("OneOfSchemaTypePart2", oneOfSchemaTypePart2.getTypeName());
    }
}
