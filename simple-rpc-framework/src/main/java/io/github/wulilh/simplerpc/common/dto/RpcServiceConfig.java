package io.github.wulilh.simplerpc.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wuliling Created By 2023-02-28 17:55
 **/
@Data
@Accessors(chain = true)
public class RpcServiceConfig {

    /**
     * service version
     */
    private String version = "";
    /**
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String group = "";

    /**
     * target service
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
