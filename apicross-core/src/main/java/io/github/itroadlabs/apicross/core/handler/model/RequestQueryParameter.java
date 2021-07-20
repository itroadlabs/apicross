package io.github.itroadlabs.apicross.core.handler.model;

import io.github.itroadlabs.apicross.core.NamedDatum;
import io.github.itroadlabs.apicross.core.data.model.DataModel;

public class RequestQueryParameter extends NamedDatum {
    public RequestQueryParameter(String name, String resolvedName, String description, DataModel type, boolean required, boolean deprecated) {
        super(name, resolvedName, description, type, required, deprecated);
    }
}
