package ewing.common.queryutils;

import java.util.List;

/**
 * 分页数据。
 *
 * @author Ewing
 * @since 2017-04-22
 **/
public class PageData<T> {
    private long total;

    private List<T> content;

    public PageData() {
    }

    public PageData(List<T> content) {
        if (content == null) return;
        this.content = content;
        this.total = content.size();
    }

    public PageData(long total, List<T> content) {
        this.total = total;
        this.content = content;
    }

    public long getTotal() {
        return total;
    }

    public PageData<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public List<T> getContent() {
        return content;
    }

    public PageData<T> setContent(List<T> content) {
        this.content = content;
        return this;
    }
}
