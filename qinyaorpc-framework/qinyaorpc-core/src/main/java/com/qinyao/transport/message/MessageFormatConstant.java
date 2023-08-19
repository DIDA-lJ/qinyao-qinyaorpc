package com.qinyao.transport.message;

/**
 *  * <pre>
 *  *   0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20   21   22
 *  *   +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 *  *   |    magic          |ver |head  len|    full length    | qt | ser|comp|              RequestId                |
 *  *   +-----+-----+-------+----+----+----+----+-----------+----- ---+--------+----+----+----+----+----+----+---+---+
 *  *   |                                                                                                             |
 *  *   |                                         body                                                                |
 *  *   |                                                                                                             |
 *  *   +--------------------------------------------------------------------------------------------------------+---+
 *  * </pre>
 * @author LinQi
 * @createTime 2023-07-02
 */
public class MessageFormatConstant {
    
    public final static byte[] MAGIC = "yrpc".getBytes();
    public final static byte VERSION = 1;
    
    // 头部信息的长度
    public final static short HEADER_LENGTH = (byte)(MAGIC.length + 1 + 2 + 4 + 1 + 1 + 1 + 8 + 8);
    // 头部信息长度占用的字节数
    public static final int HEADER_FIELD_LENGTH = 2;
    
    public final static int MAX_FRAME_LENGTH = 1024 * 1024;
    
    public static final int VERSION_LENGTH = 1;
    
    // 总长度占用的字节数
    public static final int FULL_FIELD_LENGTH = 4;
}
