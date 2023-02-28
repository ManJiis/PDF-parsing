package io.github.wulilh.simplerpc.registry;

import io.github.wulilh.simplerpc.common.dto.RpcRequest;
import io.github.wulilh.simplerpc.common.dto.RpcServiceConfig;

import java.net.InetSocketAddress;

/**
 * rpc服务管理
 *
 * @author wuliling Created By 2023-02-28 16:58
 **/
public interface ServiceManage {

    /**
     * 服务注册
     *
     * @param rpcServiceConfig rpcServiceConfig
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

    /**
     * 服务注册
     *
     * @param rpcServiceName 服务注册名称
     * @param address        服务地址
     */
    void registerService(String rpcServiceName, InetSocketAddress address);

    /**
     * getService
     *
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * 获取服务地址
     *
     * @param rpcRequest rpc request
     * @return socket address
     */
    InetSocketAddress lookupServiceAddress(RpcRequest rpcRequest);
}
