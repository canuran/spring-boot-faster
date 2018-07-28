package ewing.query.paging;

import java.io.Serializable;

/**
 * 分页参数。
 *
 * @author Ewing
 **/
public class Pager implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Pager COUNT_ONLY = new Pager(0, 0);

    private int offset;
    private int limit;
    private boolean count;

    public Pager() {
        this.offset = 0;
        this.limit = 100;
        this.count = true;
    }

    public Pager(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
        this.count = true;
    }

    public Pager(int offset, int limit, boolean count) {
        this.offset = offset;
        this.limit = limit;
        this.count = count;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }
}
