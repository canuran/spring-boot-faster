package canuran.common.exception;

import canuran.common.ResultMessage;

/**
 * 业务异常对象，方便统一处理，支持链式调用。
 *
 * @author canuran
 */
public class AttentionException extends Exception implements ResultException {

    // 异常类型、结果编码
    private int code = 0;

    // 异常可带出结果数据
    private Object data;

    public AttentionException() {
        super();
    }

    public AttentionException(String message) {
        super(message);
    }

    public AttentionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttentionException(Throwable cause) {
        super(cause);
    }

    public AttentionException(ResultMessage message) {
        this(message.getMessage());
        this.code = message.getCode();
        this.data = message.getData();
    }

    public AttentionException(ResultMessage message, Throwable cause) {
        this(message.getMessage(), cause);
        this.code = message.getCode();
        this.data = message.getData();
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

}
