package ewing.application;

import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 应用中业务方法调用的日志切面。
 */
@Aspect
@Component
public class AppMethodLogger {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AppMethodLogger.class.getSimpleName());

    private static final String START_PACKAGE = "ewing";

    @Around("execution(* " + START_PACKAGE + "..*.*(..))" +
            " && (@within(org.springframework.stereotype.Component)" +
            " || @within(org.springframework.stereotype.Controller)" +
            " || @within(org.springframework.stereotype.Service)" +
            " || @within(org.springframework.stereotype.Repository)" +
            " || @within(org.springframework.web.bind.annotation.RestController))")
    public Object logging(ProceedingJoinPoint point) throws Throwable {
        Object result;
        // 此处判断可以节省准备日志内容的时间
        Signature signature = point.getSignature();
        if (LOGGER.isInfoEnabled() && signature instanceof MethodSignature) {
            // 获取方法名、方法上的Swagger注释
            Method method = ((MethodSignature) signature).getMethod();
            String methodName = methodToString(method);
            ApiOperation operation = method.getAnnotation(ApiOperation.class);
            String annotation = operation == null ? "" : operation.value() + "：";
            // 记录执行日志 方法名、参数、返回值、异常信息等
            LOGGER.info("调用方法：" + annotation + methodName
                    + " 参数：" + argsToString(point.getArgs()));
            long time = System.currentTimeMillis();
            try {
                result = point.proceed();
            } catch (Throwable throwable) {
                LOGGER.info("执行方法：" + annotation + methodName
                        + " 经过时间：" + (System.currentTimeMillis() - time)
                        + "ms 发生异常：" + throwable.getMessage());
                throw throwable; // 原来的异常继续抛出去
            }
            String returnValue = method.getReturnType() == void.class
                    ? "无" : "ms 返回值：" + result;
            LOGGER.info("结束方法：" + annotation + methodName
                    + " 执行耗时：" + (System.currentTimeMillis() - time)
                    + "ms 返回值：" + returnValue);
        } else {
            result = point.proceed();
        }
        return result;
    }

    private String methodToString(Method method) {
        String name = method.getDeclaringClass()
                .getName().substring(START_PACKAGE.length() + 1);
        Class[] types = method.getParameterTypes();
        if (types.length == 0) return name + "()";
        StringBuilder builder = new StringBuilder(name).append('(');
        for (int i = 0; i < types.length; i++) {
            String typeName = types[i].getSimpleName();
            if (i > 0) {
                builder.append(',').append(typeName);
            } else {
                builder.append(typeName);
            }
        }
        return builder.append(')').toString();
    }

    private String argsToString(Object[] args) {
        if (args.length == 0) return "无";
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            if (builder.length() > 0) {
                builder.append(',').append(arg);
            } else {
                builder.append(arg);
            }
        }
        return builder.toString();
    }

}