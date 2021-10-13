package io.github.itroadlabs.apicross.core.handler.model;

import io.github.itroadlabs.apicross.core.NamedDatum;
import io.github.itroadlabs.apicross.core.data.model.DataModel;

public class RequestQueryParameter extends NamedDatum {
    private final boolean jsonEncoding;

    public RequestQueryParameter(String name, String resolvedName, String description, DataModel type, boolean required, boolean deprecated) {
        this(name, resolvedName, description, type, required, deprecated, false);
    }

    public RequestQueryParameter(String name, String resolvedName, String description, DataModel type, boolean required, boolean deprecated, boolean jsonEncoding) {
        super(name, resolvedName, description, type, required, deprecated);
        this.jsonEncoding = jsonEncoding;
    }

    public boolean isJsonEncoding() {
        return jsonEncoding;
    }
}
