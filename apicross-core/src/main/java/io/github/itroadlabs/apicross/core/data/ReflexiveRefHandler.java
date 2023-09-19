package io.github.itroadlabs.apicross.core.data;

import io.github.itroadlabs.apicross.core.data.model.DataModel;

import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReflexiveRefHandler {
    private final Function<String, DataModel> dataModelByRefResolver;
    private final Stack<String> resolvingRefStack = new Stack<>();

    public ReflexiveRefHandler(Function<String, DataModel> dataModelByRefResolver) {
        this.dataModelByRefResolver = dataModelByRefResolver;
    }

    public void begin(String $ref) {
        resolvingRefStack.push($ref);
    }

    public void end() {
        resolvingRefStack.pop();
    }

    public boolean reflexiveRefDetected(String $ref) {
        if (!inHandling()) {
            throw new IllegalStateException();
        }
        return resolvingRefStack.contains($ref);
    }

    public Supplier<DataModel> resolveLater(String $ref) {
        return () -> {
            if (inHandling()) {
                throw new IllegalStateException();
            }
            return dataModelByRefResolver.apply($ref);
        };
    }

    private boolean inHandling() {
        return !resolvingRefStack.isEmpty();
    }
}
