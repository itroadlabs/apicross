package apicross.demoapp.storefront.app;

import apicross.demoapp.storefront.app.dto.ListOpenCompetitionsQuery;
import apicross.demo.common.utils.ValidationStages;
import apicross.demo.common.models.Page;
import apicross.demo.common.models.PaginatedResult;
import apicross.demo.common.models.ModelConverter;
import apicross.demoapp.storefront.domain.Competition;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Collections;

@Service
@Validated(ValidationStages.class)
public class StorefrontService {
    public <T> PaginatedResult<T> search(@Valid ListOpenCompetitionsQuery queryParameters,
                                         ModelConverter<Competition, T> resultTransformer) {

        return new PaginatedResult<>(
                new Page(0, queryParameters.getPage(), queryParameters.getPageSize(), 0),
                Collections.emptyList());
    }
}
