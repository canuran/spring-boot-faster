package canuran.common.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常工具类。
 *
 * @author canuran
 */
public class ExceptionUtils {
    private ExceptionUtils() {
        throw new IllegalStateException("Can not construct ExceptionUtils");
    }

    /**
     * 获取异常根栈信息及业务链调用栈信息。
     */
    public static List<StackTraceElement> getBusinessTraces(Throwable throwable, String businessPackage) {
        StackTraceElement[] traces = throwable.getStackTrace();
        List<StackTraceElement> businessTraces = new ArrayList<>();
        if (traces != null && traces.length > 0) {
            for (StackTraceElement trace : traces) {
                if (businessTraces.isEmpty()) {
                    // 添加来根栈信息
                    businessTraces.add(trace);
                } else if (trace != null && trace.getClassName().startsWith(businessPackage)) {
                    // 添加业务链调用栈
                    businessTraces.add(trace);
                }
            }
        }
        return businessTraces;
    }

    /**
     * 获取最开始的原始异常。
     */
    public static Throwable getOriginCause(Throwable throwable) {
        Throwable cause = throwable;
        while (throwable != null) {
            cause = throwable;
            throwable = cause.getCause();
        }
        return cause;
    }
}