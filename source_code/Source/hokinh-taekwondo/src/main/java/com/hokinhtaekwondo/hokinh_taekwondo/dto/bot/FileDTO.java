package com.hokinhtaekwondo.hokinh_taekwondo.dto.bot;

import com.hokinhtaekwondo.hokinh_taekwondo.model.BotFile;
import lombok.Data;

import java.io.File;

@Data
public class FileDTO {
    private String id;
    private String name;
    private String size;
    private String uploadDate;
    private String status; // indexed, processing, error
    private String type;
    private String pythonFileId; // ID from Python vector store
}
