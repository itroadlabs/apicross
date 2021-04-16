package apicross.demo.common.models;

public class Page {
    private Integer totalPages;
    private Integer number;
    private Integer pageSize;
    private Integer total;

    public Page(Integer totalPages, Integer number, Integer pageSize, Integer total) {
        this.totalPages = totalPages;
        this.number = number;
        this.pageSize = pageSize;
        this.total = total;
    }

    public Page() {
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Page setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Page setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Page setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getTotal() {
        return total;
    }

    public Page setTotal(Integer total) {
        this.total = total;
        return this;
    }

    @Override
    public String toString() {
        return "Page{" +
                "totalPages=" + totalPages +
                ", number=" + number +
                ", pageSize=" + pageSize +
                ", total=" + total +
                '}';
    }
}
