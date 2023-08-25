package com.qinyao;


import com.qinyao.discovery.RegistryConfig;

/**
 * @author LinQi
 * @createTime 2023-08-28
 */
public class ProviderApplication {
    
    public static void main(String[] args) {
        // 服务提供方，需要注册服务，启动服务
        // 1、封装要发布的服务
//        ServiceConfig<HelloQinYaorpc> service = new ServiceConfig<>();
//        service.setInterface(HelloQinYaorpc.class);
//        service.setRef(new HelloQinYaorpcImpl());
        // 2、定义注册中心
        
        // 3、通过启动引导程序，启动服务提供方
        //   （1） 配置 -- 应用的名称 -- 注册中心 -- 序列化协议 -- 压缩方式
        //   （2） 发布服务
        QinYaorpcBootstrap.getInstance()
            .application("first-qinyaorpc-provider")
            // 配置注册中心
            .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
            .serialize("jdk")
//             发布服务
//                .publish(service)
            // 扫包批量发布
            .scan("com.qinyao")
            // 启动服务
            .start();
    }
    
    
}
