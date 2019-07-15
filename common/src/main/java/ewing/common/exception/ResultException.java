package ewing.common.exception;

/**
 * ResultException可自动转换成Result格式返回。
 *
 * @author Ewing
 */
public interface ResultException {

    Throwable getCause();

    String getMessage();

    default int getCode() {
        return 0;
    }

    void setCode(int code);

    default Object getData() {
        return null;
    }

    void setData(Object data);
}
