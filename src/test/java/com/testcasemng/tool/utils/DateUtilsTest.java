package com.testcasemng.tool.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.testcasemng.tool.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtilsTest {
    @Test
    public void testHelloWorld() throws ParseException {
        //assertEquals("hello world", MessageBuilder.getHelloWorld());
        String format = "MM/dd/yy HH:mm:ss";
        String sample = "07/27/21 18:06:59";
        Date sampleDate = new SimpleDateFormat(format).parse(sample);
        assertEquals(DateUtils.getDateFormat(null, format), "");
        assertEquals(DateUtils.getDateFormat(sampleDate, format), sample);
    }
}
