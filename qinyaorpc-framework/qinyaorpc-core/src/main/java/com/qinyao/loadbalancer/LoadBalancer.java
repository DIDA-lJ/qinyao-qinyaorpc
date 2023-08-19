package com.qinyao.loadbalancer;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 负载均衡器的接口
 *
 * @author LinQi
 * @createTime 2023-07-07
 */
public interface LoadBalancer {
    
    // 他应该具备的能力，根据服务列表找到一个可以用的服务
    
    /**
     * 根据服务名获取一个可用的服务
     * @param serviceName 服务名称
     * @return 服务地址
     */
    InetSocketAddress selectServiceAddress(String serviceName,String group);
    
    /**
     * 当感知节点发生了动态上下线，我们需要重新进行负载均衡
     * @param serviceName 服务的名称
     */
    void reLoadBalance(String serviceName, List<InetSocketAddress> addresses);
}
