package apicross.demo.myspace.domain;

import lombok.NonNull;

public class WorkFileReferenceNotFoundException extends RuntimeException {
    private final String fileId;

    public WorkFileReferenceNotFoundException(@NonNull String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }
}
