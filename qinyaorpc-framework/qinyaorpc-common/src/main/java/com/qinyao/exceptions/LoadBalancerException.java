package com.qinyao.exceptions;

/**
 * @author LinQi
 * @createTime 2023-08-08
 */
public class LoadBalancerException extends RuntimeException {
    
    public LoadBalancerException(String message) {
        super(message);
    }
    
    public LoadBalancerException() {
    }
}
