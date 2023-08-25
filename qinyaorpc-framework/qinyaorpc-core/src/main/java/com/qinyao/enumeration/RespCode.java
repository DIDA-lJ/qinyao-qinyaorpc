package com.qinyao.enumeration;

/**
 * 响应码需要做统一的处理
 * 成功码  20(方法成功调用)   21（心跳成功反回）
 * 负载码  31（服务器负载过高，被限流）
 * 错误码（客户端错误）  44
 * 错误码（服务端错误）  50（请求的方法不存在）

 * @author LinQi
 * @createTime 2023-08-03
 */
public enum RespCode {
    
    SUCCESS((byte) 20,"成功"),
    SUCCESS_HEART_BEAT((byte) 21,"心跳检测成功返回"),
    RATE_LIMIT((byte)31,"服务被限流" ),
    RESOURCE_NOT_FOUND((byte)44,"请求的资源不存在" ),
    FAIL((byte)50,"调用方法发生异常"),
    BECOLSING((byte)51,"调用方法发生异常");
    /**
     * 响应码
     */
    private byte code;
    /**
     * 响应描述
     */
    private String desc;
    
    RespCode(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public byte getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}
