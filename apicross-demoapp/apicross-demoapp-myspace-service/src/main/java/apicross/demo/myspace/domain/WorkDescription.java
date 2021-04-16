package apicross.demo.myspace.domain;

import lombok.Getter;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
public class WorkDescription {
    @Column(name = "title", length = 100)
    private String title;
    @Column(name = "description", length = 2000)
    private String description;
    @Column(name = "author", length = 150)
    private String author;
    @Column(name = "author_age")
    private int authorAge;

    public WorkDescription(@NonNull String title, String description, @NonNull String author, int authorAge) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.authorAge = authorAge;
    }

    protected WorkDescription() {
        // infrastructure requirement
    }
}
