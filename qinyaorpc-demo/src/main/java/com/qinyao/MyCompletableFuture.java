package com.qinyao;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author LinQi
 * @createTime 2023-08-01
 */
public class MyCompletableFuture {
    
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
    
        /*
         * 可以获取子线程中的返回，过程中的结果，并可以在主线程中阻塞等待其完成
         */
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        
        new Thread( () -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int i = 8;
    
            completableFuture.complete(i);
            
        }).start();
    
        // get方法是一个阻塞的方法，
        Integer integer = completableFuture.get(1, TimeUnit.SECONDS);
        System.out.println(integer);
        // 如何在子线程中，获取到这个8
        
    }
    
}
