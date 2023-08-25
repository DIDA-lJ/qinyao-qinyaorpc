package com.qinyao.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于序列化化的包装类，即动态传值
 * @author LinQi
 * @createTime 2023-08-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectWrapper<T> {
    private Byte code;
    private String name;
    private T impl;
}
