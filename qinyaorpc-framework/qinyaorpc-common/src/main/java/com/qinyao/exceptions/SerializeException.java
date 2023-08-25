package com.qinyao.exceptions;

/**
 * @author LinQi
 * @createTime 2023-08-29
 */
public class SerializeException extends RuntimeException{
    
    public SerializeException() {
    }
    
    public SerializeException(String message) {
        super(message);
    }
    
    public SerializeException(Throwable cause) {
        super(cause);
    }
}
