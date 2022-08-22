package canuran.common.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ExcelSheet信息。
 *
 * @author caiyouyuan
 * @since 2019年06月15日
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSheet {
    String value() default "";

    int columnWidth() default 20;

    boolean freezeFirstRow() default true;

    boolean freezeFirstColumn() default false;
}
