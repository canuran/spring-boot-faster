package ewing.application.query;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 分页数据。
 *
 * @author Ewing
 **/
public class Page<T> {
    private long total;

    private List<T> rows;

    public Page() {
        this.rows = Collections.emptyList();
    }

    public Page(List<T> rows) {
        this.rows = rows == null ? Collections.emptyList() : rows;
        this.total = this.rows.size();
    }

    public Page(long total, List<T> rows) {
        this.total = total;
        this.rows = rows == null ? Collections.emptyList() : rows;
    }

    public long getTotal() {
        return total;
    }

    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public List<T> getRows() {
        return rows;
    }

    public Page<T> setRows(List<T> rows) {
        this.rows = rows == null ? Collections.emptyList() : rows;
        return this;
    }

    public Page<T> forEach(Consumer<T> consumer) {
        this.rows.forEach(consumer);
        return this;
    }
}
