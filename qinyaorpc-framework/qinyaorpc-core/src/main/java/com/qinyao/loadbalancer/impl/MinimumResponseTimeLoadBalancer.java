package com.qinyao.loadbalancer.impl;

import com.qinyao.QinYaorpcBootstrap;
import com.qinyao.loadbalancer.AbstractLoadBalancer;
import com.qinyao.loadbalancer.Selector;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 最短响应时间的负载均衡策略
 * @author LinQi
 * @createTime 2023-07-08
 */
@Slf4j
public class MinimumResponseTimeLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new MinimumResponseTimeSelector(serviceList);
    }
    
    private static class MinimumResponseTimeSelector implements Selector {
        
        public MinimumResponseTimeSelector(List<InetSocketAddress> serviceList) {
        
        }
        
        @Override
        public InetSocketAddress getNext() {
            Map.Entry<Long, Channel> entry = QinYaorpcBootstrap.ANSWER_TIME_CHANNEL_CACHE.firstEntry();
            if (entry != null) {
                if (log.isDebugEnabled()){
                    log.debug("选取了响应时间为【{}ms】的服务节点.",entry.getKey());
                }
                return (InetSocketAddress) entry.getValue().remoteAddress();
            }
            
            // 直接从缓存中获取一个可用的就行了
            System.out.println("----->"+Arrays.toString(QinYaorpcBootstrap.CHANNEL_CACHE.values().toArray()));
            Channel channel = (Channel) QinYaorpcBootstrap.CHANNEL_CACHE.values().toArray()[0];
            return (InetSocketAddress)channel.remoteAddress();
        }
        
    }
}
