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
            String methodName = method.getDeclaringClass()
                    .getName().substring(START_PACKAGE.length() + 1)
                    + "." + method.getName();
            ApiOperation operation = method.getAnnotation(ApiOperation.class);
            String methodDetail = operation == null ? methodName
                    : operation.value() + "：" + methodName;
            // 记录执行日志 方法名、参数、返回值、异常信息等
            LOGGER.info("调用方法：" + methodDetail + argsToString(point.getArgs()));
            long time = System.currentTimeMillis();
            try {
                result = point.proceed();
            } catch (Throwable throwable) {
                LOGGER.info("执行方法：" + methodDetail
                        + " 经过时间：" + (System.currentTimeMillis() - time)
                        + "ms 发生异常：" + throwable.getMessage());
                throw throwable; // 原来的异常继续抛出去
            }
            String returnValue = method.getReturnType() == void.class
                    ? "ms" : "ms 返回值：" + result;
            LOGGER.info("结束方法：" + methodDetail
                    + " 执行耗时：" + (System.currentTimeMillis() - time)
                    + returnValue);
        } else {
            result = point.proceed();
        }
        return result;
    }

    private String argsToString(Object[] args) {
        if (args.length == 0) {
            return "()";
        }
        StringBuilder builder = new StringBuilder().append('(');
        for (Object arg : args) {
            if (builder.length() > 1) {
                builder.append(',').append(arg);
            } else {
                builder.append(arg);
            }
        }
        return builder.append(')').toString();
    }

}