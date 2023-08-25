package com.qinyao.core;

import com.qinyao.NettyBootstrapInitializer;
import com.qinyao.QinYaorpcBootstrap;
import com.qinyao.compress.CompressorFactory;
import com.qinyao.discovery.Registry;
import com.qinyao.enumeration.RequestType;
import com.qinyao.serialize.SerializerFactory;
import com.qinyao.transport.message.QinYaorpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 心跳探测的核心目的是什么？探活，感知哪些服务器的连接状态是正常的，哪些是不正常的
 *
 * @author LinQi
 * @createTime 2023-08-08
 */
@Slf4j
public class HeartbeatDetector {
    
    public static void detectHeartbeat(String ServiceName) {
        // 1、从注册中心拉取服务列表并建立连接
        Registry registry = QinYaorpcBootstrap.getInstance().getConfiguration().getRegistryConfig().getRegistry();
        List<InetSocketAddress> addresses = registry.lookup(ServiceName,
            QinYaorpcBootstrap.getInstance().getConfiguration().getGroup()
        );
        
        // 将连接进行缓存
        for (InetSocketAddress address : addresses) {
            try {
                if (!QinYaorpcBootstrap.CHANNEL_CACHE.containsKey(address)) {
                    Channel channel = NettyBootstrapInitializer.getBootstrap().connect(address).sync().channel();
                    QinYaorpcBootstrap.CHANNEL_CACHE.put(address, channel);
                }
                
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        
        // 3、任务，定期发送消息
        Thread thread = new Thread(() ->
            new Timer().scheduleAtFixedRate(new MyTimerTask(), 0, 2000)
            , "yrpc-HeartbeatDetector-thread");
        thread.setDaemon(true);
        thread.start();
        
    }
    
    private static class MyTimerTask extends TimerTask {
        
        @Override
        public void run() {
            
            // 将响应时长的map清空
            QinYaorpcBootstrap.ANSWER_TIME_CHANNEL_CACHE.clear();
            
            // 遍历所有的channel
            Map<InetSocketAddress, Channel> cache = QinYaorpcBootstrap.CHANNEL_CACHE;
            for (Map.Entry<InetSocketAddress, Channel> entry : cache.entrySet()) {
                // 定义一个重试的次数
                int tryTimes = 3;
                while (tryTimes > 0) {
                    // 通过心跳检测处理每一个channel
                    Channel channel = entry.getValue();
                    
                    long start = System.currentTimeMillis();
                    // 构建一个心跳请求
                    QinYaorpcRequest qinyaorpcrequest = QinYaorpcRequest.builder()
                        .requestId(QinYaorpcBootstrap.getInstance().getConfiguration().getIdGenerator().getId())
                        .compressType(CompressorFactory.getCompressor(QinYaorpcBootstrap.getInstance()
                            .getConfiguration().getCompressType()).getCode())
                        .requestType(RequestType.HEART_BEAT.getId())
                        .serializeType(SerializerFactory.getSerializer(QinYaorpcBootstrap.getInstance()
                            .getConfiguration().getSerializeType()).getCode())
                        .timeStamp(start)
                        .build();
                    
                    // 4、写出报文
                    CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                    // 将 completableFuture 暴露出去
                    QinYaorpcBootstrap.PENDING_REQUEST.put(qinyaorpcrequest.getRequestId(), completableFuture);
                    
                    channel.writeAndFlush(qinyaorpcrequest).addListener((ChannelFutureListener) promise -> {
                        if (!promise.isSuccess()) {
                            completableFuture.completeExceptionally(promise.cause());
                        }
                    });
                    
                    Long endTime = 0L;
                    try {
                        // 阻塞方法，get()方法如果得不到结果，就会一直阻塞
                        // 我们想不一直阻塞可以添加参数
                        completableFuture.get(1, TimeUnit.SECONDS);
                        endTime = System.currentTimeMillis();
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        // 一旦发生问题，需要优先重试
                        tryTimes --;
                        log.error("和地址为【{}】的主机连接发生异常.正在进行第【{}】次重试......",
                            channel.remoteAddress(), 3 - tryTimes);
                        
                        // 将重试的机会用尽，将失效的地址移出服务列表
                        if(tryTimes == 0){
                            QinYaorpcBootstrap.CHANNEL_CACHE.remove(entry.getKey());
                        }
                        
                        // 尝试等到一段时间后重试
                        try {
                            Thread.sleep(10*(new Random().nextInt(5)));
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
    
                        continue;
                    }
                    Long time = endTime - start;
                    
                    // 使用treemap进行缓存
                    QinYaorpcBootstrap.ANSWER_TIME_CHANNEL_CACHE.put(time, channel);
                    log.debug("和[{}]服务器的响应时间是[{}].", entry.getKey(), time);
                    break;
                }
            }
            
            log.info("-----------------------响应时间的treemap----------------------");
            for (Map.Entry<Long, Channel> entry : QinYaorpcBootstrap.ANSWER_TIME_CHANNEL_CACHE.entrySet()) {
                if (log.isDebugEnabled()) {
                    log.debug("[{}]--->channelId:[{}]", entry.getKey(), entry.getValue().id());
                }
            }
        }
    }
    
}
