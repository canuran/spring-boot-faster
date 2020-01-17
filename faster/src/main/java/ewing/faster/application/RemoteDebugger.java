package ewing.faster.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ewing.common.utils.Arguments;
import ewing.common.utils.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 调试工具，可调用项目中的任意方法。
 * <p>
 * 注意：方法重载的参数只提供Json级别的识别。
 *
 * @author Ewing
 * @since 2018年6月1日
 */
@Controller
@ConditionalOnProperty(name = "debugger.enable", havingValue = "true")
@Api(tags = "debugger", description = "调试接口")
public class RemoteDebugger {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDebugger.class);

    @Autowired
    private ApplicationContext applicationContext;

    private static final Pattern BEAN_METHOD = Pattern.compile("([a-zA-Z0-9_$.]+)[.#]([a-zA-Z0-9_$]+)\\((.*)\\)", Pattern.DOTALL);

    @RequestMapping("/debugger")
    @ApiOperation(value = "在线调试")
    public ResponseEntity home(@RequestParam(value = "expression", required = false)
                                       String expression) throws Exception {
        String page = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>在线调试</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<form method=\"post\" action=\"\">\n" +
                "    <label>在线调试</label>\n" +
                "    <br/>\n" +
                "    <textarea rows=\"10\" cols=\"100\" name=\"expression\" placeholder=\"" +
                "Spring Bean 方法调用：userServiceImpl.findUser({id:123})\n" +
                "用 IDEA 复制方法引用：com.ewing.UserServiceImpl#findUser({id:123})\n" +
                "静态方法或new一个新对象调用：ewing.common.TimeUtils.getDaysOfMonth(2018,5)\n" +
                "方法参数为去掉中括号的JSON数组\">";

        // 写入参数
        page += expression == null ? "" : expression.trim();

        page += "</textarea>\n" +
                "    <br/>\n" +
                "    <input type=\"submit\"  value=\"　提　交　\"/>\n" +
                "</form>\n" +
                "<br/>\n" +
                "<textarea rows=\"20\" cols=\"100\" id=\"result\" placeholder=\"调用返回的结果\">";

        // 写入返回值
        try {
            if (StringUtils.hasText(expression)) {
                page += methodExecute(expression);
            }
        } catch (Exception e) {
            page += e.getMessage();
        }

        page += "</textarea>\n" +
                "</body>\n" +
                "</html>";

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/html;charset=UTF-8"))
                .body(page);
    }

    private String methodExecute(String expression) {
        Arguments.of(expression).hasText("表达式不能为空");
        Matcher matcher = BEAN_METHOD.matcher(expression);
        Arguments.of(matcher.matches()).equalsTo(true, "表达式格式不正确");

        String classOrBeanName = matcher.group(1);
        String methodName = matcher.group(2);
        JsonArray arguments = getJsonArray("[" + matcher.group(3) + "]");

        // 根据名称获取Bean
        TargetInstance targetInstance = getTargetInstance(classOrBeanName);

        TargetInvoker targetInvoker = getTargetInvoker(targetInstance.advisedTargetClass, methodName, arguments);
        if (targetInvoker.method != null) {
            return GsonUtils.toJson(targetInvoker.invoke(targetInstance.advisedTarget));
        } else {
            targetInvoker = getTargetInvoker(targetInstance.targetClass, methodName, arguments);
            Arguments.of(targetInvoker.method).notNull("找不到满足参数的方法：" + methodName);
            return GsonUtils.toJson(targetInvoker.invoke(targetInstance.target));
        }
    }

    private static class TargetInstance {
        Object target;
        Object advisedTarget;
        Class<?> targetClass;
        Class<?> advisedTargetClass;
    }

    private TargetInvoker getTargetInvoker(Class<?> clazz, String methodName, JsonArray arguments) {
        TargetInvoker targetInvoker = new TargetInvoker();
        if (clazz == null) {
            return targetInvoker;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                Type[] types = method.getGenericParameterTypes();
                if (types.length == arguments.size()) {
                    // 参数转换无异常表示参数匹配
                    Iterator<JsonElement> elementIterator = arguments.iterator();
                    try {
                        List<Object> args = new ArrayList<>(types.length);
                        for (Type type : types) {
                            Object arg = GsonUtils.getGson().fromJson(elementIterator.next(), type);
                            args.add(arg);
                        }
                        targetInvoker.arguments = args;
                    } catch (Exception e) {
                        continue;
                    }
                    Arguments.of(targetInvoker.method).isNull("有多个可调用的方法：" + methodName);
                    targetInvoker.method = method;
                }
            }
        }
        return targetInvoker;
    }

    private static class TargetInvoker {
        Method method;
        List<Object> arguments;

        Object invoke(Object target) {
            Method methodPresent = Objects.requireNonNull(this.method, "方法不能为空");
            Object invokeTarget = target;
            try {
                if (Modifier.isStatic(methodPresent.getModifiers())) {
                    invokeTarget = methodPresent.getDeclaringClass();
                } else if (invokeTarget == null) {
                    invokeTarget = methodPresent.getDeclaringClass().newInstance();
                }
                Object targetPresent = Objects.requireNonNull(invokeTarget, "实例不能为空");

                methodPresent.setAccessible(true);
                return arguments == null || arguments.isEmpty() ?
                        methodPresent.invoke(targetPresent) :
                        methodPresent.invoke(targetPresent, arguments.toArray());
            } catch (ReflectiveOperationException e) {
                LOGGER.error("调用方法失败", e);
                throw new RuntimeException(e);
            }
        }
    }

    private TargetInstance getTargetInstance(String classOrBeanName) {
        TargetInstance targetInstance = new TargetInstance();
        try {
            targetInstance.advisedTarget = applicationContext.getBean(classOrBeanName);
        } catch (Exception e) {
            LOGGER.info("Get spring bean name {} error: {}", classOrBeanName, e.getMessage());
        }

        if (targetInstance.advisedTarget == null) {
            try {
                targetInstance.advisedTarget = applicationContext.getBean(Class.forName(classOrBeanName));
            } catch (Exception e) {
                LOGGER.info("Get spring bean class {} error: {}", classOrBeanName, e.getMessage());
            }
        }

        if (targetInstance.advisedTarget == null) {
            try {
                targetInstance.targetClass = Class.forName(classOrBeanName);
            } catch (Exception e) {
                throw new RuntimeException("找不到类：" + classOrBeanName);
            }
        } else {
            targetInstance.advisedTargetClass = targetInstance.advisedTarget.getClass();
            targetInstance.target = AopProxyUtils.getSingletonTarget(targetInstance.advisedTarget);
            targetInstance.targetClass = AopProxyUtils.ultimateTargetClass(targetInstance.advisedTarget);
        }
        return targetInstance;
    }

    private JsonArray getJsonArray(String jsonParams) {
        try {
            return GsonUtils.toObject(jsonParams, JsonArray.class);
        } catch (Exception e) {
            throw new RuntimeException("方法参数格式不正确");
        }
    }
}
