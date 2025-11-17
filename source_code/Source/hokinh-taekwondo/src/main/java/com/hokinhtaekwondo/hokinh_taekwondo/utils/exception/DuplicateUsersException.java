package com.hokinhtaekwondo.hokinh_taekwondo.utils.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuplicateUsersException extends RuntimeException {
    private final List<String> duplicateIds;

    public DuplicateUsersException(List<String> ids) {
        super("Trong danh sách người dùng mới tạo, phát hiện một số người dùng bị trùng ID với " + ids.size() + " người dùng đã tồn tại trên hệ thống. Hãy sửa ID các người dùng có viền đỏ");
        this.duplicateIds = new ArrayList<>(ids); // defensive copy
    }

    public List<String> getDuplicateIds() {
        return Collections.unmodifiableList(duplicateIds);
    }
}
