package com.qinyao.channelhandler;

import com.qinyao.channelhandler.handler.MySimpleChannelInboundHandler;
import com.qinyao.channelhandler.handler.QinYaorpcRequestEncoder;
import com.qinyao.channelhandler.handler.QinYaorpcResponseDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author LinQi
 * @createTime 2023-08-02
 */
public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
            // netty自带的日志处理器
            .addLast(new LoggingHandler(LogLevel.DEBUG))
            // 消息编码器
            .addLast(new QinYaorpcRequestEncoder())
            // 入栈的解码器
            .addLast(new QinYaorpcResponseDecoder())
            // 处理结果
            .addLast(new MySimpleChannelInboundHandler());
        
    }
}
