package ewing.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ewing.application.common.GsonUtils;
import ewing.application.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 调试工具，可调用项目中的任意方法。
 * <p>
 * 注意：方法重载的参数只提供Json级别的识别。
 *
 * @author Ewing
 * @since 2018年6月1日
 */
@RestController("/debugger")
@Api(tags = "debugger", description = "调试接口")
public class RemoteDebugger {

    @Autowired
    private ApplicationContext applicationContext;

    private static final Pattern BEAN_METHOD = Pattern.compile("([a-zA-Z0-9_$.]+)\\.([a-zA-Z0-9_$]+)\\((.*)\\)", Pattern.DOTALL);

    @PostMapping("/methodExecute")
    @ApiOperation(value = "根据Bean名称或类全名调用方法", notes = "例如：userServiceImpl.findUserWithRole({limit:2})" +
            " 或：ewing.application.common.TimeUtils.getDaysOfMonth(2018,5) 注意：无法区分重载且参数的Json相互兼容的方法")
    public ResultMessage methodExecute(@RequestBody String expression) {
        AppAsserts.hasText(expression, "表达式不能为空！");
        Matcher matcher = BEAN_METHOD.matcher(expression);
        AppAsserts.yes(matcher.find(), "表达式格式不正确！");

        // 根据名称获取Bean
        String classOrBeanName = matcher.group(1);
        Object bean = getBean(classOrBeanName);
        Class clazz;
        try {
            clazz = bean == null ? Class.forName(classOrBeanName) : AopProxyUtils.ultimateTargetClass(bean);
        } catch (Exception e) {
            throw new BusinessException("初始化类失败！", e);
        }
        AppAsserts.notNull(clazz, "调用Class不能为空！");

        // 根据名称获取方法列表
        List<Method> mayMethods = getMethods(clazz, matcher.group(2));

        // 转换方法参数
        JsonArray params = getJsonArray("[" + matcher.group(3) + "]");
        return new ResultMessage<>(executeFoundMethod(clazz, bean, mayMethods, params));
    }

    private Object executeFoundMethod(Class clazz, Object bean, List<Method> mayMethods, JsonArray params) {
        // 根据参数锁定方法
        List<Object> args = new ArrayList<>();
        Method foundMethod = null;
        for (Method method : mayMethods) {
            if (!args.isEmpty()) {
                args.clear();
            }
            Type[] types = method.getGenericParameterTypes();
            if (types.length != params.size()) {
                continue;
            }
            // 参数转换，无异常表示匹配
            Iterator<JsonElement> paramIterator = params.iterator();
            try {
                for (Type type : types) {
                    Object arg = GsonUtils.getGson().fromJson(paramIterator.next(), type);
                    args.add(arg);
                }
            } catch (Exception e) {
                continue;
            }
            AppAsserts.isNull(foundMethod, "方法调用重复：" + foundMethod + " 和 " + method);
            foundMethod = method;
        }

        // 调用方法并返回
        AppAsserts.notNull(foundMethod, "未找到满足参数的方法！");
        try {
            foundMethod.setAccessible(true);
            if (Modifier.isStatic(foundMethod.getModifiers())) {
                return foundMethod.invoke(clazz, args.toArray());
            } else {
                return foundMethod.invoke(bean == null ? clazz.newInstance() : bean, args.toArray());
            }
        } catch (Exception e) {
            throw new BusinessException("调用方法失败！", e);
        }
    }

    private JsonArray getJsonArray(String jsonParams) {
        try {
            return GsonUtils.toObject(jsonParams, JsonArray.class);
        } catch (Exception e) {
            throw new BusinessException("参数格式不正确！");
        }
    }

    private List<Method> getMethods(Class clazz, String methodName) {
        List<Method> mayMethods = Stream.of(clazz.getDeclaredMethods())
                .filter(m -> methodName.equals(m.getName()))
                .collect(Collectors.toList());
        AppAsserts.notEmpty(mayMethods, "未找到方法：" + methodName);
        return mayMethods;
    }

    private Object getBean(String beanName) {
        try {
            return applicationContext.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
    }

}
