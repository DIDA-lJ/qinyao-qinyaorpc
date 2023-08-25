package com.qinyao.impl;

import com.qinyao.HelloQinYaorpc2;
import com.qinyao.annotation.QinYaorpcApi;

/**
 * @author LinQi
 * @createTime 2023-08-27
 */
@QinYaorpcApi
public class HelloQinYaorpcImpl2 implements HelloQinYaorpc2 {
    @Override
    public String sayHi(String msg) {
        return "hi consumer:" + msg;
    }
}
