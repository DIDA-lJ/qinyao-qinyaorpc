package com.qinyao.exceptions;

/**
 * @author LinQi
 * @createTime 2023-08-29
 */
public class NetworkException extends RuntimeException{
    
    public NetworkException() {
    }
    
    public NetworkException(String message) {
        super(message);
    }
    
    public NetworkException(Throwable cause) {
        super(cause);
    }
}
