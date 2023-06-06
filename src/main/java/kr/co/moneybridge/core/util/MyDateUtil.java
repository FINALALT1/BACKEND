package kr.co.moneybridge.core.util;

import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MyDateUtil {
    public static String toStringFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String localTimeToString(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static LocalTime StringToLocalTime(String string) {
        return LocalTime.parse(string, DateTimeFormatter.ofPattern("HH:mm"));
    }
}
