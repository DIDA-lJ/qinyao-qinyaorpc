package com.qinyao;

/**
 * @author LinQi
 * @createTime 2023-08-29
 */
public class ServiceConfig<T> {
    private Class<?> interfaceProvider;
    private Object ref;
    private String group = "default";
    
    public Class<?> getInterface() {
     return interfaceProvider;
    }

    public void setInterface(Class<?> interfaceProvider) {
     this.interfaceProvider = interfaceProvider;
    }

    public Object getRef() {
     return ref;
    }

    public void setRef(Object ref) {
     this.ref = ref;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public String getGroup() {
        return group;
    }
}
