package com.qinyao;

import com.qinyao.discovery.Registry;
import com.qinyao.proxy.handler.RpcConsumerInvocationHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author LinQi
 * @createTime 2023-08-29
 */
@Slf4j
public class ReferenceConfig<T> {
    
    private Class<T> interfaceRef;
    
    private Registry registry;
    // 分组信息
    private String group;
    
    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }
    
    /**
     * 代理设计模式，生成一个api接口的代理对象，HelloQinYaorpc.sayHi("你好");
     * @return 代理对象
     */
    public T get() {
        // 此处一定是使用动态代理完成了一些工作
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<T>[] classes = new Class[]{interfaceRef};
        InvocationHandler handler = new RpcConsumerInvocationHandler(registry,interfaceRef,group);
        
        // 使用动态代理生成代理对象
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, handler);
        
        return (T) helloProxy;
    }
    
    
    public Class<T> getInterface() {
        return interfaceRef;
    }
    
    public void setInterfaceRef(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }
    
    public Registry getRegistry() {
        return registry;
    }
    
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public String getGroup() {
        return group;
    }
}
