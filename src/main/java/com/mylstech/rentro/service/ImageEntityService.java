package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.model.Image;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageEntityService {
    Image saveImage(String imageUrl);
    
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
    
    void deleteImage(Long imageId) throws IOException;
    
    List<Image> getAllImages();
    
    Image getImageById(Long imageId);

    @Transactional
    void cleanupOrphanedImages();
}
