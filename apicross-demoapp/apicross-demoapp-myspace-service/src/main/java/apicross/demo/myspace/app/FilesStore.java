package apicross.demo.myspace.app;

import apicross.demo.myspace.domain.WorkFileReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FilesStore {
    void save(WorkFileReference workFileReference, InputStream content) throws IOException;

    void delete(String fileId);

    InputStream fileContent(String fileId) throws FileNotFoundException;
}
