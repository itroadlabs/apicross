package apicross.demo.myspace.ports.adapters.web.models;

import apicross.demo.common.models.ModelConverter;
import apicross.demo.myspace.domain.Competition;

import java.util.List;

public class ListCompetitionsResponseRepresentationAssembler implements ModelConverter<List<Competition>, RpmCmListCompetitionsResponse> {
    @Override
    public RpmCmListCompetitionsResponse convert(List<Competition> source) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
