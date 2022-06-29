package com.bench.runtime.rpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * rpc body error metadata
 * </p>
 *
 * @author Karl
 * @date 2022/6/29 17:16
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ErrMetadata {
    private final List<String> message = new ArrayList<>();

    public ErrMetadata(String detail) {
        this.message.add(detail);
    }

    public void addDetail(String detail) {
        this.message.add(detail);
    }
}
