package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.utils.ETagDoesntMatchException;
import apicross.demo.common.utils.EntityWithETag;
import apicross.demo.common.utils.HasETag;
import apicross.demo.common.utils.HttpEtagETagMatchPolicy;
import apicross.demo.myspace.app.ManageCompetitionsService;
import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.ports.adapters.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@RestController
public class CompetitionsRequestsController implements CompetitionsRequestsHandler {
    private final ManageCompetitionsService manageCompetitionsService;

    @Autowired
    public CompetitionsRequestsController(ManageCompetitionsService manageCompetitionsService) {
        this.manageCompetitionsService = manageCompetitionsService;
    }

    @Override
    public ResponseEntity<?> registerNewCompetitionConsumeVndDemoappV1Json(HttpHeaders headers,
                                                                           Authentication authentication,
                                                                           RpmCmRegisterCompetitionRequest requestEntity) {
        EntityWithETag<Competition> registerCompetitionResult =
                manageCompetitionsService.registerNewCompetition((User) authentication.getPrincipal(), requestEntity);
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(registerCompetitionResult.getEntity().getId());
        return ResponseEntity.created(location.toUri())
                .eTag(registerCompetitionResult.etag())
                .build();
    }

    @Override
    public ResponseEntity<RpmCmListCompetitionsResponse> listCompetitionsProduceVndDemoappV1Json(HttpHeaders headers,
                                                                                                 Authentication authentication) {
        RpmCmListCompetitionsResponse response =
                manageCompetitionsService.listAllCompetitions((User) authentication.getPrincipal(),
                        new ListCompetitionsResponseRepresentationAssembler());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteCompetition(String competitionId, HttpHeaders headers,
                                               Authentication authentication) {
        manageCompetitionsService.deleteCompetition((User) authentication.getPrincipal(), competitionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RpmCmGetCompetitionResponse> getCompetitionDescriptionProduceVndDemoappV1Json(String competitionId, HttpHeaders headers,
                                                                                                        Authentication authentication) {
        EntityWithETag<RpmCmGetCompetitionResponse> response =
                manageCompetitionsService.getCompetition((User) authentication.getPrincipal(), competitionId,
                        new GetCompetitionResponseRepresentationAssembler());
        return ResponseEntity.status(HttpStatus.OK)
                .eTag(response.etag())
                .body(response.getEntity());
    }

    @Override
    public ResponseEntity<?> updateCompetitionConsumeVndDemoappV1Json(String competitionId, HttpHeaders headers,
                                                                      Authentication authentication,
                                                                      RpmCmUpdateCompetitionRequest requestEntity) {
        try {
            HasETag outcome = manageCompetitionsService.updateCompetition((User) authentication.getPrincipal(), competitionId,
                    requestEntity, new HttpEtagETagMatchPolicy(headers));
            return ResponseEntity.noContent().eTag(outcome.etag()).build();
        } catch (ETagDoesntMatchException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }

    @Override
    public ResponseEntity<?> openCompetitionConsumeVndDemoappV1Json(String competitionId, HttpHeaders headers,
                                                                    Authentication authentication,
                                                                    RpmCmOpenCompetitionRequest requestEntity) {
        manageCompetitionsService.openCompetition((User) authentication.getPrincipal(), competitionId, requestEntity);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> startCompetitionVoting(String competitionId, HttpHeaders headers,
                                                    Authentication authentication) {
        manageCompetitionsService.startVoting((User) authentication.getPrincipal(), competitionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> closeCompetition(String competitionId, HttpHeaders headers,
                                              Authentication authentication) {
        manageCompetitionsService.closeCompetition((User) authentication.getPrincipal(), competitionId);
        return ResponseEntity.noContent().build();
    }
}
