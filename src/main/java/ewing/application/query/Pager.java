package ewing.application.query;

/**
 * 分页参数。
 *
 * @author Ewing
 **/
public class Pager {
    private int offset = 0;
    private int limit = 100;
    private boolean count = true;

    public Pager() {
    }

    public Pager(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
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
