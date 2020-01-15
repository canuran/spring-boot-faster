package ewing.query.paging;

import java.io.Serializable;

/**
 * 分页参数。
 *
 * @author Ewing
 **/
public class OffsetPager implements Serializable, Paging {
    private static final long serialVersionUID = 1L;

    public static final OffsetPager COUNT_ONLY = new OffsetPager(0, 0, true);
    public static final OffsetPager ROWS_ONLY = new OffsetPager(0, Integer.MAX_VALUE, false);

    private long offset = 0;
    private long limit = 100;
    private boolean count = true;

    public OffsetPager() {
    }

    public OffsetPager(long offset, long limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public OffsetPager(long offset, long limit, boolean count) {
        this.offset = offset;
        this.limit = limit;
        this.count = count;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "OffsetPager{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", count=" + count +
                '}';
    }
}
