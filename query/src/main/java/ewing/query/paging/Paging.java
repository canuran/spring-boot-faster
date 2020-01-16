package ewing.query.paging;

/**
 * 请输入描述说明。
 *
 * @author caiyouyuan
 * @since 2020年01月16日
 */
public interface Paging {

    default boolean isCountRows() {
        return true;
    }

    default boolean isFetchRows() {
        return true;
    }

    long getLimit();

    long getOffset();

}
