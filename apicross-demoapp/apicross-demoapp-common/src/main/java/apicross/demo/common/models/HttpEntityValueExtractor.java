package apicross.demo.common.models;

import org.springframework.http.HttpEntity;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.ValueExtractor;

public class HttpEntityValueExtractor implements ValueExtractor<HttpEntity<@ExtractedValue ?>> {
    @Override
    public void extractValues(HttpEntity<?> httpEntity, ValueReceiver valueReceiver) {
        if (httpEntity.hasBody()) {
            valueReceiver.value(null, httpEntity.getBody());
        }
    }
}
