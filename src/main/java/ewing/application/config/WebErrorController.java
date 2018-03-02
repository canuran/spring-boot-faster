package ewing.application.config;

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
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Web应用错误控制器，优先级低于全局异常处理。
 *
 * @author Ewing
 */
@Controller
@ApiIgnore
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
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            return;
        }
        writer.println(ERROR_PAGE);
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

    private static final String ERROR_PAGE = "<html>\n" +
            "<head>\n" +
            "<meta charset='UTF-8'>\n" +
            "<title>网页出问题了</title>\n" +
            "</head>\n" +
            "<body style='margin:10%;text-align:center;background-color:#eee'>\n" +
            "<img src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFAAAABaCAMAAAAYVCH5AAAAGXRFWHRTb2Z0d2FyZQBB" +
            "ZG9iZSBJbWFnZVJlYWR5ccllPAAAA3FpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0i" +
            "VzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFk" +
            "b2JlIFhNUCBDb3JlIDUuNi1jMDE0IDc5LjE1Njc5NywgMjAxNC8wOC8yMC0wOTo1MzowMiAgICAgICAgIj4gPHJkZjpSREYgeG1s" +
            "bnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6" +
            "YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8v" +
            "bnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFw" +
            "LzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDozMTkzNTU0NS1iNjJiLTc5NDItOGQyZS01NmIwY2NmZDZk" +
            "YmUiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NzVFMEEwRjhEQUIzMTFFN0FFRTVCQTJCRjMwMURGNDciIHhtcE1NOkluc3Rh" +
            "bmNlSUQ9InhtcC5paWQ6NzVFMEEwRjdEQUIzMTFFN0FFRTVCQTJCRjMwMURGNDciIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhv" +
            "dG9zaG9wIENDIChXaW5kb3dzKSI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOmQ0ZWU3Y2Mx" +
            "LWMwNmEtZTE0YS04NGJlLTZkOGU1MmZmYzZjZiIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDoyYTc2MDJkNS0xNzcwLTlmNDkt" +
            "OGQzNy04NGFlYmUyYTc5Y2UiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBl" +
            "bmQ9InIiPz7uR1yyAAAAMFBMVEWpfFDt+Pj626o9KBump6XFoHWIfnP2igHvxpXN5fE0Y6qszuOMRiTqtHfBwsD///9sJMGjAAAA" +
            "EHRSTlP///////////////////8A4CNdGQAABH5JREFUeNqsmYlirCoMhgmGCALh/d/2JgGny532xGq6GLfPPwsUp2E8bEF+gKgR" +
            "i1PUKXIAxahVJkY5I76cZUJ2Aqm1Rg0GNDWCEbetbaEdrW1bklNtozF06wICtX2PIMBtN9sGy5EOfZNNhqDn2ygNog+Icv0e9ga0" +
            "L+Ooh/puD+g7y4kGLB66gCYAkEAppFRShXsjVRianJM9VGpxA0PjES0yxQYlSHhrYxmYcbuAJmcPJktzr8Dpro0d207fU2UjNgon" +
            "sKV5t4ZvaVOn7Y19wMJQLGcfIfM3hTOI5mxsaa9i6kyHVhbjAu5nmAYkN1Dj2feK1jPSbab4s0KL2dU0J3AGZE4MdEp75dBils43" +
            "K3DeCwBvc4g24MYag5r6IsNsbjZ+AadWwKCHgDFRCPS+ygM+PfRNFFxeWjmEgKIhLPsB+LvpnLEiNlB64RKVAn8BnjUOb40YrgGt" +
            "WIeA3/NCEma5BGQiVF4KPxteAWrYP+pbKuEiEAP9CsRxDfi7PDO+BMR/A0+RPiAlB5H8QEfEL40uILt4KYAXiMEv0QVMTwPJhbtQ" +
            "FKdAcAL5Sgo9QHLyQnICvSUJ3rZBN5CdOZyT6D/CTX5gkctj/53XekzuPpSR3A2YslgSC+trmRJz7v7GJiWpiJhzNMvqLV8stSQH" +
            "kpXZA8Qul5Peo8DMtjW1uqs7JApjIq9CJfQwcbTJgt4wMcqSfqOJDF3wxTv0UOPRsCPLywEFNEaOSEmWzSY2CTDzFWA3YGgzhZmn" +
            "RrFGBpSERPcEm05gtnpYQUStlmUl0RS6gWDlMKBKM1L+/HudjekSsPeZuAgW8tILkzcTkpxjmcFuYdZQZeklbWkZzEiUuhHtaZGL" +
            "FygWsdYqWJnLGk1KbjJLC51r5VT0Iu/kUGQVWFKuLMBV5ZzPGqUuwJoiyEUXgAU1Kub8P5PIVTnqRReB9Q3OrHYhXgVKozH3H4hS" +
            "FCz1ErBS7/11t7h9bmxf3VD9CvXZBZPeuUrBCuHIZ3V6thxWZ9uAvAwWTsrrMpwJPoxCNLE5SRhQvWsbhAIYZ4hdZizmWCAWZpnL" +
            "tCXlRLFr3It2DWeYxBi2rxb0QTkNi8ILLBW0tzX5Oqm2D9PJNhtPSwLutc2MR7qmi8L22WR+1fqowBmxD6gSFzG1r6bFZ33iEuhc" +
            "Y7MGbURBJptNbZoOmr9oCSx87T1Fk4gRw+rmPrtSOxrlaVUfdwk4ULu7VEz9qyU98YnnBo6KdmOVEf1hMnMVmzp4XAbqR3bCqzYJ" +
            "AG5Y4tqriDD+AJyfAgpBMHAcUljj4bcPAa8AV3EMdNQz2utv9N9avFjBj3p6TwGPZ4GWwweB5TiO8iAQFDiTeBfIk1JV4XT5rsJp" +
            "VQWadzfkOU8jbXS6jwCJ9NvsZshlO4mLt8E94JBF9fn3Sf+ibDRuAsskvQzuAmfQJ/LNR+WXgWApZGZ7SxlPANvsFXqXwT8qbAp8" +
            "XKFs21PApfCRkJnO8WavkVhuAtf4oFXtN2kMfxnK2s98ureAMBUe+HK3erconn/QPGr/CTAAGeY5eMygzYoAAAAASUVORK5CYII='/>\n" +
            "<h2><font color='#555'>网页出问题了</font></h2>\n" +
            "<button onclick='history.go(-1)' style='color:#555;border:none;border-radius:5px;" +
            "height:35px;margin-right:10px'>返回前页</button>\n" +
            "<button onclick='location.href=\"/\"' style='color:#555;border:none;height:35px;" +
            "border-radius:5px'>回到首页</button>\n" +
            "</body>\n" +
            "</html>";

}