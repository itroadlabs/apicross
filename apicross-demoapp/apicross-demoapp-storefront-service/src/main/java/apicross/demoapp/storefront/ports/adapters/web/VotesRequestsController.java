package apicross.demoapp.storefront.ports.adapters.web;

import apicross.demoapp.storefront.app.VotesService;
import apicross.demoapp.storefront.app.dto.VtCompetitionResultsResponseRepresentationModel;
import apicross.demoapp.storefront.app.dto.VtVoteRequestRepresentationModel;
import apicross.demoapp.storefront.app.dto.VtVotesResponseRepresentationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VotesRequestsController implements VotesRequestsHandler {
    private final VotesService votesService;

    @Autowired
    public VotesRequestsController(VotesService votesService) {
        this.votesService = votesService;
    }

    @Override
    public ResponseEntity<?> vote(String workId, HttpHeaders headers, VtVoteRequestRepresentationModel requestEntity) {
        votesService.voteForWork(workId, requestEntity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<VtVotesResponseRepresentationModel> listVotes(String workId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public ResponseEntity<VtCompetitionResultsResponseRepresentationModel> getContestResults(String competitionId, HttpHeaders headers) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
