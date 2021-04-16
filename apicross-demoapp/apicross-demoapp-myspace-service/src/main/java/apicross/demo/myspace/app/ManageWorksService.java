package apicross.demo.myspace.app;

import apicross.demo.common.models.ModelConverter;
import apicross.demo.common.utils.EntityWithETag;
import apicross.demo.common.utils.ValidationStages;
import apicross.demo.myspace.app.dto.IReadWpPlaceWorkRequest;
import apicross.demo.myspace.domain.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Service
@Validated({ValidationStages.class})
@Slf4j
public class ManageWorksService {
    private final WorkRepository workRepository;
    private final CompetitionRepository competitionRepository;
    private final WorkFileReferenceRepository workFileReferenceRepository;
    private final FilesStore filesStore;

    @Autowired
    public ManageWorksService(WorkRepository workRepository, CompetitionRepository competitionRepository,
                              WorkFileReferenceRepository workFileReferenceRepository, FilesStore filesStore) {
        this.workRepository = workRepository;
        this.competitionRepository = competitionRepository;
        this.workFileReferenceRepository = workFileReferenceRepository;
        this.filesStore = filesStore;
    }

    @Transactional
    public EntityWithETag<Work> placeWork(@NonNull String competitionId, @NonNull User user,
                                          @NonNull @Valid IReadWpPlaceWorkRequest command) {
        Competition competition = competitionRepository.find(competitionId);

        WorkDescription workDescription =
                new WorkDescription(
                        command.getTitle(), command.getDescription(),
                        command.getAuthor(), command.getAuthorAge());


        Work work = competition.addWork(user, workDescription);

        workRepository.add(work);

        return new EntityWithETag<>(work, work::etag);
    }

    @Transactional(readOnly = true)
    public <T> T listWorks(@NonNull User user, @NonNull ModelConverter<List<Work>, T> modelConverter) {
        List<Work> works = workRepository.findAllForUser(user);
        return modelConverter.convert(works);
    }

    @Transactional
    public void deleteWork(@NonNull User user, @NonNull String workId) {
        workRepository.delete(user, workId);
    }

    @Transactional(readOnly = true)
    public <T> T getWork(@NonNull User user, @NonNull String workId, @NonNull ModelConverter<Work, T> modelConverter) {
        Work work = workRepository.findForUser(user, workId);
        return modelConverter.convert(work);
    }

    @Transactional(rollbackFor = IOException.class)
    public WorkFileReference addWorkFile(@NonNull User user, @NonNull String workId,
                                         @NonNull InputStream content, @NonNull String mediaType) throws IOException {
        Work work = workRepository.findForUser(user, workId);
        WorkFileReference workFileReference = work.addFile(mediaType);
        workFileReferenceRepository.add(workFileReference);
        filesStore.save(workFileReference, content);
        return workFileReference;
    }

    @Transactional
    public void deleteWorkFile(@NonNull User user, @NonNull String fileId) {
        workFileReferenceRepository.delete(user.getUsername(), fileId);
        filesStore.delete(fileId);
    }

    public javax.activation.DataSource loadFileContent(Authentication authentication, @NonNull String fileId) throws IOException {
        WorkFileReference workFileReference = workFileReferenceRepository.findById(((User) authentication.getPrincipal()).getUsername(), fileId);
        return new InputStreamDataSource(filesStore.fileContent(fileId), workFileReference.getMediaType());
    }

    private static class InputStreamDataSource implements javax.activation.DataSource {
        private final InputStream inputStream;
        private final String contentType;

        public InputStreamDataSource(InputStream inputStream, String contentType) {
            this.inputStream = inputStream;
            this.contentType = contentType;
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        @Override
        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }
    }
}
