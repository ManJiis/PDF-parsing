package io.github.wulilh.simplerpc.remoting;

import io.github.wulilh.simplerpc.common.dto.RpcRequest;
import io.github.wulilh.simplerpc.common.dto.RpcResponse;
import io.github.wulilh.simplerpc.common.dto.RpcServiceConfig;
import io.github.wulilh.simplerpc.remoting.transport.RpcRequestTransport;
import io.github.wulilh.simplerpc.remoting.transport.socket.SocketRpcClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author wuliling Created By 2023-02-28 23:21
 **/
@Slf4j
@SuppressWarnings("unchecked")
public class RpcClientProxy implements InvocationHandler {

    /**
     * implementations: socket and netty
     */
    private final RpcRequestTransport rpcRequestTransport;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcServiceConfig rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest()
                .setRequestId(UUID.randomUUID().toString())
                .setMethodName(method.getName())
                .setParameters(args)
                .setInterfaceName(method.getDeclaringClass().getName())
                .setGroup(rpcServiceConfig.getGroup())
                .setVersion(rpcServiceConfig.getVersion());
        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.invokeRpcRequest(rpcRequest);
        }
        return rpcResponse.getData();
    }
}
