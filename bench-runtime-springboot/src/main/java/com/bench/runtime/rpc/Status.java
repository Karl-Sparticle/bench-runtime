package com.bench.runtime.rpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * RPC body status
 * </p>
 *
 * @author Karl
 * @date 2022/6/29 17:15
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Status {
    @ApiModelProperty(value = "请求成功与否,true:成功,false:失败", required = true)
    private boolean ok;

    @ApiModelProperty(value = "可读错误码")
    private String errCode;

    @ApiModelProperty(value = "提示信息")
    private ErrMetadata errMetadata;

    public Status(boolean ok) {
        this.ok = ok;
    }

    public Status(boolean ok, String errCode) {
        this.ok = ok;
        this.errCode = errCode;
    }

    public Status(boolean ok, String errCode, String message) {
        this.ok = ok;
        this.errCode = errCode;
        this.errMetadata = new ErrMetadata(message);
    }
}
