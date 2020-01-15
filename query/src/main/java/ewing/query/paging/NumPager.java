package ewing.query.paging;

import java.io.Serializable;

/**
 * 分页参数。
 *
 * @author Ewing
 **/
public class NumPager implements Serializable, Paging {
    private static final long serialVersionUID = 1L;

    public static final NumPager COUNT_ONLY = new NumPager(1, 0, true);
    public static final NumPager ROWS_ONLY = new NumPager(1, Integer.MAX_VALUE, false);

    private long page = 1;
    private long size = 100;
    private boolean count = true;

    public NumPager() {
    }

    public NumPager(long page, long size) {
        this.page = page;
        this.size = size;
    }

    public NumPager(long page, long size, boolean count) {
        this.page = page;
        this.size = size;
        this.count = count;
    }

    public long getOffset() {
        return page * size - size;
    }

    public long getLimit() {
        return size;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
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
                ", count=" + count +
                '}';
    }
}
