package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private long size;
    private String error;
    private Image image;

    public FileUploadResponse(String fileName, String fileUrl, String fileType, long size) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.size = size;
        this.error = null;
    }
    
    public FileUploadResponse(String fileName, String fileUrl, String fileType, long size, Image image) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.size = size;
        this.error = null;
        this.image = image;
    }
}
