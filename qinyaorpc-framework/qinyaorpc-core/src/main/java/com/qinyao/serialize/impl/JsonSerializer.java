package com.qinyao.serialize.impl;

import com.alibaba.fastjson2.JSON;
import com.qinyao.serialize.Serializer;
import com.qinyao.transport.message.RequestPayload;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author LinQi
 * @createTime 2023-07-04
 */
@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        
        byte[] result = JSON.toJSONBytes(object);
        if (log.isDebugEnabled()) {
            log.debug("对象【{}】已经完成了序列化操作，序列化后的字节数为【{}】", object, result.length);
        }
        return result;
        
    }
    
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || clazz == null) {
            return null;
        }
        T t = JSON.parseObject(bytes, clazz);
        if (log.isDebugEnabled()) {
            log.debug("类【{}】已经完成了反序列化操作.", clazz);
        }
        return t;
    }
    
    public static void main(String[] args) {
        Serializer serializer = new JsonSerializer();
    
        RequestPayload requestPayload = new RequestPayload();
        requestPayload.setInterfaceName("xxxx");
        requestPayload.setMethodName("yyy");
        requestPayload.setParametersValue(new Object[]{"xxxx"});
    
        byte[] serialize = serializer.serialize(requestPayload);
        System.out.println(Arrays.toString(serialize));
    
        RequestPayload deserialize = serializer.deserialize(serialize, RequestPayload.class);
        System.out.println(deserialize);
    
    
    }
    
}
