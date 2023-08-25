package com.qinyao.enumeration;

/**
 * 用来标记请求类型
 *
 * @author LinQi
 * @createTime 2023-08-03
 */
public enum RequestType {
    
    REQUEST((byte)1,"普通请求"), HEART_BEAT((byte)2,"心跳检测请求");
    /**
     * 枚举请求的 id
     */
    private byte id;
    /**
     * 枚举请求的类型 --> 用于判断请求的类型，心跳请求没有负载
     */
    private String type;
    
    RequestType(byte id, String type) {
        this.id = id;
        this.type = type;
    }
    
    public byte getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
}
