package apicross.demo.myspace.app;

import apicross.demo.common.models.ModelConverter;
import apicross.demo.common.utils.*;
import apicross.demo.myspace.app.dto.IReadCmOpenCompetitionRequest;
import apicross.demo.myspace.app.dto.IReadCmRegisterCompetitionRequest;
import apicross.demo.myspace.app.dto.IReadCmUpdateCompetitionRequest;
import apicross.demo.myspace.app.dto.IReadParticipantRequirements;
import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.domain.CompetitionParticipantRequirements;
import apicross.demo.myspace.domain.CompetitionRepository;
import apicross.demo.myspace.domain.CompetitionVotingType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated({ValidationStages.class})
public class ManageCompetitionsService {
    private final CompetitionRepository competitionRepository;

    @Autowired
    public ManageCompetitionsService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @Transactional
    public EntityWithETag<Competition> registerNewCompetition(@NonNull User requestee,
                                                              @NonNull @Valid IReadCmRegisterCompetitionRequest command) {
        IReadParticipantRequirements participantReqs = command.getParticipantReqs();

        Competition competition = new Competition(
                requestee, command.getTitle(), command.getDescription(),
                new CompetitionParticipantRequirements(
                        participantReqs.getMinAgeOrElse(null),
                        participantReqs.getMaxAgeOrElse(null)),
                VotingTypeFactory.detectVotingType(command.getVotingType())
        );

        competitionRepository.add(competition);

        return new EntityWithETag<>(competition, competition::etag);
    }

    @Transactional
    public HasETag updateCompetition(@NonNull User requestee, @NonNull String competitionId,
                                     @NonNull @Valid IReadCmUpdateCompetitionRequest command,
                                     @NonNull ETagMatchPolicy ETagMatchPolicy) {
        Competition competition = competitionRepository.findForUser(requestee, competitionId);
        CompetitionPatch patch = new CompetitionPatch(ETagMatchPolicy, command);
        patch.apply(competition);
        return new HasETagSupplier(competition::etag);
    }

    @Transactional(readOnly = true)
    public <T> T listAllCompetitions(@NonNull User requestee, @NonNull ModelConverter<List<Competition>, T> modelConverter) {
        List<Competition> result = competitionRepository.findAllForUser(requestee);
        return modelConverter.convert(result);
    }

    @Transactional(readOnly = true)
    public <T> EntityWithETag<T> getCompetition(@NonNull User requestee, @NonNull String competitionId,
                                                @NonNull ModelConverter<Competition, T> modelConverter) {
        Competition competition = competitionRepository.findForUser(requestee, competitionId);
        return new EntityWithETag<>(modelConverter.convert(competition), competition::etag);
    }

    @Transactional
    public void deleteCompetition(@NonNull User requestee, @NonNull String competitionId) {
        competitionRepository.delete(requestee, competitionId);
    }

    @Transactional
    public void openCompetition(@NonNull User requestee, @NonNull String competitionId,
                                @NonNull @Valid IReadCmOpenCompetitionRequest command) {
        Competition competition = competitionRepository.findForUser(requestee, competitionId);
        competition.open(command.getAcceptWorksTillDate(), command.getAcceptVotesTillDate());
    }

    @Transactional
    public void startVoting(@NonNull User requestee, @NonNull String competitionId) {
        Competition competition = competitionRepository.findForUser(requestee, competitionId);
        competition.startVoting();
    }

    @Transactional
    public void closeCompetition(@NonNull User requestee, @NonNull String competitionId) {
        Competition competition = competitionRepository.findForUser(requestee, competitionId);
        competition.close();
    }

    static class CompetitionPatch extends ETagConditionalPatch<Competition> {
        private final IReadCmUpdateCompetitionRequest request;

        CompetitionPatch(@NonNull ETagMatchPolicy ETagMatchPolicy, @NonNull IReadCmUpdateCompetitionRequest request) {
            super(ETagMatchPolicy);
            this.request = request;
        }

        @Override
        protected void doPatch(Competition entityToBeUpdated) {
            entityToBeUpdated.setTitle(request.getTitleOrElse(null));
            entityToBeUpdated.setDescription(request.getDescriptionOrElse(null));
            if (request.isParticipantReqsPresent()) {
                IReadParticipantRequirements participantReqs = request.getParticipantReqs();
                entityToBeUpdated.setParticipantRequirements(new CompetitionParticipantRequirements(
                        participantReqs.getMinAgeOrElse(null),
                        participantReqs.getMaxAgeOrElse(null)));
            }
            entityToBeUpdated.setVotingType(VotingTypeFactory.detectVotingType(request.getVotingType()));
        }
    }

    static class VotingTypeFactory {
        static CompetitionVotingType detectVotingType(String votingType) {
            if ("ClapsVoting".equals(votingType)) {
                return CompetitionVotingType.CLAPS_VOTING;
            } else if ("PointsVoting".equals(votingType)) {
                return CompetitionVotingType.POINTS_VOTING;
            } else {
                throw new IllegalArgumentException("Unknown voting type: " + votingType);
            }
        }
    }
}
