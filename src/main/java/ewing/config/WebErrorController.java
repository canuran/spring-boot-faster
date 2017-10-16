package ewing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Web应用错误控制器，优先级低于全局异常处理。
 */
@Controller
@EnableConfigurationProperties(ServerProperties.class)
@RequestMapping({"${server.error.path:${error.path:error}}"})
public class WebErrorController implements ErrorController {

    private ErrorAttributes errorAttributes;

    private ServerProperties serverProperties;

    /**
     * 初始化WebErrorController
     */
    @Autowired
    public WebErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        if (errorAttributes == null || serverProperties == null) {
            throw new IllegalArgumentException("错误页面初始化失败：参数为空！");
        }
        this.errorAttributes = errorAttributes;
        this.serverProperties = serverProperties;
    }

    /**
     * 定义错误页面信息
     */
    @RequestMapping(produces = "text/html")
    public void errorPage(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getHttpStatus(request);
        response.setStatus(status.value());
        Map<String, Object> model = getErrorAttributes(request, hasStackTrace(request));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            return;
        }
        writer.println("<html>\n<head>");
        writer.println("<meta charset='UTF-8'>");
        writer.println("<title>网页访问出错</title>\n</head>");
        writer.println("<body style='text-align:center;background-color:#eee'>");
        writer.println("<div style='width:420px;margin:5% auto'>");
        writer.println("<h2>您访问的网页出错了</h2>");
        writer.println("<button onclick='history.go(-1)' style='margin-right:25px'>返回前页</button>");
        writer.println("<button onclick='location.href=\"/\"'>回到首页</button>\n<h3></h3>");
        writer.println("<div style='text-align:left;padding:20px;background-color:#e5e5e5;border-radius:10px'>");
        writer.println("<pre>状态: " + status + "</pre>");
        writer.println("<pre>错误: " + model.get("error") + "</pre>");
        writer.println("<pre>异常: " + model.get("exception") + "</pre>");
        writer.println("<pre>信息: " + model.get("message") + "</pre>\n</div>");
        writer.print("</div>\n</body>\n</html>");
        writer.close();
    }

    /**
     * 定义错误数据信息
     */
    @ResponseBody
    @RequestMapping
    public ResponseEntity<Map<String, Object>> errorJson(HttpServletRequest request) {
        Map<String, Object> model = getErrorAttributes(request, hasStackTrace(request));
        HttpStatus status = getHttpStatus(request);
        Map<String, Object> body = new HashMap<>(4);
        body.put("code", status.value());
        body.put("success", false);
        body.put("message", model.get("message"));
        body.put("data", model.get("error"));
        return new ResponseEntity<>(body, status);
    }

    /**
     * 是否包括堆栈跟踪属性
     */
    private boolean hasStackTrace(HttpServletRequest request) {
        ErrorProperties.IncludeStacktrace include = this.serverProperties.getError().getIncludeStacktrace();
        return include == ErrorProperties.IncludeStacktrace.ALWAYS ||
                (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM && this.hasStackTrace(request));
    }

    /**
     * 获取错误的信息
     */
    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }

    /**
     * 获取错误编码
     */
    private HttpStatus getHttpStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * 实现错误路径（暂时无用）
     */
    @Override
    public String getErrorPath() {
        return "error";
    }

}