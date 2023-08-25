package com.qinyao;

import com.qinyao.annotation.QinYaorpcService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LinQi
 * @createTime 2023-08-30
 */
@RestController
public class HelloController {
    
    // 需要注入一个代理对象
    @QinYaorpcService
    private HelloQinYaorpc HelloQinYaorpc;
    
    @GetMapping("hello")
    public String hello(){
        return HelloQinYaorpc.sayHi("provider");
    }
    
}
