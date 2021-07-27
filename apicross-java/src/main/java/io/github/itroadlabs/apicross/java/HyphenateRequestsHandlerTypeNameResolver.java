package io.github.itroadlabs.apicross.java;

import io.github.itroadlabs.apicross.CodeGeneratorException;
import io.github.itroadlabs.apicross.core.handler.RequestsHandlerTypeNameResolver;
import io.github.itroadlabs.apicross.core.handler.model.HttpOperationsGroup;

import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class HyphenateRequestsHandlerTypeNameResolver implements RequestsHandlerTypeNameResolver {
    @Override
    public String resolve(HttpOperationsGroup httpOperationsGroup) {
        String normalizedToHyphenateJavaIdentifier = normalizeToHyphenateJavaIdentifier(httpOperationsGroup.getName());

        return LOWER_HYPHEN.to(UPPER_CAMEL, normalizedToHyphenateJavaIdentifier) + "RequestsHandler";
    }

    private static String normalizeToHyphenateJavaIdentifier(String name) {
        StringBuilder result = new StringBuilder();
        boolean firstSymbol = true;

        for (char b : name.toCharArray()) {
            if (firstSymbol) {
                if (Character.isJavaIdentifierStart(b)) {
                    result.append(b);
                    firstSymbol = false;
                }
            } else {
                if (b == '-' || Character.isJavaIdentifierPart(b)) {
                    result.append(b);
                }
            }
        }
        String normalizedToJavaIdentifier = result.toString();

        if (normalizedToJavaIdentifier.isEmpty()) {
            throw new CodeGeneratorException("Invalid identifier");
        }
        return normalizedToJavaIdentifier;
    }
}
