package apicross.demo.myspace.ports.adapters.web.models;

import apicross.demo.common.models.ModelConverter;
import apicross.demo.myspace.domain.Work;

import java.util.List;
import java.util.stream.Collectors;

public class ListWorksResponseRepresentationAssembler implements ModelConverter<List<Work>, RpmWpListWorksResponse> {
    @Override
    public RpmWpListWorksResponse convert(List<Work> source) {
        return new RpmWpListWorksResponse()
                .withWorks(transformWorks(source));
    }

    private List<RpmWorkSummary> transformWorks(List<Work> source) {
        return source.stream()
                .map(work -> new RpmWorkSummary()
                        .withAuthor(work.getAuthor())
                        .withTitle(work.getTitle())
                        .withPlacementDate(work.getPlacedAt())
                        .withStatus(work.getStatus().toString())) // TODO: encode according API specification
                .collect(Collectors.toList());
    }
}
