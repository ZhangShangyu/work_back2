package com.zsy.bmw.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {

    public static String getNowTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
