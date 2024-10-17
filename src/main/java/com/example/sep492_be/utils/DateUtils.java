package com.example.sep492_be.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtils {
    private static final ZoneId HO_CHI_MINH_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    // Convert epoch milliseconds to start of the day in Ho Chi Minh timezone and return as epoch milliseconds
    public static Long convertToStartOfDayInMillis(Long epochMillis) {
        if(epochMillis == null) return null;
        Instant instant = Instant.ofEpochMilli(epochMillis);
        ZonedDateTime zonedDateTime = instant.atZone(HO_CHI_MINH_ZONE);
        LocalDate localDate = zonedDateTime.toLocalDate();
        ZonedDateTime startOfDay = localDate.atStartOfDay(HO_CHI_MINH_ZONE);
        return startOfDay.toInstant().toEpochMilli();
    }

    // Convert epoch milliseconds to end of the day in Ho Chi Minh timezone and return as epoch milliseconds
    public static Long convertToEndOfDayInMillis(Long epochMillis) {
        if(epochMillis == null) return null;

        Instant instant = Instant.ofEpochMilli(epochMillis);
        ZonedDateTime zonedDateTime = instant.atZone(HO_CHI_MINH_ZONE);
        LocalDate localDate = zonedDateTime.toLocalDate();
        ZonedDateTime endOfDay = localDate.atTime(23, 59, 59, 999999999).atZone(HO_CHI_MINH_ZONE);
        return endOfDay.toInstant().toEpochMilli();
    }

}
