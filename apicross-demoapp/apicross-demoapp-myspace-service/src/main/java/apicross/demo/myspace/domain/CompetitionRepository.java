package apicross.demo.myspace.domain;

import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface CompetitionRepository {
    void add(Competition competition);

    Competition findForUser(User user, String competitionId) throws CompetitionNotFoundException;

    List<Competition> findAllForUser(User user);

    void delete(User user, String competitionId);

    Competition find(String competitionId);
}
