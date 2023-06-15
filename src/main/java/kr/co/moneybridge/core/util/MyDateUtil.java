package kr.co.moneybridge.core.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MyDateUtil {
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String localDateTimeToStringV2(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 mm분"));
    }

    public static LocalDateTime StringToLocalDateTime(String string) {
        return LocalDateTime.parse(string,
                DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 mm분"));
    }

    public static String localTimeToString(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static LocalTime StringToLocalTime(String string) {
        return LocalTime.parse(string, DateTimeFormatter.ofPattern("HH:mm"));
    }
}
