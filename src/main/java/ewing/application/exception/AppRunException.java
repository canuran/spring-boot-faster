package ewing.application.exception;

import ewing.application.ResultMessage;

/**
 * 业务运行时异常对象，方便统一处理，支持链式调用。
 *
 * @author Ewing
 */
public class AppRunException extends RuntimeException implements ResultException {

    // 异常类型、结果编码
    private int code = 0;

    // 异常可带出结果数据
    private Object data;

    public AppRunException() {
        super();
    }

    public AppRunException(String message) {
        super(message);
    }

    public AppRunException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppRunException(Throwable cause) {
        super(cause);
    }

    public AppRunException(ResultMessage message) {
        this(message.getMessage());
        this.code = message.getCode();
        this.data = message.getData();
    }

    public AppRunException(ResultMessage message, Throwable cause) {
        this(message.getMessage(), cause);
        this.code = message.getCode();
        this.data = message.getData();
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public AppRunException setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public AppRunException setData(Object data) {
        this.data = data;
        return this;
    }

}
