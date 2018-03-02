package ewing.application.query;

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
    }

    public Page(List<T> rows) {
        if (rows == null) {
            return;
        }
        this.rows = rows;
        this.total = rows.size();
    }

    public Page(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
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
        this.rows = rows;
        return this;
    }

    public Page<T> forEach(Consumer<T> consumer) {
        if (rows != null && rows.size() > 0) {
            this.rows.forEach(consumer);
        }
        return this;
    }
}
