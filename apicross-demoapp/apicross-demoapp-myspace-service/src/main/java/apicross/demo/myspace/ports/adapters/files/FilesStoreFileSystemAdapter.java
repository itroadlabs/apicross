package apicross.demo.myspace.ports.adapters.files;

import apicross.demo.myspace.app.FilesStore;
import apicross.demo.myspace.domain.WorkFileReference;
import lombok.NonNull;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

@Component
class FilesStoreFileSystemAdapter implements FilesStore {
    private File directory;

    public FilesStoreFileSystemAdapter() {
    }

    @PostConstruct
    protected void init() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        this.directory = new File(tmpDir, "competition-work-files");
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
    }

    @Override
    public void save(@NonNull WorkFileReference workFileReference, @NonNull InputStream content) throws IOException {
        File file = new File(directory, workFileReference.getId());
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            IOUtils.copy(content, out);
            out.flush();
        }
    }

    @Override
    public void delete(String fileId) {
        File file = new File(directory, fileId);
        file.delete();
    }

    @Override
    public InputStream fileContent(String fileId) throws FileNotFoundException {
        File file = new File(directory, fileId);
        return new BufferedInputStream(new FileInputStream(file));
    }
}
