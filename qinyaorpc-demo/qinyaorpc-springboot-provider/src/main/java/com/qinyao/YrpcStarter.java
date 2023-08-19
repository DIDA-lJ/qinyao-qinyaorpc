package com.qinyao;

import com.qinyao.discovery.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author LinQi
 * @createTime 2023-07-30
 */
@Component
@Slf4j
public class YrpcStarter implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(5000);
        log.info("yrpc 开始启动...");
        QinYaorpcBootstrap.getInstance()
            .application("first-yrpc-provider")
            // 配置注册中心
            .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
            .serialize("jdk")
            .scan("com.qinyao.impl")
            // 启动服务
            .start();
    }
}
