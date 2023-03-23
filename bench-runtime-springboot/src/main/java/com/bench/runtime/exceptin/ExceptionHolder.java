package com.bench.runtime.exceptin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
@Slf4j
public class ExceptionHolder {
    /**
     * 与网关约定好的状态码，表示业务出错
     */
    //private static final int BIZ_ERROR_CODE = 4000;

    /**
     * 与网关约定好的系统错误状态码
     */
    private static final int SYSTEM_ERROR_CODE = 5050;

    /**
     * header中的错误code
     */
    private static final String X_SERVICE_ERROR_HEADER_NAME = "x-service-error-code";

    /**
     * header中的错误信息
     */
    private static final String X_SERVICE_ERROR_MESSAGE = "x-service-error-message";

    /**
     * header中的返回信息
     */
    private static final String X_SERVICE_ERROR_RESPONSE = "x-service-error-response";

    /**
     * 处理微服务异常信息，做到不与原系统的错误处理相冲突
     *
     * @param request   request
     * @param response  response
     * @param exception exception
     */
    public static void hold(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        //log.error("handleException[{} -> {}]", request.getRequestURI(), exception.getMessage(), exception);

        // 需要设置两个值，这样网关会收到错误信息
        // 并且会统计到监控当中
        response.setHeader(X_SERVICE_ERROR_HEADER_NAME, String.valueOf(SYSTEM_ERROR_CODE));
        String responseBody = exception.getMessage();
        response.setHeader(X_SERVICE_ERROR_RESPONSE, UriUtils.encode(responseBody, StandardCharsets.UTF_8));

        // 如果是未知错误，还需要收集异常信息
        StringBuilder msg = new StringBuilder();
        msg.append(exception.getMessage());
        StackTraceElement[] stackTrace = exception.getStackTrace();
        // 取5行错误内容
        int lineCount = 5;
        for (int i = 0; i < stackTrace.length && i < lineCount; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            msg.append("\n at ").append(stackTraceElement.toString());
        }
        response.setHeader(X_SERVICE_ERROR_MESSAGE, UriUtils.encode(msg.toString(), StandardCharsets.UTF_8));
    }

}
