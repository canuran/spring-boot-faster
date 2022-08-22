package canuran.query.support;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

import java.util.LinkedHashMap;

/**
 * 单元测试专用Tuple。
 *
 * @author canuran
 * @since 2020年02月20日
 */
@SuppressWarnings("unchecked")
public class TestTuple extends LinkedHashMap<Expression<?>, Object> implements Tuple {

    public <T> TestTuple put(Expression<T> key, T value) {
        super.put(key, value);
        return this;
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        return (T) values().stream().skip(index)
                .findFirst().orElse(null);
    }

    @Override
    public <T> T get(Expression<T> expr) {
        return (T) super.get(expr);
    }

    @Override
    public Object[] toArray() {
        return values().toArray();
    }
}
