package com.yy.sign.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utils {


    public static String getDateStr(Date date, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String date2TimeStamp(String date, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);

            return Utils.round(String.valueOf(sdf.parse(date).getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String round(String date) {
        Long time = Long.valueOf(date);
        Random random = new Random();
        int i = random.nextInt(900) + 100;
        time = time + i;
        return String.valueOf(time);
    }

}

