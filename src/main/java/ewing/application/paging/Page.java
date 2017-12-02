package ewing.application.paging;

import java.util.List;

/**
 * 分页数据。
 *
 * @author Ewing
 **/
public class Page<T> {
    private long total;

    private List<T> content;

    public Page() {
    }

    public Page(List<T> content) {
        if (content == null) {
            return;
        }
        this.content = content;
        this.total = content.size();
    }

    public Page(long total, List<T> content) {
        this.total = total;
        this.content = content;
    }

    public long getTotal() {
        return total;
    }

    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public List<T> getContent() {
        return content;
    }

    public Page<T> setContent(List<T> content) {
        this.content = content;
        return this;
    }
}
