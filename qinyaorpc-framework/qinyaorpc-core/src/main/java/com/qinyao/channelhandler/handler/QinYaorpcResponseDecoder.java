package com.qinyao.channelhandler.handler;

import com.qinyao.compress.Compressor;
import com.qinyao.compress.CompressorFactory;
import com.qinyao.serialize.Serializer;
import com.qinyao.serialize.SerializerFactory;
import com.qinyao.transport.message.MessageFormatConstant;
import com.qinyao.transport.message.QinYaorpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 *  * /**
 *  * 自定义协议编码器
 *  * <p>
 *  * <pre>
 *  *   0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20   21   22   23   24  25   26   27
 *  *   +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 *  *   |               magic                        |ver |head  len|    full length    |code  ser|comp|              RequestId                |
 *  *   +-----+-----+--------------------------------+----+----+----+----+-----------+----- ---+--------+----+----+----+----+----+----+---+----+
 *  *   |                                                                                                                                      |
 *  *   |                                                   body                                                                               |
 *  *   |                                                                                                                                      |
 *  *   +--------------------------------------------------------------------------------------------------------------------------------------+
 *  * </pre>
 *  * <p>
 *  * <p>
 *  * <p>
 *  * 4 Byte full length 总长度
 *  * 1 Byte serialize 序列化方式
 *  * 1 Byte compress 压缩类型
 *  * 1 Byte requestType 请求类型
 *  * 8 Byte requestId 请求 ID
 *  * 9 Byte magic (魔术值) --> qinyaorpc.getBytes()
 *  * 1 Byte version(版本) --> 1
 *  * 2 Byte header length 首部的长度
 *  * 4 Byte full length 总长度
 *  * 1 Byte serialize 序列化方式
 *  * 1 Byte compress 压缩类型
 *  * 1 Byte code  响应码
 *  * 8 Byte requestId 请求 ID
 * <p>
 * 基于长度字段的帧解码器
 *
 * @author LinQi
 * @createTime 2023-08-03
 */
@Slf4j
public class QinYaorpcResponseDecoder extends LengthFieldBasedFrameDecoder {
    // 响应报文解析
    public QinYaorpcResponseDecoder() {
        super(
                // 找到当前报文的总长度，截取报文，截取出来的报文我们可以去进行解析
                // 最大帧的长度，超过这个maxFrameLength值会直接丢弃
                MessageFormatConstant.MAX_FRAME_LENGTH,
                // 长度的字段的偏移量，
                MessageFormatConstant.MAGIC.length + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.HEADER_FIELD_LENGTH,
                // 长度的字段的长度
                MessageFormatConstant.FULL_FIELD_LENGTH,
                // todo 负载的适配长度
                -(MessageFormatConstant.MAGIC.length + MessageFormatConstant.VERSION_LENGTH
                        + MessageFormatConstant.HEADER_FIELD_LENGTH + MessageFormatConstant.FULL_FIELD_LENGTH),
                0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf byteBuf) {
            return decodeFrame(byteBuf);
        }
        return null;
    }

    private Object decodeFrame(ByteBuf byteBuf) {
        // 1、解析魔数
        byte[] magic = new byte[MessageFormatConstant.MAGIC.length];
        byteBuf.readBytes(magic);
        // 检测魔数是否匹配
        for (int i = 0; i < magic.length; i++) {
            if (magic[i] != MessageFormatConstant.MAGIC[i]) {
                throw new RuntimeException("The request obtained is not legitimate。");
            }
        }

        // 2、解析版本号
        byte version = byteBuf.readByte();
        if (version > MessageFormatConstant.VERSION) {
            throw new RuntimeException("获得的请求版本不被支持。");
        }

        // 3、解析头部的长度
        short headLength = byteBuf.readShort();

        // 4、解析总长度
        int fullLength = byteBuf.readInt();

        // 5、请求类型
        byte responseCode = byteBuf.readByte();

        // 6、序列化类型
        byte serializeType = byteBuf.readByte();

        // 7、压缩类型
        byte compressType = byteBuf.readByte();

        // 8、请求id
        long requestId = byteBuf.readLong();

        // 9、时间戳
        long timeStamp = byteBuf.readLong();

        // 我们需要封装
        QinYaorpcResponse qinYaorpcResponse = new QinYaorpcResponse();
        qinYaorpcResponse.setCode(responseCode);
        qinYaorpcResponse.setCompressType(compressType);
        qinYaorpcResponse.setSerializeType(serializeType);
        qinYaorpcResponse.setRequestId(requestId);
        qinYaorpcResponse.setTimeStamp(timeStamp);

        // todo 心跳请求没有负载，此处可以判断并直接返回
//        if( requestType == RequestType.HEART_BEAT.getId()){
//            return qinyaorpcrequest;
//        }

        int bodyLength = fullLength - headLength;
        byte[] payload = new byte[bodyLength];
        byteBuf.readBytes(payload);

        if (payload.length > 0) {
            // 有了字节数组之后就可以解压缩，反序列化
            // 1、解压缩
            Compressor compressor = CompressorFactory.getCompressor(compressType).getImpl();
            payload = compressor.decompress(payload);

            // 2、反序列化
            Serializer serializer = SerializerFactory
                    .getSerializer(qinYaorpcResponse.getSerializeType()).getImpl();
            Object body = serializer.deserialize(payload, Object.class);
            qinYaorpcResponse.setBody(body);
        }

        if (log.isDebugEnabled()) {
            log.debug("响应【{}】已经在调用端完成解码工作。", qinYaorpcResponse.getRequestId());
        }

        return qinYaorpcResponse;
    }

    public static void main(String[] args) {
        int i = ~(-1 << 3);
        System.out.println(i);
    }
}
