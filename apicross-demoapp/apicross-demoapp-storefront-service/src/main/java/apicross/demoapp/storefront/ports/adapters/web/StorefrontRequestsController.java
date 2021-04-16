package apicross.demoapp.storefront.ports.adapters.web;

import apicross.demoapp.storefront.app.dto.*;
import apicross.demo.common.models.PaginatedResult;
import apicross.demoapp.storefront.app.StorefrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StorefrontRequestsController implements StorefrontRequestsHandler {
    private final StorefrontService storefrontService;

    @Autowired
    public StorefrontRequestsController(StorefrontService storefrontService) {
        this.storefrontService = storefrontService;
    }

    @Override
    public ResponseEntity<SfListCompetitionsResponseRepresentationModel> listOpenCompetitions(ListOpenCompetitionsQuery queryParameters, HttpHeaders headers) {
        PaginatedResult<SfCompetitionsShortDescriptionRepresentationModel> result = storefrontService.search(queryParameters, new SfCompetitionShortDescriptionViewAssembler());
        return ResponseEntity.ok(new SfListCompetitionsResponseRepresentationModel()
                .withPage(result.getPage())
                .withPageContent(result.getContent()));
    }

    @Override
    public ResponseEntity<SfGetCompetitionResponseRepresentationModel> getCompetitionDescription(String competitionId, GetCompetitionDescriptionQuery queryParameters, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<SfListCompetitionWorksResponseRepresentationModel> listCompetitionWorks(String competitionId, ListCompetitionWorksQuery queryParameters, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<SfGetCompetitionWorkResponseRepresentationModel> getWork(String workId, GetWorkQuery queryParameters, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<InputStreamResource> getWorkMediaProduceImageJpeg(String fileId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<InputStreamResource> getWorkMediaProduceAudioMp4(String fileId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<InputStreamResource> getWorkMediaProduceVideoMp4(String fileId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
