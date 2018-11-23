package ewing.common.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常工具类。
 *
 * @author Ewing
 */
public class ExceptionUtils {

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

}