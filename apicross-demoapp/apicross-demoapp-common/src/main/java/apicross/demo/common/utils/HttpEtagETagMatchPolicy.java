package apicross.demo.common.utils;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HttpEtagETagMatchPolicy implements ETagMatchPolicy {
    private final Collection<String> requiredEtags;

    public HttpEtagETagMatchPolicy(@NonNull HttpHeaders headers) {
        this.requiredEtags = prepareETags(headers.getIfMatch());
    }

    @Override
    public boolean matches(@NonNull String etag) {
        return requiredEtags.contains(etag);
    }

    private Collection<String> prepareETags(List<String> ifMatch) {
        if (CollectionUtils.isEmpty(ifMatch)) {
            return Collections.emptySet();
        }
        return ifMatch.stream().map(this::removeLeadingAndTrailingDoubleQuotes)
                .collect(Collectors.toSet());
    }

    private String removeLeadingAndTrailingDoubleQuotes(String etag) {
        String result = etag;
        if (etag.startsWith("\"")) {
            result = result.substring(1);
        }
        if (etag.endsWith("\"")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
