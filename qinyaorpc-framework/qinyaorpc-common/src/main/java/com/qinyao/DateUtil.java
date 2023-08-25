package com.qinyao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LinQi
 * @createTime 2023-08-04
 */
public class DateUtil {
    /**
     * 根据传入的字符串传回一个具体的 Date,时间数据格式化
     * @param pattern
     * @return
     */
    public static Date get(String pattern) {
        // 日期格式化格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return  sdf.parse(pattern);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
}
