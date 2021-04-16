package apicross.demo.myspace.domain;

import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface WorkRepository {
    void add(Work work);

    List<Work> findAllForUser(User user);

    Work findForUser(User user, String workId) throws WorkNotFoundException;

    void delete(User user, String workId) throws WorkNotFoundException;
}
