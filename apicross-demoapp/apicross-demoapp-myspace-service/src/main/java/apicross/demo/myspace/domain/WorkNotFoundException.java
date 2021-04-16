package apicross.demo.myspace.domain;

import org.springframework.security.core.userdetails.User;

public class WorkNotFoundException extends RuntimeException {
    public WorkNotFoundException(User user, String workId) {

    }
}
