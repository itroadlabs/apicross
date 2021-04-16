package apicross.demo.common.utils;

public interface ETagMatchPolicy {
    boolean matches(String etag);
}
