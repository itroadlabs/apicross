package apicross.demo.myspace.domain;

import lombok.NonNull;

public class CompetitionNotFoundException extends RuntimeException {
    private final String competitionId;

    public CompetitionNotFoundException(@NonNull String competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionId() {
        return competitionId;
    }
}
