package com.hokinhtaekwondo.hokinh_taekwondo.dto.user.imports;

import lombok.Getter;

import java.util.List;

@Getter
public class UserImportResult {

    private final int totalRows;
    private final int successCount;
    private final int failureCount;
    private final List<UserImportRowResult> rows;

    public UserImportResult(List<UserImportRowResult> rows) {
        this.rows = rows;
        this.totalRows = rows.size();
        this.successCount = (int) rows.stream().filter(UserImportRowResult::isSuccess).count();
        this.failureCount = totalRows - successCount;
    }

    public boolean hasErrors() {
        return failureCount > 0;
    }

    public List<UserImportRowResult> getFailedRows() {
        return rows.stream()
                .filter(r -> !r.isSuccess())
                .toList();
    }

    // getters
}

