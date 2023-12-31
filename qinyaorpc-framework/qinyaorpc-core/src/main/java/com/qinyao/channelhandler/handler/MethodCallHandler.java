package com.qinyao.channelhandler.handler;

import com.qinyao.ServiceConfig;
import com.qinyao.QinYaorpcBootstrap;
import com.qinyao.core.ShutDownHolder;
import com.qinyao.enumeration.RequestType;
import com.qinyao.enumeration.RespCode;
import com.qinyao.protection.RateLimiter;
import com.qinyao.protection.TokenBuketRateLimiter;
import com.qinyao.transport.message.RequestPayload;
import com.qinyao.transport.message.QinYaorpcRequest;
import com.qinyao.transport.message.QinYaorpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Map;

/**
 * @author LinQi
 * @createTime 2023-08-03
 */
@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler<QinYaorpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, QinYaorpcRequest qinyaorpcrequest) throws Exception {
    
        // 1、先封装部分响应
        QinYaorpcResponse qinYaorpcResponse = new QinYaorpcResponse();
        qinYaorpcResponse.setRequestId(qinyaorpcrequest.getRequestId());
        qinYaorpcResponse.setCompressType(qinyaorpcrequest.getCompressType());
        qinYaorpcResponse.setSerializeType(qinyaorpcrequest.getSerializeType());
    
        // 2、 获得通道
        Channel channel = channelHandlerContext.channel();
        
        // 3、查看关闭的挡板是否打开，如果挡板已经打开，返回一个错误的响应
        if( ShutDownHolder.BAFFLE.get() ){
            qinYaorpcResponse.setCode(RespCode.BECOLSING.getCode());
            channel.writeAndFlush(qinYaorpcResponse);
            return;
        }
        
        // 4、计数器加一
        ShutDownHolder.REQUEST_COUNTER.increment();
        
        // 4、完成限流相关的操作
        SocketAddress socketAddress = channel.remoteAddress();
        Map<SocketAddress, RateLimiter> everyIpRateLimiter =
            QinYaorpcBootstrap.getInstance().getConfiguration().getEveryIpRateLimiter();
        
        RateLimiter rateLimiter = everyIpRateLimiter.get(socketAddress);
        if (rateLimiter == null) {
            rateLimiter = new TokenBuketRateLimiter(10, 10);
            everyIpRateLimiter.put(socketAddress, rateLimiter);
        }
        boolean allowRequest = rateLimiter.allowRequest();
        
        // 5、处理请求的逻辑
        // 限流
        if (!allowRequest) {
            // 需要封装响应并且返回了
            qinYaorpcResponse.setCode(RespCode.RATE_LIMIT.getCode());
            
            // 处理心跳
        } else if (qinyaorpcrequest.getRequestType() == RequestType.HEART_BEAT.getId()) {
            // 需要封装响应并且返回
           qinYaorpcResponse.setCode(RespCode.SUCCESS_HEART_BEAT.getCode());
           
           // 正常调用
        } else {
            /** ---------------具体的调用过程--------------**/
            // （1）获取负载内容
            RequestPayload requestPayload = qinyaorpcrequest.getRequestPayload();
    
            // （2）根据负载内容进行方法调用
            try {
                Object result = callTargetMethod(requestPayload);
                if (log.isDebugEnabled()) {
                    log.debug("请求【{}】已经在服务端完成方法调用。", qinyaorpcrequest.getRequestId());
                }
                // （3）封装响应   我们是否需要考虑另外一个问题，响应码，响应类型
                qinYaorpcResponse.setCode(RespCode.SUCCESS.getCode());
                qinYaorpcResponse.setBody(result);
            } catch (Exception e){
                log.error("编号为【{}】的请求在调用过程中发生异常。",qinyaorpcrequest.getRequestId(),e);
                qinYaorpcResponse.setCode(RespCode.FAIL.getCode());
            }
        }
        
        // 6、写出响应
        channel.writeAndFlush(qinYaorpcResponse);
        
        // 7、计数器减一
        ShutDownHolder.REQUEST_COUNTER.decrement();
    }

    /**
     * 根据负载内容进行方法调用
     * @param requestPayload 请求载荷
     * @return
     */
    private Object callTargetMethod(RequestPayload requestPayload) {
        // 获取元数据
        String interfaceName = requestPayload.getInterfaceName();
        String methodName = requestPayload.getMethodName();
        Class<?>[] parametersType = requestPayload.getParametersType();
        Object[] parametersValue = requestPayload.getParametersValue();
        
        // 寻找到匹配的暴露出去的具体的实现
        ServiceConfig<?> serviceConfig = QinYaorpcBootstrap.SERVERS_LIST.get(interfaceName);
        Object refImpl = serviceConfig.getRef();
        
        // 通过反射调用 1、获取方法对象  2、执行invoke方法
        // 这里需要进行赋值，否则可能产生空指针异常
        Object returnValue;
        try {
            Class<?> aClass = refImpl.getClass();
            Method method = aClass.getMethod(methodName, parametersType);
            returnValue = method.invoke(refImpl, parametersValue);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            log.error("调用服务【{}】的方法【{}】时发生了异常。", interfaceName, methodName, e);
            throw new RuntimeException(e);
        }
        return returnValue;
    }
}
