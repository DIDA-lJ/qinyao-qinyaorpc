package com.qinyao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LinQi
 * @createTime 2023-07-04
 */
public class DateUtil {
    
    public static Date get(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return  sdf.parse(pattern);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
}
