package apicross.demo.common.models;

public interface ModelConverter<S, R> {
    R convert(S source);
}
