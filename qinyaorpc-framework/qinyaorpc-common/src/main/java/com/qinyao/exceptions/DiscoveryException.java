package com.qinyao.exceptions;

/**
 * @author LinQi
 * @createTime 2023-08-29
 */
public class DiscoveryException extends RuntimeException{
    
    public DiscoveryException() {
    }
    
    public DiscoveryException(String message) {
        super(message);
    }
    
    public DiscoveryException(Throwable cause) {
        super(cause);
    }
}
