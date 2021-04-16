package com.github.itroadlabs.oas.apicross.java;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

public class DefaultMethodNameBuilder {
    private String operationId;
    private String consumingsMediaType = null;
    private String producingMediaType = null;

    public DefaultMethodNameBuilder operationId(@NonNull String operationId) {
        Preconditions.checkArgument(!StringUtils.isBlank(operationId), "'operationId' argument is blank string");
        this.operationId = operationId;
        return this;
    }

    public DefaultMethodNameBuilder consumingsMediaType(String consumingsMediaType) {
        this.consumingsMediaType = consumingsMediaType;
        return this;
    }

    public DefaultMethodNameBuilder producingMediaType(String producingMediaType) {
        Preconditions.checkArgument((producingMediaType == null) || !producingMediaType.contains("*"),
                "'*' symbol is not allowed in 'producingMediaType' argument - i.e. server handler always must return specific content media-type");
        this.producingMediaType = producingMediaType;
        return this;
    }

    public String build() {
        Preconditions.checkState(!StringUtils.isEmpty(operationId), "'operationId' was not defined");

        StringBuilder methodNameBuilder = new StringBuilder(operationId);

        boolean consumesSpecificType = !(StringUtils.isEmpty(consumingsMediaType) || "*/*".equals(consumingsMediaType));
        boolean producesSpecificType = !StringUtils.isEmpty(producingMediaType);

        String targetConsumingMediaType = consumingsMediaType == null ? "" : consumingsMediaType;
        String targetProducingMediaType = producingMediaType == null ? "" : producingMediaType;
        if (consumesSpecificType && targetConsumingMediaType.endsWith("/*")) {
            targetConsumingMediaType = targetConsumingMediaType.replace("*", "any-sub-type");
        } else if (consumesSpecificType && targetConsumingMediaType.startsWith("*/")) {
            targetConsumingMediaType = targetConsumingMediaType.replace("*", "any-type");
        }
        if (targetConsumingMediaType.equals("text/plain")) {
            targetConsumingMediaType = "plain-text";
        }
        if (targetProducingMediaType.equals("text/plain")) {
            targetProducingMediaType = "plain-text";
        }

        if (consumesSpecificType && producesSpecificType) {
            methodNameBuilder.append("Consume").append(mediaTypeMethodNamePart(targetConsumingMediaType));
            methodNameBuilder.append("Produce").append(mediaTypeMethodNamePart(targetProducingMediaType));
        } else if (consumesSpecificType) {
            methodNameBuilder.append("Consume").append(mediaTypeMethodNamePart(targetConsumingMediaType));
        } else if (producesSpecificType) {
            methodNameBuilder.append("Produce").append(mediaTypeMethodNamePart(targetProducingMediaType));
        }
        return methodNameBuilder.toString();
    }

    private static String mediaTypeMethodNamePart(String mediaType) {
        String[] parts;
        StringBuilder builder = new StringBuilder();
        if (useOnlyMediaSubTypeInMethodName(mediaType)) {
            int index = mediaType.indexOf('/');
            String subtype;
            if (index >= 0) {
                subtype = mediaType.substring(index + 1);
            } else {
                subtype = mediaType;
            }
            subtype = subtype.replaceAll("\\.", "-");
            subtype = subtype.replaceAll("\\+", "-");
            parts = subtype.split("-");
        } else {
            String type = mediaType;
            type = type.replaceAll("/", "-");
            type = type.replaceAll("\\.", "-");
            type = type.replaceAll("\\+", "-");
            parts = type.split("-");
        }
        for (String part : parts) {
            builder.append(StringUtils.capitalize(part));
        }
        return builder.toString();
    }

    private static boolean useOnlyMediaSubTypeInMethodName(String mediaType) {
        return isVendorLikeMediaType(mediaType) || isWellKnownMediaType(mediaType);
    }

    private static boolean isVendorLikeMediaType(String mediaType) {
        // See https://tools.ietf.org/html/rfc6838
        return mediaType.contains("/vnd.") || mediaType.contains("/x.") || mediaType.contains("/x-");
    }

    private static boolean isWellKnownMediaType(String mediaType) {
        // JSON, XML, PDF, HTML, XHTML are widely used and well known, so in method name we can use short form, JSON for example
        return mediaType.equals("application/json")
                || mediaType.equals("application/xml")
                || mediaType.equals("application/pdf")
                || mediaType.equals("text/xml")
                || mediaType.equals("text/html")
                || mediaType.equals("text/xhtml");
    }
}
