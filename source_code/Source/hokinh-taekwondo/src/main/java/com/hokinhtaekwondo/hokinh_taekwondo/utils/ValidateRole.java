package com.hokinhtaekwondo.hokinh_taekwondo.utils;

import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;

public final class ValidateRole {
    private ValidateRole() {}
    public static boolean isResponsibleForFacility(User user, User facilityManager) {
        return user.getRole() == 0
                || (user.getIsActive() && facilityManager != null && facilityManager.getId().equals(user.getId()));
    }
}
