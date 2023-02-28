package io.github.wulilh.simplerpc.registry.zk;

import io.github.wulilh.simplerpc.common.constants.RpcConstants;
import io.github.wulilh.simplerpc.common.dto.RpcRequest;
import io.github.wulilh.simplerpc.common.dto.RpcServiceConfig;
import io.github.wulilh.simplerpc.common.enums.RpcErrorMessageEnum;
import io.github.wulilh.simplerpc.common.exception.RpcException;
import io.github.wulilh.simplerpc.common.extension.ExtensionLoader;
import io.github.wulilh.simplerpc.common.utils.ComUtils;
import io.github.wulilh.simplerpc.registry.ServiceManage;
import io.github.wulilh.simplerpc.registry.zk.utils.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import javax.imageio.spi.ServiceRegistry;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuliling Created By 2023-02-28 17:13
 **/
@Slf4j
public class ZkServiceManageImpl implements ServiceManage {

    /**
     * key: rpc service name(interface name + version + group)
     * value: service object
     */
    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;

    public ZkServiceManageImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            this.registerService(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, RpcConstants.PORT));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }

    private void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    /**
     * 服务注册
     *
     * @param rpcServiceName 服务注册名称
     * @param address        服务地址
     */
    public void registerService(String rpcServiceName, InetSocketAddress address) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + address.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }


    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    /**
     * 获取服务地址
     *
     * @param rpcRequest rpc request
     * @return socket address
     */
    @Override
    public InetSocketAddress lookupServiceAddress(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (ComUtils.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // load balancing
        // TODO: 2023/2/28
        Random random = new Random();
        String targetServiceUrl = serviceUrlList.get(random.nextInt(serviceUrlList.size()));
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
