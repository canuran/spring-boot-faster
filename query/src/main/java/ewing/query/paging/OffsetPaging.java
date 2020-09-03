package ewing.query.paging;

import java.io.Serializable;

/**
 * 分页参数。
 *
 * @author Ewing
 **/
public class OffsetPaging implements Serializable, Paging {
    private static final long serialVersionUID = 1L;

    public static final OffsetPaging COUNT_ONLY = new OffsetPaging(0, 0, true, false);
    public static final OffsetPaging ROWS_ONLY = new OffsetPaging(0, 0, false, true);

    private long offset = 0;
    private long limit = 100;
    private boolean countRows = true;
    private boolean fetchRows = true;

    public OffsetPaging() {
    }

    public OffsetPaging(long offset, long limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public OffsetPaging(long offset, long limit, boolean countRows, boolean fetchRows) {
        this.offset = offset;
        this.limit = limit;
        this.countRows = countRows;
        this.fetchRows = fetchRows;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    @Override
    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
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

    @Override
    public String toString() {
        return "OffsetPager{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", countRows=" + countRows +
                ", fetchRows=" + fetchRows +
                '}';
    }
}
