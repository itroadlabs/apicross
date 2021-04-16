package apicross.demo.myspace.ports.adapters.db;

import apicross.demo.myspace.domain.Work;
import apicross.demo.myspace.domain.WorkNotFoundException;
import apicross.demo.myspace.domain.WorkRepository;
import lombok.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class WorkRepositoryDbAdapter implements WorkRepository {
    private final WorkDao workDao;

    WorkRepositoryDbAdapter(WorkDao workDao) {
        this.workDao = workDao;
    }

    @Override
    public void add(@NonNull Work work) {
        workDao.save(work);
    }

    @Override
    public List<Work> findAllForUser(@NonNull User user) {
        return workDao.findAllForUser(user.getUsername());
    }

    @Override
    public Work findForUser(@NonNull User user, @NonNull String workId) {
        return workDao.findForUser(user.getUsername(), workId).orElseThrow(() -> new WorkNotFoundException(user, workId));
    }

    @Override
    public void delete(@NonNull User user, @NonNull String workId) {
        Work work = findForUser(user, workId);
        workDao.delete(work);
    }
}
