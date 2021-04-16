package com.github.itroadlabs.oas.apicross.utils;

import io.swagger.v3.oas.models.media.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SchemaHelper {
    public static boolean isObjectSchema(Schema<?> schema) {
        return !((schema instanceof ArraySchema) || SchemaHelper.isPrimitiveTypeSchema(schema));
    }

    public static boolean isPrimitiveTypeSchema(Schema<?> schema) {
        return (schema instanceof StringSchema) || (schema instanceof BooleanSchema)
                || (schema instanceof NumberSchema) || (schema instanceof IntegerSchema)
                || (schema instanceof DateSchema) || (schema instanceof DateTimeSchema)
                || (schema instanceof UUIDSchema) || (schema instanceof ByteArraySchema)
                || (schema instanceof PasswordSchema) || (schema instanceof BinarySchema);
    }

    @Nonnull
    public static String schemaNameFromRef(@Nonnull String $ref) {
        return Objects.requireNonNull($ref).substring(StringUtils.lastIndexOf($ref, "/") + 1);
    }

    public static boolean isPrimitiveLikeSchema(Schema<?> schema) {
        return schema.getClass().equals(Schema.class)
                && schema.get$ref() == null
                && hasNoObjectSchemaAttributes(schema);
    }

    public static boolean isAnyObjectLikeSchema(Schema<?> schema) {
        return (schema instanceof ObjectSchema) && hasNoObjectSchemaAttributes(schema);
    }

    private static boolean hasNoObjectSchemaAttributes(Schema<?> schema) {
        return schema.getProperties() == null
                && schema.getRequired() == null
                && schema.getMinProperties() == null
                && schema.getMaxProperties() == null;
    }
}
