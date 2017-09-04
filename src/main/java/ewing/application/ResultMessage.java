package ewing.application;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 统一返回值的格式，方便封装处理，支持链式调用。
 * AppException可自动转换成Result格式返回。
 */
@ApiModel("结果对象")
public class ResultMessage<E> {

    @ApiModelProperty("结果编码：默认1为成功 0为失败")
    private int code = 1;

    @ApiModelProperty("是否成功")
    private boolean success = true;

    @ApiModelProperty("调用方可读的消息")
    private String message = "成功！";

    @ApiModelProperty("结果数据或异常调试信息")
    private E data;

    /**
     * 默认构造为成功，因为失败还可以用异常。
     */
    public ResultMessage() {
    }

    /**
     * 默认构造为成功并设置数据。
     */
    public ResultMessage(E data) {
        this.data = data;
    }

    public ResultMessage<E> toSuccess() {
        this.code = 1;
        this.success = true;
        this.message = "成功！";
        return this;
    }

    public ResultMessage<E> toFailure() {
        this.code = 0;
        this.success = false;
        this.message = "失败！";
        return this;
    }

    public ResultMessage<E> toSuccess(String message) {
        this.code = 1;
        this.success = true;
        this.message = message;
        return this;
    }

    public ResultMessage<E> toFailure(String message) {
        this.code = 0;
        this.success = false;
        this.message = message;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ResultMessage<E> setCode(int code) {
        this.code = code;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public ResultMessage<E> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResultMessage<E> setMessage(String message) {
        this.message = message;
        return this;
    }

    public E getData() {
        return data;
    }

    public ResultMessage<E> setData(E data) {
        this.data = data;
        return this;
    }

}
