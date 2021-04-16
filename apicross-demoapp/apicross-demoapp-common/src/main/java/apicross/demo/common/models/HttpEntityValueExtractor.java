package apicross.demo.common.models;

import org.springframework.http.HttpEntity;

import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.ValueExtractor;

public class HttpEntityValueExtractor implements ValueExtractor<HttpEntity<@ExtractedValue ?>> {
    @Override
    public void extractValues(HttpEntity<?> httpEntity, ValueReceiver valueReceiver) {
        if (httpEntity.hasBody()) {
            valueReceiver.value(null, httpEntity.getBody());
        }
    }
}
