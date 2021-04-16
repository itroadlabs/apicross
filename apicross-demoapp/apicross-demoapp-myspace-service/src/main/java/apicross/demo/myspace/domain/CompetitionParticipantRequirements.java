package apicross.demo.myspace.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CompetitionParticipantRequirements {
    @Column(name = "participant_req_min_age")
    private Integer minAge;
    @Column(name = "participant_req_max_age")
    private Integer maxAge;

    public CompetitionParticipantRequirements(Integer minAge, Integer maxAge) {
        if (minAge == null && maxAge == null) {
            throw new IllegalArgumentException("'minAge' and 'maxAge' both must not be null");
        }
        if ((minAge != null && maxAge != null) && (maxAge < minAge)) {
            throw new IllegalArgumentException("'maxAge' must not be less than 'minAge'");
        }
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    protected CompetitionParticipantRequirements() {
        // infrastructure requirement
    }

    public Integer getMinAge() {
        return minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public boolean isAuthorAgeSatisfied(int authorAge) {
        return ((minAge == null) || (authorAge >= minAge)) && ((maxAge == null) || (authorAge <= maxAge));
    }

    public boolean isSatisfiedBy(WorkDescription workDescription) {
        return isAuthorAgeSatisfied(workDescription.getAuthorAge());
    }
}
