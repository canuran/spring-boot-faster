package ewing.application;

/**
 * 业务异常对象，方便统一处理，支持链式调用。
 * AppException可自动转换成Result格式返回。
 */
public class AppException extends RuntimeException {

    // 异常类型、结果编码
    private int code = 0;

    // 异常可带出结果数据
    private Object data;

    public AppException() {
        super();
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public int getCode() {
        return code;
    }

    public AppException setCode(int code) {
        this.code = code;
        return this;
    }

    public Object getData() {
        return data;
    }

    public AppException setData(Object data) {
        this.data = data;
        return this;
    }

}
