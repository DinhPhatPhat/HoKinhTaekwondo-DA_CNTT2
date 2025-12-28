package com.hokinhtaekwondo.hokinh_taekwondo.utils.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class VietNamTime {
    private static final ZoneId VIETNAM_ZONE =
            ZoneId.of("Asia/Ho_Chi_Minh");

    private static final DateTimeFormatter ISO_NO_OFFSET =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    private VietNamTime() {} // prevent instantiation

    public static LocalDate nowDate() {
        return LocalDate.now(VIETNAM_ZONE);
    }

    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now(VIETNAM_ZONE);
    }

    public static String fromUtcIsoToVietnam(String utcIsoString) {
        return OffsetDateTime.parse(utcIsoString)
                .atZoneSameInstant(VIETNAM_ZONE)
                .toLocalDateTime()
                .format(ISO_NO_OFFSET);
    }
}
