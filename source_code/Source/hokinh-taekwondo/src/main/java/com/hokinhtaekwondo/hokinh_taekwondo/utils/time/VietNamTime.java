package com.hokinhtaekwondo.hokinh_taekwondo.utils.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class VietNamTime {
    private static final ZoneId VIETNAM_ZONE =
            ZoneId.of("Asia/Ho_Chi_Minh");

    private VietNamTime() {} // prevent instantiation

    public static LocalDate nowDate() {
        return LocalDate.now(VIETNAM_ZONE);
    }

    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now(VIETNAM_ZONE);
    }
}
