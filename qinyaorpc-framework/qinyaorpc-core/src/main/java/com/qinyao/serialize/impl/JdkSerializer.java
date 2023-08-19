package com.qinyao.serialize.impl;

import com.qinyao.exceptions.SerializeException;
import com.qinyao.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author LinQi
 * @createTime 2023-07-04
 */
@Slf4j
public class JdkSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        
        try (
            // 将流的定义写在这里会自动关闭，不需要在写finally
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
        ) {
            outputStream.writeObject(object);
            
            byte[] result = baos.toByteArray();
            if(log.isDebugEnabled()){
                log.debug("对象【{}】已经完成了序列化操作，序列化后的字节数为【{}】",object,result.length);
            }
            return result;
        } catch (IOException e) {
            log.error("序列化对象【{}】时放生异常.",object);
            throw new SerializeException(e);
        }
    }
    
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if(bytes == null || clazz == null){
            return null;
        }
        try (
            // 将流的定义写在这里会自动关闭，不需要在写finally
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(bais);
        ) {
             Object object = objectInputStream.readObject();
            if(log.isDebugEnabled()){
                log.debug("类【{}】已经完成了反序列化操作.",clazz);
            }
            return (T)object;
        } catch (IOException | ClassNotFoundException e) {
            log.error("反序列化对象【{}】时放生异常.",clazz);
            throw new SerializeException(e);
        }
    }
}
