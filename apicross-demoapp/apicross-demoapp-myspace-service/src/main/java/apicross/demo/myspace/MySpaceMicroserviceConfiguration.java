package apicross.demo.myspace;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;

@Configuration
@EnableJpaRepositories
public class MySpaceMicroserviceConfiguration {
    @Bean
    public Validator validator() {
        return Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(
                        new ResourceBundleMessageInterpolator(
                                new AggregateResourceBundleLocator(
                                        Collections.singletonList(
                                                "apicross/beanvalidation/ValidationMessages"
                                        )
                                )
                        )
                )
                .buildValidatorFactory()
                .getValidator();
    }
}
