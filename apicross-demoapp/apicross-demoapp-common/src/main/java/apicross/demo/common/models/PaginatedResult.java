package apicross.demo.common.models;

import java.util.List;

public class PaginatedResult<T> {
    private final Page page;
    private final List<T> content;

    public PaginatedResult(Page page, List<T> content) {
        this.page = page;
        this.content = content;
    }

    public Page getPage() {
        return page;
    }

    public List<T> getContent() {
        return content;
    }
}
