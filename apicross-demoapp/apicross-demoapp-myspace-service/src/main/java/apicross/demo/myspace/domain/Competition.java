package apicross.demo.myspace.domain;

import apicross.demo.common.models.AbstractEntity;
import apicross.demo.common.utils.IdGenerator;
import lombok.NonNull;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "competition")
public class Competition extends AbstractEntity {
    @Column(name = "organizer_user_id", length = 100)
    private String organizerUserId;
    @Column(name = "title", length = 100)
    private String title;
    @Column(name = "description", length = 1000)
    private String description;
    @Embedded
    private CompetitionParticipantRequirements participantRequirements;
    @Column(name = "voting_type", length = 100)
    @Enumerated(EnumType.STRING)
    private CompetitionVotingType votingType;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private CompetitionStatus status;
    @Column(name = "accept_works_till")
    private LocalDate acceptWorksTillDate;
    @Column(name = "accept_votes_till")
    private LocalDate acceptVotesTillDate;
    @OneToMany
    private List<Work> works;

    public Competition(@NonNull User competitionOrganizer, @NonNull String title,
                       String description,
                       @NonNull CompetitionParticipantRequirements participantRequirements,
                       @NonNull CompetitionVotingType votingType) {
        super(IdGenerator.newId(), 0);
        this.organizerUserId = competitionOrganizer.getUsername();
        this.status = CompetitionStatus.PENDING;
        this.registeredAt = LocalDateTime.now();
        this.title = title;
        this.description = description;
        this.participantRequirements = participantRequirements;
        this.votingType = votingType;
    }

    protected Competition() {
        super();
        // infrastructure requirement
    }

    public String getOrganizerUserId() {
        return organizerUserId;
    }

    public String getTitle() {
        return title;
    }

    public Competition setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Competition setDescription(String description) {
        this.description = description;
        return this;
    }

    public CompetitionParticipantRequirements getParticipantRequirements() {
        return participantRequirements;
    }

    public Competition setParticipantRequirements(CompetitionParticipantRequirements participantRequirements) {
        this.participantRequirements = participantRequirements;
        return this;
    }

    public CompetitionVotingType getVotingType() {
        return votingType;
    }

    public Competition setVotingType(CompetitionVotingType votingType) {
        this.votingType = votingType;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public CompetitionStatus getStatus() {
        return status;
    }

    public LocalDate getAcceptWorksTillDate() {
        return acceptWorksTillDate;
    }

    public LocalDate getAcceptVotesTillDate() {
        return acceptVotesTillDate;
    }

    public Iterable<Work> getWorks() {
        if (works == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(works);
        }
    }

    public void open(LocalDate acceptWorksTillDate, LocalDate acceptVotesTillDate) {
        if (status != CompetitionStatus.PENDING) {
            throw new IllegalCompetitionStatusException("Unable to open competition", getId(), getStatus());
        }
        this.status = CompetitionStatus.OPEN;
        this.acceptWorksTillDate = acceptWorksTillDate;
        this.acceptVotesTillDate = acceptVotesTillDate;
    }

    public Work addWork(User owner, WorkDescription workDescription) {
        if (getStatus() != CompetitionStatus.OPEN) {
            throw new IllegalCompetitionStatusException("Competition must be open for works placement", getId(), getStatus());
        }
        if (!participantRequirements.isSatisfiedBy(workDescription)) {
            throw new WorkDoesntMeetCompetitionParticipantRequirementsException(this.getId(), participantRequirements, workDescription);
        }
        if (works == null) {
            works = new ArrayList<>();
        }
        Work work = new Work(this, owner, workDescription);
        works.add(work);
        return work;
    }

    public void startVoting() {
        if (status != CompetitionStatus.OPEN) {
            throw new IllegalCompetitionStatusException("Competition must be open first", getId(), getStatus());
        }
        this.status = CompetitionStatus.VOTING;
    }

    public void close() {
        if (status == CompetitionStatus.CLOSED) {
            throw new IllegalCompetitionStatusException("Competition already closed", getId(), getStatus());
        }
        this.status = CompetitionStatus.CLOSED;
    }
}
