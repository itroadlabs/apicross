package com.github.itroadlabs.oas.apicross.core.handler.model;

import com.github.itroadlabs.oas.apicross.core.NamedDatum;
import com.github.itroadlabs.oas.apicross.core.data.model.DataModel;

public class RequestUriPathParameter extends NamedDatum {
    public RequestUriPathParameter(String name, String resolvedName, String description, DataModel type, boolean required, boolean deprecated) {
        super(name, resolvedName, description, type, required, deprecated);
    }
}
