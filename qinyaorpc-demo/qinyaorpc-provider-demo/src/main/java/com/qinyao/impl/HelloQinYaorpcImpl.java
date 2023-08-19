package com.qinyao.impl;

import com.qinyao.HelloQinYaorpc;
import com.qinyao.annotation.QinYaorpcApi;

/**
 * @author LinQi
 * @createTime 2023-07-27
 */
@QinYaorpcApi(group = "primary")
public class HelloQinYaorpcImpl implements HelloQinYaorpc {
    @Override
    public String sayHi(String msg) {
        return "hi consumer:" + msg;
    }
}
