package io.github.wulilh.simplerpc.remoting.transport;

import io.github.wulilh.simplerpc.common.dto.RpcRequest;

/**
 * @author wuliling Created By 2023-02-28 22:54
 **/
public interface RpcRequestTransport {

    Object invokeRpcRequest(RpcRequest rpcRequest);
}
