package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.utils.EntityWithETag;
import apicross.demo.myspace.app.ManageWorksService;
import apicross.demo.myspace.domain.Work;
import apicross.demo.myspace.domain.WorkFileReference;
import apicross.demo.myspace.ports.adapters.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class WorksRequestsController implements WorksRequestsHandler {
    private final ManageWorksService manageWorksService;

    @Autowired
    public WorksRequestsController(ManageWorksService manageWorksService) {
        this.manageWorksService = manageWorksService;
    }

    @Override
    public ResponseEntity<?> deleteWork(String workId, HttpHeaders headers, Authentication authentication) {
        manageWorksService.deleteWork((User) authentication.getPrincipal(), workId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RpmWpListWorksResponse> listWorksProduceVndDemoappV1Json(HttpHeaders headers, Authentication authentication) {
        RpmWpListWorksResponse response = manageWorksService.listWorks((User) authentication.getPrincipal(), new ListWorksResponseRepresentationAssembler());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> placeWorkConsumeVndDemoappV1Json(String competitionId, HttpHeaders headers, Authentication authentication, RpmWpPlaceWorkRequest requestBody) {
        final EntityWithETag<Work> placeWorkResult = manageWorksService.placeWork(competitionId, (User) authentication.getPrincipal(), requestBody);
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(placeWorkResult.getEntity().getId());
        return ResponseEntity.created(location.toUri())
                .eTag(placeWorkResult.etag())
                .build();
    }

    @Override
    public ResponseEntity<RpmWpGetWorkResponse> getWorkDescriptionProduceVndDemoappV1Json(String workId, HttpHeaders headers, Authentication authentication) {
        RpmWpGetWorkResponse response = manageWorksService.getWork((User) authentication.getPrincipal(), workId, new GetWorkResponseRepresentationAssembler());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeImageJpeg(String workId, HttpHeaders headers,
                                                         Authentication authentication, InputStream requestEntity) throws IOException {
        return addWorkFile((User) authentication.getPrincipal(), workId, requestEntity, "image/jpeg");
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeAudioMp4(String workId, HttpHeaders headers,
                                                        Authentication authentication, InputStream requestEntity) throws IOException {
        return addWorkFile((User) authentication.getPrincipal(), workId, requestEntity, "audio/mp4");
    }

    @Override
    public ResponseEntity<?> addWorkFileConsumeVideoMp4(String workId, HttpHeaders headers,
                                                        Authentication authentication, InputStream requestEntity) throws IOException {
        return addWorkFile((User) authentication.getPrincipal(), workId, requestEntity, "video/mp4");
    }

    @Override
    public ResponseEntity<?> deleteWorkFile(String fileId, HttpHeaders headers,
                                            Authentication authentication) {
        manageWorksService.deleteWorkFile((User) authentication.getPrincipal(), fileId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadWorkFileProduceImageJpeg(String fileId, HttpHeaders headers,
                                                                                Authentication authentication) {
        return downloadFile(fileId, "image/jpeg", authentication);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadWorkFileProduceAudioMp4(String fileId, HttpHeaders headers,
                                                                               Authentication authentication) {
        return downloadFile(fileId, "audio/mp4", authentication);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadWorkFileProduceVideoMp4(String fileId, HttpHeaders headers,
                                                                               Authentication authentication) {
        return downloadFile(fileId, "video/mp4", authentication);
    }

    private ResponseEntity<?> addWorkFile(User principal, String workId, InputStream requestEntity, String mediaType) throws IOException {
        try {
            WorkFileReference result = manageWorksService.addWorkFile(principal, workId, requestEntity, mediaType);

            UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest().path("/my/files/{fileId}")
                    .buildAndExpand(result.getId());

            return ResponseEntity.created(location.toUri())
                    .build();
        } finally {
            requestEntity.close();
        }
    }

    private ResponseEntity<InputStreamResource> downloadFile(String fileId, String requiredMediaType, Authentication authentication) {
        try {
            DataSource dataSource = manageWorksService.loadFileContent(authentication, fileId);
            if (requiredMediaType.equals(dataSource.getContentType())) {
                return ResponseEntity.ok(new InputStreamResource(dataSource.getInputStream()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
