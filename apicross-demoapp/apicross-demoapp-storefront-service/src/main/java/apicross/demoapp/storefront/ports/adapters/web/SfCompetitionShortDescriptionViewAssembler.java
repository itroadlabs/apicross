package apicross.demoapp.storefront.ports.adapters.web;

import apicross.demoapp.storefront.app.dto.SfCompetitionsShortDescriptionRepresentationModel;
import apicross.demoapp.storefront.domain.Competition;
import apicross.demo.common.models.ModelConverter;

public class SfCompetitionShortDescriptionViewAssembler implements ModelConverter<Competition, SfCompetitionsShortDescriptionRepresentationModel> {
    @Override
    public SfCompetitionsShortDescriptionRepresentationModel convert(Competition source) {
        // TODO: implement me!!
        throw new UnsupportedOperationException("not implemented yet");
    }
}
