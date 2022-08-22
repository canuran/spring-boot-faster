package canuran.common.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ExcelSheet列信息。
 *
 * @author caiyouyuan
 * @since 2019年06月15日
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    String value() default "";

    int columnWidth() default 0;

}
