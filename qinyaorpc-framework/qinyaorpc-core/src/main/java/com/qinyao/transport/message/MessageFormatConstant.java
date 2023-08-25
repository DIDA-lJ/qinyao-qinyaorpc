package com.qinyao.transport.message;

/**
 * 自定义协议编码器
 * <p>
 * <pre>
 *   0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20   21   22   23   24  25   26   27
 *   +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 *   |               magic                        |ver |head  len|    full length    |code  ser|comp|              RequestId                |
 *   +-----+-----+--------------------------------+----+----+----+----+-----------+----- ---+--------+----+----+----+----+----+----+---+----+
 *   |                                                                                                                                      |
 *   |                                                   body                                                                               |
 *   |                                                                                                                                      |
 *   +--------------------------------------------------------------------------------------------------------------------------------------+
 * </pre>
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
 * @createTime 2023-08-02
 */
public class MessageFormatConstant {
    /**
     * qinyaorpc 魔术值封装 --> 9 个字节
     */
    public final static byte[] MAGIC = "qinyaorpc".getBytes();
    /**
     * 版本号 --> 1 个字节
     */
    public final static byte VERSION = 1;

    /**
     * 头部信息的长度 魔术值 + 版本号 + 首部长度 + 全部长度 + 请求类型 + 序列化类型  + 压缩 + 请求 ID
     */
    public final static short HEADER_LENGTH = (byte) (MAGIC.length + 1 + 2 + 4 + 1 + 1 + 1 + 8 );
    /**
     * 头部信息长度占用的字节数
     */
    public static final int HEADER_FIELD_LENGTH = 2;

    public final static int MAX_FRAME_LENGTH = 1024 * 1024;

    public static final int VERSION_LENGTH = 1;

    /**
     * 总长度占用的字节数
     */
    public static final int FULL_FIELD_LENGTH = 4;
}
