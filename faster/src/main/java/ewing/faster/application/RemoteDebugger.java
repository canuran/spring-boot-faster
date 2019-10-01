package ewing.faster.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ewing.common.exception.Checks;
import ewing.common.utils.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@Controller
@Api(tags = "debugger", description = "调试接口")
public class RemoteDebugger {

    @Autowired
    private ApplicationContext applicationContext;

    private static final Pattern BEAN_METHOD = Pattern.compile("([a-zA-Z0-9_$.]+)[.#]([a-zA-Z0-9_$]+)\\((.*)\\)", Pattern.DOTALL);

    @RequestMapping("/debugger")
    @ApiOperation(value = "后门界面")
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
                "或直接复制方法引用：com.ewing.UserServiceImpl#findUser({id:123})\n" +
                "静态方法或new一个新对象调用：ewing.common.TimeUtils.getDaysOfMonth(2018,5)\n" +
                "注意：如果方法重载的，参数Json也是兼容的，将无法确定调用哪个方法。\">";

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

    private String methodExecute(@RequestBody String expression) {
        Checks.hasText(expression, "表达式不能为空！");
        Matcher matcher = BEAN_METHOD.matcher(expression);
        Checks.isTrue(matcher.find(), "表达式格式不正确！");

        // 根据名称获取Bean
        String classOrBeanName = matcher.group(1);
        Object springBean = getSpringBean(classOrBeanName);
        Class clazz;
        try {
            clazz = springBean == null ? Class.forName(classOrBeanName) : AopProxyUtils.ultimateTargetClass(springBean);
        } catch (Exception e) {
            throw new RuntimeException("初始化类失败！", e);
        }
        Checks.notNull(clazz, "调用Class不能为空！");

        // 根据名称获取方法列表
        List<Method> mayMethods = getMethods(clazz, matcher.group(2));

        // 转换方法参数
        JsonArray params = getJsonArray("[" + matcher.group(3) + "]");
        return GsonUtils.toJson(executeFoundMethod(clazz, springBean, mayMethods, params));
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
            Checks.isNull(foundMethod, "方法调用重复：" + foundMethod + " 和 " + method);
            foundMethod = method;
        }

        // 调用方法并返回
        Checks.notNull(foundMethod, "未找到满足参数的方法！");
        try {
            foundMethod.setAccessible(true);
            if (Modifier.isStatic(foundMethod.getModifiers())) {
                return foundMethod.invoke(clazz, args.toArray());
            } else {
                if (bean != null) {
                    Class<?> methodClass = foundMethod.getDeclaringClass();
                    if (!methodClass.equals(bean.getClass())) {
                        foundMethod = bean.getClass().getDeclaredMethod(foundMethod.getName(), foundMethod.getParameterTypes());
                    }
                    return foundMethod.invoke(bean, args.toArray());
                } else {
                    return foundMethod.invoke(clazz.newInstance(), args.toArray());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("调用方法失败！", e);
        }
    }

    private JsonArray getJsonArray(String jsonParams) {
        try {
            return GsonUtils.toObject(jsonParams, JsonArray.class);
        } catch (Exception e) {
            throw new RuntimeException("参数格式不正确！");
        }
    }

    private List<Method> getMethods(Class clazz, String methodName) {
        List<Method> mayMethods = Stream.of(clazz.getDeclaredMethods())
                .filter(m -> methodName.equals(m.getName()))
                .collect(Collectors.toList());
        Checks.notEmpty(mayMethods, "未找到方法：" + methodName);
        return mayMethods;
    }

    private Object getSpringBean(String beanName) {
        try {
            return applicationContext.getBean(beanName);
        } catch (Exception e) {
            try {
                return applicationContext.getBean(Class.forName(beanName));
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
