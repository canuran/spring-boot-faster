package ewing.application.exception;

/**
 * 异常工具类。
 *
 * @author Ewing
 */
public class ExceptionUtils {

    /**
     * 获取来源异常消息及堆栈信息。
     */
    public static ExceptionTrace getCauseTrace(Throwable throwable) {
        // 获取最开始的异常
        Throwable cause = getFirstCause(throwable);
        // 获取异常消息及堆栈信息
        ExceptionTrace exceptionTrace = new ExceptionTrace();
        exceptionTrace.setMessage(cause.getMessage());
        exceptionTrace.setException(cause.getClass().getName());
        StackTraceElement[] traces = cause.getStackTrace();
        if (traces != null && traces.length > 0) {
            StackTraceElement firstTrace = traces[0];
            exceptionTrace.setAtClass(firstTrace.getClassName());
            exceptionTrace.setAtMethod(firstTrace.getMethodName());
            exceptionTrace.setAtLine(firstTrace.getLineNumber());
        }
        return exceptionTrace;
    }

    /**
     * 获取第一次导致的异常信息。
     */
    public static Throwable getFirstCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

}