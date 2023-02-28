package io.github.wulilh.simplerpc.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * rpc 请求对象
 *
 * @author wuliling Created By 2023-02-28 17:29
 **/
@Data
@Accessors(chain = true)
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 8944139129709448996L;

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
