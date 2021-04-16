package apicross.demo.myspace.domain;

import apicross.demo.common.models.AbstractEntity;
import apicross.demo.common.utils.IdGenerator;
import lombok.NonNull;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "work")
public class Work extends AbstractEntity {
    @Column(name = "user_id", length = 100)
    private String userId;
    @ManyToOne
    private Competition competition;
    @Embedded
    private WorkDescription workDescription;
    @Column(name = "placed_at")
    private LocalDate placedAt;
    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private WorkStatus status;
    @OneToMany
    private List<WorkFileReference> files;

    Work(@NonNull Competition competition, @NonNull User userId, @NonNull WorkDescription workDescription) {
        super(IdGenerator.newId(), 0);
        this.competition = competition;
        this.placedAt = LocalDate.now();
        this.userId = userId.getUsername();
        this.workDescription = workDescription;
    }

    protected Work() {
        super();
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return workDescription.getTitle();
    }

    public String getDescription() {
        return workDescription.getDescription();
    }

    public String getAuthor() {
        return workDescription.getAuthor();
    }

    public int getAuthorAge() {
        return workDescription.getAuthorAge();
    }

    public LocalDate getPlacedAt() {
        return placedAt;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public WorkFileReference addFile(String mediaType) {
        if (competition.getStatus() != CompetitionStatus.OPEN) {
            throw new IllegalCompetitionStatusException("Competition must be open", competition.getId(), competition.getStatus());
        }
        if(this.files == null) {
            this.files = new ArrayList<>();
        }
        WorkFileReference workFileReference = new WorkFileReference(this, mediaType);
        this.files.add(workFileReference);
        return workFileReference;
    }

    public Iterable<WorkFileReference> getFiles() {
        if(this.files == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(this.files);
        }
    }
}
