package io.github.wulilh.simplerpc.common.exception;

import io.github.wulilh.simplerpc.common.enums.RpcErrorMessageEnum;

/**
 * @author wuliling Created By 2023-02-28 17:39
 **/
public class RpcException extends RuntimeException {
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
