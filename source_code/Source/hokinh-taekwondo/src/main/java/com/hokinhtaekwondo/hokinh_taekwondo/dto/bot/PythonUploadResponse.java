package com.hokinhtaekwondo.hokinh_taekwondo.dto.bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PythonUploadResponse {
    private boolean success;
    @JsonProperty("file_id")
    private String fileId;
    private String message;
}
