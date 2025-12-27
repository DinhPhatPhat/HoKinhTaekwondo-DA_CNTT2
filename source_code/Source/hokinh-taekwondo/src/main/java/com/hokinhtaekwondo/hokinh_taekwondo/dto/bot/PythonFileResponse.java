package com.hokinhtaekwondo.hokinh_taekwondo.dto.bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PythonFileResponse {
    private String id;
    private String name;
    private String size;
    @JsonProperty("upload_date")
    private String uploadDate;
    private String status;
    private String type;
}
