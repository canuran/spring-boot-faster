package ewing.common.queryutils;

/**
 * 分页参数。
 *
 * @author Ewing
 * @since 2017-04-22
 **/
public class PageParam {
    private int offset = 0;
    private int limit = 100;
    private boolean count = true;

    public PageParam() {
    }

    public PageParam(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public PageParam(int offset, int limit, boolean count) {
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
