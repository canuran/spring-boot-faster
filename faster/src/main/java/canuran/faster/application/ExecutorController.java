package canuran.faster.application;

import canuran.common.utils.GsonUtils;
import groovy.lang.GroovyClassLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.codehaus.groovy.control.CompilationFailedException;
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

/**
 * 后门相关接口，仅当executor.enable为true时开启。
 *
 * @author canuran
 * @since 2020年03月05日
 */
@Controller
@RequestMapping("/executor")
@ConditionalOnProperty(name = "executor.enable", havingValue = "true")
@Api(value = "Groovy调试接口")
public class ExecutorController {

    private static final String CODE_TEMPLATE = "import " + Autowired.class.getName() + ";\n" +
            "import " + ApplicationContext.class.getName() + ";\n" +
            "\n" +
            "class Runner {\n" +
            "    @Autowired\n" +
            "    ApplicationContext context;\n" +
            "\n" +
            "    Object run() {\n" +
            "        return context.getBean(" + ExecutorController.class.getName() + ".class).toString();\n" +
            "    }\n" +
            "}";

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping
    @ApiOperation(value = "在线调试")
    public ResponseEntity home(@RequestParam(value = "expression", required = false)
                                       String expression) throws Exception {
        String page = "<!DOCTYPE html>\n" +
                "<html lang='en'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <title>在线调试</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<form method='post' action=''>\n" +
                "    <label>在线调试</label>\n" +
                "    <br/>\n" +
                "    <textarea rows='20' cols='150' name='expression' placeholder='" +
                "编写可用Groovy编译的Java代码，无须定义包名，支持Spring注入，默认调第一个方法'>";

        // 写入参数
        page += StringUtils.hasText(expression) ? expression.trim() : CODE_TEMPLATE;

        page += "</textarea>\n" +
                "    <br/>\n" +
                "    <input type='submit'  value='编译并调用第一个方法' style='width:1073px'/>\n" +
                "</form>\n" +
                "<br/>\n" +
                "<textarea rows='20' cols='150' placeholder='调用返回的结果'>";

        // 写入返回值
        try {
            if (StringUtils.hasText(expression)) {
                page += methodExecute(expression);
            }
        } catch (Throwable throwable) {
            Throwable cause = throwable;
            while (throwable != null) {
                cause = throwable;
                throwable = cause.getCause();
            }
            page += "代码调用异常：" + cause.getMessage();
        }

        page += "</textarea>\n" +
                "</body>\n" +
                "</html>";

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/html;charset=UTF-8"))
                .body(page);
    }

    /***
     * 可执行可用Groovy编译的Java代码。
     *
     * @param expression 编写可用Groovy编译的Java代码，无须定义包名，支持Spring注入，默认调第一个方法。
     */
    private String methodExecute(String expression) throws ReflectiveOperationException {
        Class clazz;
        try {
            clazz = new GroovyClassLoader().parseClass(expression);
        } catch (CompilationFailedException e) {
            return "编译代码生成类失败：" + e.getMessage();
        }

        Method method;
        try {
            method = clazz.getDeclaredMethods()[0];
            method.setAccessible(true);
        } catch (Exception e) {
            return "获取第一个方法失败：" + e.getMessage();
        }

        Object object;
        try {
            object = clazz.newInstance();
        } catch (Exception e) {
            return "创建类的对象失败：" + e.getMessage();
        }

        try {
            applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
        } catch (Exception e) {
            return "SpringAutowire失败：" + e.getMessage();
        }

        Object result = method.invoke(object);

        try {
            if (Void.TYPE.equals(method.getReturnType())) {
                return "调用成功，该方法无返回值";
            } else {
                return result instanceof String ? (String) result : GsonUtils.toJson(result);
            }
        } catch (Exception e) {
            return String.valueOf(result);
        }
    }
}
