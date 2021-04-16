package apicross.demo.myspace.domain;

import apicross.demo.common.models.AbstractEntity;
import apicross.demo.common.utils.IdGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "work_file")
public class WorkFileReference extends AbstractEntity {
    @Column(name = "media_type", length = 200)
    private String mediaType;
    @ManyToOne
    private Work work;

    WorkFileReference(Work work, String mediaType) {
        super(IdGenerator.newId(), 0);
        this.work = work;
        this.mediaType = mediaType;
    }

    protected WorkFileReference() {
        // infrastructure requirement
    }

    public String getMediaType() {
        return mediaType;
    }

    public Work getWork() {
        return work;
    }
}
