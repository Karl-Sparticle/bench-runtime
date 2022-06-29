package com.bench.runtime.rpc;

import com.bench.lang.base.json.jackson.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * RPC(rpc over http)通用返回模型
 * 业务上有以下要求：
 * 1.http 接口返回 http 状态码需要是 2xx,4xx,5xx
 * 2.http body 中有 status 对象，用于表示业务处理是否成功
 * 3.业务字段 key 为蛇形风格
 * </p>
 *
 * @author Karl
 * @date 2022/6/28 11:45
 */
@ApiModel(description = "RPC通用返回模型")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class RPCResult<T> {
    @ApiModelProperty(value = "状态")
    private Status status;

    @ApiModelProperty(value = "返回业务数据")
    private T data;

    private RPCResult(boolean ok) {
        this.status = new Status(ok);
    }

    private RPCResult(boolean ok, String errCode) {
        this.status = new Status(ok, errCode);
    }

    private RPCResult(boolean ok, String errCode, String message) {
        this.status = new Status(ok, errCode, message);
    }

    public void addMessage(String message) {
        this.status.getErrMetadata().addDetail(message);
    }

    /**
     * <p>
     * 请求成功
     * </p>
     *
     * @return com.felo.minutes.app.api.meet.request.RPCResult<T>
     * @author Karl
     * @date 2022/6/28 12:16
     */
    public static <T> ResponseEntity<RPCResult<T>> ok() {
        return ResponseEntity.ok(new RPCResult<>(true));
    }

    /**
     * <p>
     * 请求成功
     * </p>
     *
     * @param data data
     * @return com.felo.minutes.app.api.meet.request.RPCResult<T>
     * @author Karl
     * @date 2022/6/28 12:15
     */
    public static <T> ResponseEntity<RPCResult<T>> ok(T data) {
        RPCResult<T> result = new RPCResult<>(true);
        result.setData(data);
        return ResponseEntity.ok(result);
    }

    /**
     * <p>
     * 请求错误
     * </p>
     *
     * @param message message 错误信息
     * @return com.felo.minutes.app.api.meet.request.RPCResult<T>
     * @author Karl
     * @date 2022/6/28 12:15
     */
    public static <T> ResponseEntity<RPCResult<T>> badRequest(String message) {
        return ResponseEntity
                .badRequest()
                .body(new RPCResult<>(false, HttpStatus.BAD_REQUEST.name(), message));
    }

    /**
     * <p>
     * 请求错误
     * </p>
     *
     * @return com.felo.minutes.app.api.meet.request.RPCResult<T>
     * @author Karl
     * @date 2022/6/28 12:15
     */
    public static <T> ResponseEntity<RPCResult<T>> badRequest() {
        return ResponseEntity
                .badRequest()
                .body(new RPCResult<>(false, HttpStatus.BAD_REQUEST.name()));
    }

    /**
     * <p>
     * 系统发生错误
     * </p>
     *
     * @return com.felo.minutes.app.api.meet.request.RPCResult<T>
     * @author Karl
     * @date 2022/6/28 12:14
     */
    public static <T> ResponseEntity<RPCResult<T>> error() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RPCResult<>(false, HttpStatus.INTERNAL_SERVER_ERROR.name()));
    }
}
