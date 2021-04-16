package apicross.demo.myspace.ports.adapters.db;

import apicross.demo.myspace.domain.Competition;
import apicross.demo.myspace.domain.CompetitionNotFoundException;
import apicross.demo.myspace.domain.CompetitionRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class CompetitionRepositoryDbAdapter implements CompetitionRepository {
    private final CompetitionDao competitionDao;

    public CompetitionRepositoryDbAdapter(CompetitionDao competitionDao) {
        this.competitionDao = competitionDao;
    }

    @Override
    public void add(Competition competition) {
        competitionDao.save(competition);
    }


    @Override
    public Competition findForUser(User user, String competitionId) {
        Optional<Competition> competitionOpt = competitionDao.findById(competitionId);
        if (competitionOpt.isPresent()) {
            Competition competition = competitionOpt.get();
            if (competition.getOrganizerUserId().equals(user.getUsername())) {
                return competition;
            }
        }
        throw new CompetitionNotFoundException(competitionId);
    }

    @Override
    public List<Competition> findAllForUser(User user) {
        return competitionDao.findAllForUserId(user.getUsername());
    }

    @Override
    public void delete(User user, String competitionId) {
        Competition competition = findForUser(user, competitionId);
        competitionDao.delete(competition);
    }

    @Override
    public Competition find(String competitionId) {
        Optional<Competition> optionalCompetition = competitionDao.findById(competitionId);
        return optionalCompetition.orElseThrow(() -> new CompetitionNotFoundException(competitionId));
    }
}
