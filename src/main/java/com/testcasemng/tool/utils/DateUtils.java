package com.testcasemng.tool.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String getDateFormat(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        if (date == null)
            return "";
        return dateFormat.format(date);
    }
}
