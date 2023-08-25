package com.qinyao.serialize;

/**
 * 序列化器，进行解耦
 *
 * @author LinQi
 * @createTime 2023-08-04
 */
public interface Serializer {
    
    /**
     * 抽象的用来做序列化的方法
     * @param object 待序列化的对象实例
     * @return 字节数组
     */
    byte[] serialize(Object object);
    
    /**
     * 反序列化的方法，使用泛型，并不需要进行具体的强转
     *
     * @param bytes 待反序列化的字节数组
     * @param clazz 目标类的class对象
     * @param <T>   目标类泛型
     * @return 目标实例
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
    
}
