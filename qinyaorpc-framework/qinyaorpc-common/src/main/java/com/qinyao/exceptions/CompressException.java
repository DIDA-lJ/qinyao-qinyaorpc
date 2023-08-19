package com.qinyao.exceptions;

/**
 * @author LinQi
 * @createTime 2023-07-29
 */
public class CompressException extends RuntimeException{
    
    public CompressException() {
    }
    
    public CompressException(String message) {
        super(message);
    }
    
    public CompressException(Throwable cause) {
        super(cause);
    }
}
