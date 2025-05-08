package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.repository.ImageRepository;
import com.mylstech.rentro.service.FileStorageService;
import com.mylstech.rentro.service.ImageEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageEntityServiceImpl implements ImageEntityService {

    private final ImageRepository imageRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public Image saveImage(String imageUrl) {
        // Check if image already exists
        return imageRepository.findByImageUrl(imageUrl)
                .orElseGet(() -> {
                    Image image = new Image();
                    image.setImageUrl(imageUrl);
                    return imageRepository.save(image);
                });
    }

    @Override
    public FileUploadResponse uploadImage(MultipartFile file, int quality, boolean fallbackToJpeg) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileUrl = fileStorageService.storeImageAsWebP(file, quality, fallbackToJpeg);
        String contentType = fileUrl.endsWith(".webp") ? "image/webp" : "image/jpeg";
        
        // Save the image entity
        saveImage(fileUrl);
        
        return new FileUploadResponse(
            fileName,
            fileUrl,
            contentType,
            file.getSize()
        );
    }

    @Override
    public List<FileUploadResponse> uploadMultipleImages(
            MultipartFile[] files,
            int quality,
            boolean fallbackToJpeg) throws IOException {
        List<FileUploadResponse> responses = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                FileUploadResponse response = uploadImage(file, quality, fallbackToJpeg);
                responses.add(response);
            } catch (IOException e) {
                // Log error but continue processing other files
                e.printStackTrace();
            }
        }
        
        if (responses.isEmpty()) {
            throw new IOException("Failed to upload any files");
        }
        
        return responses;
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
        
        // Delete the physical file
        fileStorageService.deleteFile(image.getFilePath());
        
        // Delete the database record
        imageRepository.delete(image);
    }

    @Override
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @Override
    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
    }
}