package kr.co.moneybridge.core.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String localDateTimeToStringV2(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 mm분"))
                .replace("AM", "오전")
                .replace("PM", "오후");
    }

    public static LocalDateTime StringToLocalDateTime(String string) {
        if (string == null) {
            return null;
        }
        return LocalDateTime.parse(
                string,
                DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 mm분").withLocale(Locale.KOREAN)
        );
    }

    public static LocalDateTime StringToLocalDateTimeV2(String string) {
        if (string == null) {
            return null;
        }
        return LocalDateTime.parse(
                string,
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );
    }

    public static String localTimeToString(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static LocalTime StringToLocalTime(String string) {
        if (string == null) {
            return null;
        }
        return LocalTime.parse(string, DateTimeFormatter.ofPattern("HH:mm"));
    }
}
