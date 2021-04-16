package com.github.itroadlabs.oas.apicross.core.handler.model;

import com.github.itroadlabs.oas.apicross.core.HasCustomModelAttributes;
import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;

import javax.annotation.Nonnull;
import java.util.Objects;

public class MediaTypeContentModel extends HasCustomModelAttributes {
    private final DataModel content;
    private final String mediaType;

    public MediaTypeContentModel(@Nonnull DataModel content, @Nonnull String mediaType) {
        this.content = Objects.requireNonNull(content, "'content' argument must not be null");
        this.mediaType = Objects.requireNonNull(mediaType, "'mediaType' argument must not be null");
    }

    @Nonnull
    public DataModel getContent() {
        return content;
    }

    @Nonnull
    public String getMediaType() {
        return mediaType;
    }
}
