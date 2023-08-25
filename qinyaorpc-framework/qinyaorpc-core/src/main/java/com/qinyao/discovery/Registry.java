package com.qinyao.discovery;

import com.qinyao.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 思考注册中心，应该具有什么样的能力
 *
 * @author LinQi
 * @createTime 2023-08-30
 */
public interface Registry {
    
    /**
     * 注册服务
     * @param serviceConfig 服务的配置内容
     */
    void register(ServiceConfig<?> serviceConfig);
    
    /**
     * 从注册中心拉取服务列表
     * @param serviceName 服务的名称
     * @return 服务的地址
     */
    List<InetSocketAddress> lookup(String serviceName,String group);
    
}
