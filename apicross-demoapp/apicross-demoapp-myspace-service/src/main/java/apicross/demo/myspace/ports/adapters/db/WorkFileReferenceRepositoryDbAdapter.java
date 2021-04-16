package apicross.demo.myspace.ports.adapters.db;

import apicross.demo.myspace.domain.WorkFileReference;
import apicross.demo.myspace.domain.WorkFileReferenceNotFoundException;
import apicross.demo.myspace.domain.WorkFileReferenceRepository;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
class WorkFileReferenceRepositoryDbAdapter implements WorkFileReferenceRepository {
    private final WorkFileReferenceDao workFileReferenceDao;

    WorkFileReferenceRepositoryDbAdapter(WorkFileReferenceDao workFileReferenceDao) {
        this.workFileReferenceDao = workFileReferenceDao;
    }

    @Override
    public void add(@NonNull WorkFileReference workFileReference) {
        workFileReferenceDao.save(workFileReference);
    }

    @Override
    public void delete(@NonNull String username, @NonNull String fileId) {
        workFileReferenceDao.delete(findById(username, fileId));
    }

    @Override
    public WorkFileReference findById(@NonNull String username, @NonNull String fileId) {
        return workFileReferenceDao.findById(fileId).orElseThrow(() -> new WorkFileReferenceNotFoundException(fileId));
    }
}
