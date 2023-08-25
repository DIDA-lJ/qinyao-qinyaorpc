package com.qinyao.channelhandler.handler;

import com.qinyao.compress.Compressor;
import com.qinyao.compress.CompressorFactory;
import com.qinyao.enumeration.RequestType;
import com.qinyao.serialize.Serializer;
import com.qinyao.serialize.SerializerFactory;
import com.qinyao.transport.message.MessageFormatConstant;
import com.qinyao.transport.message.RequestPayload;
import com.qinyao.transport.message.QinYaorpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 自定义协议编码器
 * <p>
 * <pre>
 *   0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20   21   22   23   24  25   26   27
 *   +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 *   |               magic                        |ver |head  len|    full length    |qt    ser|comp|              RequestId                |
 *   +-----+-----+--------------------------------+----+----+----+----+-----------+----- ---+--------+----+----+----+----+----+----+---+----+
 *   |                                                                                                                                      |
 *   |                                                   body                                                                               |
 *   |                                                                                                                                      |
 *   +--------------------------------------------------------------------------------------------------------------------------------------+
 * </pre>
 * <p>
 * <p>
 * 4 Byte full length 总长度
 * 1 Byte serialize 序列化方式
 * 1 Byte compress 压缩类型
 * 1 Byte requestType 请求类型
 * 8 Byte requestId 请求 ID
 * 9 Byte magic (魔术值) --> qinyaorpc.getBytes()
 * 1 Byte version(版本) --> 1
 * 2 Byte header length 首部的长度
 * 4 Byte full length 总长度
 * 1 Byte serialize 序列化方式
 * 1 Byte compress 压缩类型
 * 1 Byte requestType 请求类型
 * 8 Byte requestId 请求 ID
 *
 * @author LinQi
 * @createTime 2023-08-03
 */
@Slf4j
public class QinYaorpcRequestDecoder extends LengthFieldBasedFrameDecoder {
    public QinYaorpcRequestDecoder() {
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

    /**
     * 进行解码
     * @param ctx
     * @param in
     * @return
     * @throws Exception
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        Thread.sleep(new Random().nextInt(50));

        Object decode = super.decode(ctx, in);
        // 判断 decode 属不属于 byteBuf 的子类(这里使用了 JDK 17 的新语法)
        if (decode instanceof ByteBuf byteBuf) {
            // 解析我们的报文
            return decodeFrame(byteBuf);
        }
        // 抛异常也可以，返回空值也可以
        return null;
    }

    /**
     * 解析报文
     * @param byteBuf
     * @return
     */
    private Object decodeFrame(ByteBuf byteBuf) {
        // 1、解析魔数 (qinyaorpc)
        byte[] magic = new byte[MessageFormatConstant.MAGIC.length];
        byteBuf.readBytes(magic);
        // 检测魔数是否匹配,将 Magic 循环遍历就可以了。
        for (int i = 0; i < magic.length; i++) {
            // 如果两个的魔数值有一个不相等，这不进行判断，直接抛异常
            if (magic[i] != MessageFormatConstant.MAGIC[i]) {
                throw new RuntimeException("Magic value mismatch error : The request obtained is not legitimate。");
            }
        }

        // 2、解析版本号
        byte version = byteBuf.readByte();
        // 高版本兼容低版本，解析的版本不能比当前的解析版本大
        if (version > MessageFormatConstant.VERSION) {
            throw new RuntimeException("Version Not Supported Error : The requested version is not supported.");
        }

        // 3、解析头部的长度
        short headLength = byteBuf.readShort();

        // 4、解析总长度
        int fullLength = byteBuf.readInt();

        // 5、请求类型 --> 通过判断是否是心跳检测
        byte requestType = byteBuf.readByte();

        // 6、序列化类型 --> 序列化工厂进行操作
        byte serializeType = byteBuf.readByte();

        // 7、压缩类型 --> 压缩工厂进行改造
        byte compressType = byteBuf.readByte();

        // 8、请求id --> 用雪花算法生成
        long requestId = byteBuf.readLong();

        // 9、时间戳
        long timeStamp = byteBuf.readLong();

        // 我们需要封装，进行请求数据的封装
        QinYaorpcRequest qinYaorpcRequest = new QinYaorpcRequest();
        qinYaorpcRequest.setRequestType(requestType);
        qinYaorpcRequest.setCompressType(compressType);
        qinYaorpcRequest.setSerializeType(serializeType);
        qinYaorpcRequest.setRequestId(requestId);
        qinYaorpcRequest.setTimeStamp(timeStamp);

        // 心跳请求没有负载，此处可以判断并直接返回
        if (requestType == RequestType.HEART_BEAT.getId()) {
            return qinYaorpcRequest;
        }

        // 负载的长度 = 总长度 - 头部信息的长度
        int payloadLength = fullLength - headLength;
        byte[] payload = new byte[payloadLength];
        // 将字节数据读到字节数组中
        byteBuf.readBytes(payload);

        // 有了字节数组之后就可以解压缩，反序列化
        // 1、解压缩，如果负载为空，则不需要反序列化
        if (payload != null && payload.length != 0) {
            Compressor compressor = CompressorFactory.getCompressor(compressType).getImpl();
            payload = compressor.decompress(payload);


            // 2、反序列化，采用序列化工厂的方式进行序列化
            Serializer serializer = SerializerFactory.getSerializer(serializeType).getImpl();
            RequestPayload requestPayload = serializer.deserialize(payload, RequestPayload.class);
            qinYaorpcRequest.setRequestPayload(requestPayload);
            // 3.这里没有实现关流操作，因为需要实现复用的逻辑，这里就没有实现关流了
        }

        if (log.isDebugEnabled()) {
            log.debug("请求【{}】已经在服务端完成解码工作。", qinYaorpcRequest.getRequestId());
        }

        return qinYaorpcRequest;
    }
}
