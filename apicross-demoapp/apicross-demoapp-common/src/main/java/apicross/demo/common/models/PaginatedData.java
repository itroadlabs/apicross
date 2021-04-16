package apicross.demo.common.models;

import java.util.List;

public interface PaginatedData<T> {
    Page getPage();

    List<T> getPageContent();
}
