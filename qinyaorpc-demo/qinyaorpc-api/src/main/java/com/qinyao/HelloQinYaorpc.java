package com.qinyao;

import com.qinyao.annotation.TryTimes;

/**
 * @author LinQi
 * @createTime 2023-08-27
 */
public interface HelloQinYaorpc {

    /**
     * 通用接口，server和client都需要依赖
     * @param msg 发送的具体的消息
     * @return 返回的结果
     */
    @TryTimes(tryTimes = 3,intervalTime = 3000)
    String sayHi(String msg);

}
