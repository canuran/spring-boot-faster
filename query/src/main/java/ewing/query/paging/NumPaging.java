package ewing.query.paging;

import java.io.Serializable;

/**
 * 分页参数。
 *
 * @author Ewing
 **/
public class NumPaging implements Serializable, Paging {
    private static final long serialVersionUID = 1L;

    public static final NumPaging COUNT_ONLY = new NumPaging(1, 0, true, false);
    public static final NumPaging ROWS_ONLY = new NumPaging(1, 0, false, true);

    private long page = 1;
    private long size = 100;
    private boolean countRows = true;
    private boolean fetchRows = true;

    public NumPaging() {
    }

    public NumPaging(long page, long size) {
        this.page = page;
        this.size = size;
    }

    public NumPaging(long page, long size, boolean countRows, boolean fetchRows) {
        this.page = page;
        this.size = size;
        this.countRows = countRows;
        this.fetchRows = fetchRows;
    }

    @Override
    public long getOffset() {
        return page * size - size;
    }

    @Override
    public long getLimit() {
        return size;
    }

    @Override
    public boolean isCountRows() {
        return countRows;
    }

    public void setCountRows(boolean countRows) {
        this.countRows = countRows;
    }

    @Override
    public boolean isFetchRows() {
        return fetchRows;
    }

    public void setFetchRows(boolean fetchRows) {
        this.fetchRows = fetchRows;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "NumPager{" +
                "page=" + page +
                ", size=" + size +
                ", countRows=" + countRows +
                ", fetchRows=" + fetchRows +
                '}';
    }
}
