package com.bench.runtime.exceptin;

import com.bench.common.exception.BizException;
import com.bench.common.exception.CommonErrorEnum;
import com.bench.common.model.JsonResult;
import com.bench.runtime.rpc.RPCResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author by cold
 * @date 2020/12/16
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    public ControllerExceptionHandler() {
    }

    @ExceptionHandler({BizException.class})
    @ResponseBody
    public JsonResult<Object> handleServiceException(BizException e, HttpServletRequest request) {
        log.error("BindingException[{} => {}]", request.getRequestURI(), e.getErrorMessage(), e);
        return JsonResult.error(e.getErrorMessage(), e.getErrorCode());
    }

    @ExceptionHandler({ServletRequestBindingException.class})
    @ResponseBody
    public JsonResult<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                   HttpServletRequest request) {
        log.error("BindingException[{} => {}]", request.getRequestURI(), ex.getMessage(), ex);
        return JsonResult.error(CommonErrorEnum.BIND_ERROR.message(), CommonErrorEnum.BINDING_ERROR.name());
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public JsonResult<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                                           HttpServletRequest request) {
        log.warn("HttpRequestMethodNotSupportedException[{} => {}]", request.getRequestURI(), e.getMessage(), e);
        return JsonResult.error(CommonErrorEnum.METHOD_NOT_SUPPORTED.message(),
            CommonErrorEnum.METHOD_NOT_SUPPORTED.name());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                   HttpServletRequest request, HttpServletResponse response) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage()).append(";");
        }

        String msg = sb.toString();
        log.warn("handleMethodArgumentNotValidException:{} => {}", request.getRequestURI(), msg, e);

        ExceptionHolder.hold(request, response, e);

        // 返回内部 RPC 接口格式的 http code 和 http body
        if (request.getRequestURI().startsWith("/internal")) {
            return RPCResult.badRequest(msg);
        }
        return ResponseEntity.ok(JsonResult.error(msg, CommonErrorEnum.ARGUMENT_NOT_VALID.name()));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseBody
    public JsonResult<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
                                                                        HttpServletRequest request) {
        log.error("handleMethodArgumentTypeMismatchException:{} => {}", request.getRequestURI(), e.getMessage(), e);
        return JsonResult.error(MessageFormat
                .format(CommonErrorEnum.ARGUMENT_TYPE_MISMATCH.message(), e.getMessage()),
            CommonErrorEnum.ARGUMENT_TYPE_MISMATCH.name());
    }

    @ExceptionHandler({BindException.class})
    @ResponseBody
    public JsonResult<Object> handleBindException(BindException e, HttpServletRequest request) {
        log.error("handleBindException[{} -> {}]", request.getRequestURI(), e.getMessage(), e);
        return JsonResult.error(CommonErrorEnum.ARGUMENT_TYPE_MISMATCH.message(),
            CommonErrorEnum.BIND_ERROR.name());
    }

    @ExceptionHandler({IllegalStateException.class})
    @ResponseBody
    public JsonResult<Object> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        log.error("handleIllegalStateException[{} -> {}]", request.getRequestURI(), e.getMessage(), e);
        return JsonResult.error(e.getMessage(), CommonErrorEnum.ILLEGAL_STATE.name());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public JsonResult<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("handleIllegalArgumentException[{} -> {}]", request.getRequestURI(), e.getMessage(), e);
        return JsonResult.error(e.getMessage(), CommonErrorEnum.ILLEGAL_ARGUMENT.name());
    }

    @ExceptionHandler({DuplicateKeyException.class})
    @ResponseBody
    public JsonResult<Object> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.error("handleDuplicateKeyException[{} -> {}]", request.getRequestURI(), e.getMessage(), e);
        return JsonResult.error(CommonErrorEnum.DUPLICATE_KEY.message(), CommonErrorEnum.DUPLICATE_KEY.name());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
                                                                    HttpServletRequest request, HttpServletResponse response) {
        log.warn("handleHttpMessageNotReadableException[{} -> {}]",
            request.getRequestURI(), e.getMessage(), e);

        ExceptionHolder.hold(request, response, e);

        // 返回内部 RPC 接口格式的 http code 和 http body
        if (request.getRequestURI().startsWith("/internal")) {
            return RPCResult.badRequest("invalid body format");
        }

        return ResponseEntity
                .ok()
                .body(JsonResult.error(CommonErrorEnum.BODY_IS_MISS.message(), CommonErrorEnum.BODY_IS_MISS.name()));
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Object handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.error("handleException[{} -> {}]", request.getRequestURI(), e.getMessage(), e);

        ExceptionHolder.hold(request, response, e);

        // 返回内部 RPC 接口格式的 http code 和 http body
        if (request.getRequestURI().startsWith("/internal")) {
            return RPCResult.error();
        }

        return JsonResult.error(CommonErrorEnum.SYSTEM_ERROR.message(), CommonErrorEnum.SYSTEM_ERROR.name());
    }
}
