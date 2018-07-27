package ewing.query;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 分页数据。
 *
 * @author Ewing
 **/
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows == null ? Collections.emptyList() : rows;
    }

    public Page<T> forEach(Consumer<T> consumer) {
        this.rows.forEach(consumer);
        return this;
    }
}
