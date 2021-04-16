package apicross.demo.myspace.domain;

import lombok.Getter;

@Getter
public class IllegalCompetitionStatusException extends RuntimeException {
    private final String competitionId;
    private final CompetitionStatus actualStatus;

    public IllegalCompetitionStatusException(String message, String competitionId, CompetitionStatus actualStatus) {
        super(message);
        this.competitionId = competitionId;
        this.actualStatus = actualStatus;
    }
}
