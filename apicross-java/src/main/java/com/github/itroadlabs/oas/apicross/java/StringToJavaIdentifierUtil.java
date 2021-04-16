package com.github.itroadlabs.oas.apicross.java;

import com.github.itroadlabs.oas.apicross.CodeGeneratorException;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.CharUtils;

import javax.annotation.Nonnull;

public class StringToJavaIdentifierUtil {
    public static String resolve(@Nonnull String apiPropertyName) {
        String javaIdentifier = removeNonJavaSymbols(apiPropertyName);
        return javaIdentifier.contains("_") ? CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, javaIdentifier) : javaIdentifier;
    }

    public static String removeNonJavaSymbols(@Nonnull String apiPropertyName) {
        StringBuilder buff = new StringBuilder();

        for (int i = 0; i < apiPropertyName.length(); i++) {
            char ch = apiPropertyName.charAt(i);
            if (CharUtils.isAsciiAlpha(ch) || ch == '_' || ch == '$' || CharUtils.isAsciiNumeric(ch)) {
                buff.append(ch);
            }
        }

        String javaIdentifier = buff.toString();

        while (CharUtils.isAsciiNumeric(javaIdentifier.charAt(0)) || javaIdentifier.charAt(0) == '_') {
            if (javaIdentifier.length() > 1) {
                javaIdentifier = javaIdentifier.substring(1);
            } else {
                throw new CodeGeneratorException("Unable to resolve java identifier from property name '" + apiPropertyName + "'");
            }
        }
        return javaIdentifier;
    }
}
