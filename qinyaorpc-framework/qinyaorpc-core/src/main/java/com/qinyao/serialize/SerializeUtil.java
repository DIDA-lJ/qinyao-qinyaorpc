package com.qinyao.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 序列化工具类
 * @author LinQi
 * @createTime 2023-08-04
 */
public class SerializeUtil {
    /**
     * 序列化方法
     * @param object 序列化对象
     * @return 字节数组
     */
    public static byte[] serialize(Object object) {
        // 针对不同的消息类型需要做不同的处理，心跳的请求，没有payload
        if (object == null) {
            return null;
        }
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
//            log.error("序列化时出现异常");
            throw new RuntimeException(e);
        }
    }
    
}
