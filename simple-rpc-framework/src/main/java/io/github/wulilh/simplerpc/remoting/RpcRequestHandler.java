package io.github.wulilh.simplerpc.remoting;

import io.github.wulilh.simplerpc.common.dto.RpcRequest;
import io.github.wulilh.simplerpc.common.exception.RpcException;
import io.github.wulilh.simplerpc.factory.SingletonFactory;
import io.github.wulilh.simplerpc.registry.ServiceManage;
import io.github.wulilh.simplerpc.registry.zk.ZkServiceManageImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wuliling Created By 2023-02-28 22:53
 **/
@Slf4j
public class RpcRequestHandler {

    private final ServiceManage serviceManage;

    public RpcRequestHandler() {
        // TODO: 2023/3/1
        this.serviceManage = SingletonFactory.getInstance(ZkServiceManageImpl.class);
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceManage.getService(rpcRequest.getRpcServiceName());
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }

}
