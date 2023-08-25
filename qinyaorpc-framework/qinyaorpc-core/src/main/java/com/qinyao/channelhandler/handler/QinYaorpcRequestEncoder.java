package com.qinyao.channelhandler.handler;

import com.qinyao.compress.Compressor;
import com.qinyao.compress.CompressorFactory;
import com.qinyao.serialize.Serializer;
import com.qinyao.serialize.SerializerFactory;
import com.qinyao.transport.message.MessageFormatConstant;
import com.qinyao.transport.message.QinYaorpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义协议编码器
 * <p>
 * <pre>
 *   0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20   21   22   23   24  25   26   27
 *   +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 *   |               magic                        |ver |head  len|    full length    |qt  | ser |comp|              RequestId                |
 *   +-----+-----+--------------------------------+----+----+----+----+-----------+----- ---+--------+----+----+----+----+----+----+---+----+
 *   |                                                                                                                                      |
 *   |                                                   body                                                                               |
 *   |                                                                                                                                      |
 *   +--------------------------------------------------------------------------------------------------------------------------------------+
 * </pre>
 * </p>
 * <p>
 * 9 Byte magic (魔术值) --> qinyaorpc.getBytes()
 * 1 Byte version(版本) --> 1
 * 2 Byte header length 为了使得头部更加灵活 -->首部的长度
 * 4 Byte full length 报文总长度，通过减去首部的长度得到报文的长度
 * 1 Byte serialize 序列化方式
 * 1 Byte compress 压缩类型
 * 1 Byte requestType 请求类型
 * 8 Byte requestId 请求 ID --> 请求ID 使用雪花算法生成
 * </p>
 * @author LinQi
 * @createTime 2023-08-02
 */
@Slf4j
public class QinYaorpcRequestEncoder extends MessageToByteEncoder<QinYaorpcRequest> {
    
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, QinYaorpcRequest qinYaorpcRequest, ByteBuf byteBuf) throws Exception {
        // 封装报文
        // 9个字节的魔数值
        byteBuf.writeBytes(MessageFormatConstant.MAGIC);
        // 1个字节的版本号
        byteBuf.writeByte(MessageFormatConstant.VERSION);
        // 2个字节的头部的长度
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);
        // 总长度不清楚，不知道body的长度 writeIndex(写指针)，先保存当前写指针的位置，这里的写指针添加的常量为 4，即总长度占用的字节
        byteBuf.writerIndex(byteBuf.writerIndex() + MessageFormatConstant.FULL_FIELD_LENGTH);
        // 3个类型 --> 请求类型、序列化类型、压缩类型
        byteBuf.writeByte(qinYaorpcRequest.getRequestType());
        byteBuf.writeByte(qinYaorpcRequest.getSerializeType());
        byteBuf.writeByte(qinYaorpcRequest.getCompressType());
        // 8 字节的请求id
        byteBuf.writeLong(qinYaorpcRequest.getRequestId());

        byteBuf.writeLong(qinYaorpcRequest.getTimeStamp());

//        // 如果是心跳请求，就不处理请求体
//        if(qinYaorpcRequest.getRequestType() == RequestType.HEART_BEAT.getId()){
//            // 处理一下总长度，其实总长度 = header长度
//            int writerIndex = byteBuf.writerIndex();
//            byteBuf.writerIndex(MessageFormatConstant.MAGIC.length
//                + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.HEADER_FIELD_LENGTH
//            );
//            byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH);
//            byteBuf.writerIndex(writerIndex);
//            return;
//        }
        
        // 写入请求体（requestPayload）
        // 1、根据配置的序列化方式进行序列化
        // 怎么实现序列化
        // 1 ---> 工具类 耦合性很高
        // 2 ---> 抽象，抽象出一个对应的序列化器
        byte[] body = null;
        if (qinYaorpcRequest.getRequestPayload() != null) {
            Serializer serializer = SerializerFactory.getSerializer(qinYaorpcRequest.getSerializeType()).getImpl();
            body = serializer.serialize(qinYaorpcRequest.getRequestPayload());
            // 2、根据配置的压缩方式进行压缩
            Compressor compressor = CompressorFactory.getCompressor(qinYaorpcRequest.getCompressType()).getImpl();
            body = compressor.compress(body);
        }
        
        if (body != null) {
            byteBuf.writeBytes(body);
        }
        int bodyLength = body == null ? 0 : body.length;
        
        // 重新处理报文的总长度
        // 先保存当前的写指针的位置
        int writerIndex = byteBuf.writerIndex();
        // 将写指针的位置移动到总长度的位置上
        byteBuf.writerIndex(MessageFormatConstant.MAGIC.length
            + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.HEADER_FIELD_LENGTH
        );
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + bodyLength);
        // 将写指针归位
        byteBuf.writerIndex(writerIndex);
        
        if (log.isDebugEnabled()) {
            log.debug("请求【{}】已经完成报文的编码。", qinYaorpcRequest.getRequestId());
        }
        
    }
    
    
}
