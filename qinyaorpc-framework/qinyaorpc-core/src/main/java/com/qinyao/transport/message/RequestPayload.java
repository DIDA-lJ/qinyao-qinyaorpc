package com.qinyao.transport.message;

import lombok.*;

import java.io.Serializable;

/**
 * 他用来描述，请求调用方所请求的接口方法的描述
 * HelloQinYaorpc.sayHi("你好");
 * @author LinQi
 * @createTime 2023-07-02
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPayload implements Serializable {
    
    // 1、接口的名字 -- com.qinyao.HelloQinYaorpc
    private String interfaceName;
    
    // 2、方法的名字 --sayHi
    private String methodName;
    
    // 3、参数列表，参数分为参数类型和具体的参数
    // 参数类型用来确定重载方法，具体的参数用来执行方法调用
    private Class<?>[] parametersType;  // -- {java.long.String}
    private Object[] parametersValue;   // -- "你好"
    
    // 4、返回值的封装 -- {java.long.String}
    private Class<?> returnType;
    
}
