package io.github.itroadlabs.apicross.core.data;

import io.github.itroadlabs.apicross.core.data.model.ObjectDataModel;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DataModelResolverHandlesOneOfTests extends DataModelSchemaResolverTestsBase {
    @BeforeEach
    void setup() throws IOException {
        init("DataModelSchemaResolverTest.oneOfSchemaTypeResolved.yaml");
    }

    @Test
    void simpleOneOfSchemaResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("SimpleOneOfSchemaType");

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

        assertTrue(childTypes.containsKey("SimpleOneOfSchemaTypePart1"));
        assertTrue(childTypes.containsKey("SimpleOneOfSchemaTypePart2"));

        ObjectDataModel oneOfSchemaTypePart1 = childTypes.get("SimpleOneOfSchemaTypePart1");
        assertEquals("SimpleOneOfSchemaTypePart1", oneOfSchemaTypePart1.getTypeName());
        assertEquals(3, oneOfSchemaTypePart1.getProperties().size());

        ObjectDataModel oneOfSchemaTypePart2 = childTypes.get("SimpleOneOfSchemaTypePart2");
        assertEquals("SimpleOneOfSchemaTypePart2", oneOfSchemaTypePart2.getTypeName());
        assertEquals(2, oneOfSchemaTypePart2.getProperties().size());
    }

    @Test
    void oneOfSchemaResolved() {
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
        assertEquals(2, oneOfSchemaTypePart1.getProperties().size());

        ObjectDataModel oneOfSchemaTypePart2 = childTypes.get("OneOfSchemaTypePart2");
        assertEquals("OneOfSchemaTypePart2", oneOfSchemaTypePart2.getTypeName());
        assertEquals(1, oneOfSchemaTypePart2.getProperties().size());
    }

    @Test
    void oneOfSchemaWithMappingResolved() {
        Schema<?> schema = openAPIComponentsIndex.schemaByName("OneOfSchemaTypeWithMapping");

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
        assertEquals(2, oneOfSchemaTypePart1.getProperties().size());

        ObjectDataModel oneOfSchemaTypePart2 = childTypes.get("OneOfSchemaTypePart2");
        assertEquals("OneOfSchemaTypePart2", oneOfSchemaTypePart2.getTypeName());
        assertEquals(1, oneOfSchemaTypePart2.getProperties().size());
    }
}
