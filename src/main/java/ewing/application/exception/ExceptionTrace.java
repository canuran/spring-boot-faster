package ewing.application.exception;

/**
 * 异常消息及堆栈信息。
 */
public class ExceptionTrace {
    private String exception;
    private String message;
    private String atClass;
    private String atMethod;
    private int atLine;

    public ExceptionTrace() {
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAtClass() {
        return atClass;
    }

    public void setAtClass(String atClass) {
        this.atClass = atClass;
    }

    public String getAtMethod() {
        return atMethod;
    }

    public void setAtMethod(String atMethod) {
        this.atMethod = atMethod;
    }

    public int getAtLine() {
        return atLine;
    }

    public void setAtLine(int atLine) {
        this.atLine = atLine;
    }

    @Override
    public String toString() {
        return "ExceptionTrace{" +
                "exception='" + exception + '\'' +
                ", message='" + message + '\'' +
                ", atClass='" + atClass + '\'' +
                ", atMethod='" + atMethod + '\'' +
                ", atLine=" + atLine +
                '}';
    }
}