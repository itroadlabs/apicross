package io.github.itroadlabs.apicross.java;

import io.github.itroadlabs.apicross.CodeGeneratorException;
import io.github.itroadlabs.apicross.core.handler.model.HttpOperationsGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HyphenateRequestsHandlerTypeNameResolverTest {

    @Test
    void validHyphenIdentifiersResolvedForTypeName() {
        String tagName = "hyphenate-tag-name-v1";

        String actualName = new HyphenateRequestsHandlerTypeNameResolver().resolve(new HttpOperationsGroup(tagName));

        assertEquals("HyphenateTagNameV1RequestsHandler", actualName);
    }

    @Test
    void upperValidHyphenIdentifiersResolvedForTypeName() {
        String tagName = "HYPHENATE-TAG-NAME-V1";

        String actualName = new HyphenateRequestsHandlerTypeNameResolver().resolve(new HttpOperationsGroup(tagName));

        assertEquals("HyphenateTagNameV1RequestsHandler", actualName);
    }

    @Test
    void whitespaceHyphenIdentifiersResolvedForTypeName() {
        String tagName = " HYPHENATE- TAG-NAME-V1 ";

        String actualName = new HyphenateRequestsHandlerTypeNameResolver().resolve(new HttpOperationsGroup(tagName));

        assertEquals("HyphenateTagNameV1RequestsHandler", actualName);
    }

    @Test
    void nonJavaSymbolHyphenIdentifiersResolvedForTypeName() {
        String tagName = "hyp  hena!te-tag-n@%ame-v1";

        String actualName = new HyphenateRequestsHandlerTypeNameResolver().resolve(new HttpOperationsGroup(tagName));

        assertEquals("HyphenateTagNameV1RequestsHandler", actualName);
    }

    @Test
    void nonJavaSymbolFirstHyphenateHyphenIdentifiersResolvedForTypeName() {
        String tagName = "--hyphenate-tag-name-v1";

        String actualName = new HyphenateRequestsHandlerTypeNameResolver().resolve(new HttpOperationsGroup(tagName));

        assertEquals("HyphenateTagNameV1RequestsHandler", actualName);
    }

    @Test
    void invalidIdentifierHyphenIdentifiersResolvedForTypeName() {
        String tagName = "--?%";

        Assertions.assertThrows(CodeGeneratorException.class, () -> new HyphenateRequestsHandlerTypeNameResolver().resolve(new HttpOperationsGroup(tagName)));

    }
}
