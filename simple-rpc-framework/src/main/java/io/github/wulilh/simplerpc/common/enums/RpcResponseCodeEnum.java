package io.github.wulilh.simplerpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wuliling Created By 2023-02-28 23:31
 **/
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {
    /**
     * code,message
     */
    SUCCESS(200, "The remote call is successful"),
    FAIL(500, "The remote call is fail");

    private final int code;
    private final String message;
}
