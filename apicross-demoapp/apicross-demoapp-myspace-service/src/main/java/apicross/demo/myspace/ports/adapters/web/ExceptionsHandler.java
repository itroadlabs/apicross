package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.models.ProblemDescription;
import apicross.demo.myspace.domain.*;
import apicross.demo.myspace.ports.adapters.web.models.CompetitionStatusEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler extends apicross.demo.common.utils.ExceptionsHandler {
    @ExceptionHandler({CompetitionNotFoundException.class})
    public ResponseEntity<ProblemDescription> handle(CompetitionNotFoundException e, HttpServletRequest request) {
        return resourceNotFoundResponse(request, "Competition not found");
    }

    @ExceptionHandler({WorkNotFoundException.class})
    public ResponseEntity<ProblemDescription> handle(WorkNotFoundException e, HttpServletRequest request) {
        return resourceNotFoundResponse(request, "Work not found");
    }

    @ExceptionHandler({WorkFileReferenceNotFoundException.class})
    public ResponseEntity<ProblemDescription> handle(WorkFileReferenceNotFoundException e, HttpServletRequest request) {
        return resourceNotFoundResponse(request, "Work file not found");
    }

    @ExceptionHandler({IllegalCompetitionStatusException.class})
    public ResponseEntity<ProblemDescription> handle(IllegalCompetitionStatusException e, HttpServletRequest request) {
        log.warn(e.getMessage(), e);
        ProblemDescription problem = new ProblemDescription()
                .withType("https://api.myapp.com/problems/illegal-competition-status")
                .withHttpMethod(request.getMethod())
                .withInstance(request.getRequestURI())
                .withTitle("Illegal competition status")
                .withMessage("Request could not be handled because competition is in '" + CompetitionStatusEncoder.encodeStatus(e.getActualStatus()) + "' status");
        return toResponseEntity(problem, HttpStatus.LOCKED);
    }

    @ExceptionHandler({WorkDoesntMeetCompetitionParticipantRequirementsException.class})
    public ResponseEntity<ProblemDescription> handle(WorkDoesntMeetCompetitionParticipantRequirementsException e, HttpServletRequest request) {
        ProblemDescription problem = new ProblemDescription()
                .withType("https://api.myapp.com/problems/work-doesnt-meet-competition-requirements")
                .withHttpMethod(request.getMethod())
                .withInstance(request.getRequestURI())
                .withTitle("Work doesn't meet competition requirements");
        return toResponseEntity(problem, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
