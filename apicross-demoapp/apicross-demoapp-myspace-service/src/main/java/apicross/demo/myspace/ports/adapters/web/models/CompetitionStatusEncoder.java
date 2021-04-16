package apicross.demo.myspace.ports.adapters.web.models;

import apicross.demo.myspace.domain.CompetitionStatus;

public class CompetitionStatusEncoder {
    public static String encodeStatus(CompetitionStatus status) {
        switch (status) {
            case OPEN:
                return "Open";
            case CLOSED:
                return "Closed";
            case VOTING:
                return "Voting";
            case PENDING:
                return "Pending";
            case REJECTED:
                return "Rejected";
            default:
                throw new IllegalArgumentException("Unsupported status: " + status);
        }
    }
}
