package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.response.EntityImagesResponse;
import com.mylstech.rentro.dto.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    FileUploadResponse uploadImage(
        MultipartFile file, 
        int quality, 
        boolean fallbackToJpeg
    ) throws IOException;
    
    List<FileUploadResponse> uploadMultipleImages(
        MultipartFile[] files,
        int quality,
        boolean fallbackToJpeg
    ) throws IOException;

    FileUploadResponse uploadAndAssociateImage(
        String entityType, 
        Long entityId, 
        MultipartFile file, 
        int quality, 
        boolean fallbackToJpeg
    ) throws IOException;
    
    EntityImagesResponse getEntityImages(String entityType, Long entityId);
    
    void deleteEntityImage(String entityType, Long entityId);

    List<EntityImagesResponse> getAllImagesByEntityType(String entityType);
}
