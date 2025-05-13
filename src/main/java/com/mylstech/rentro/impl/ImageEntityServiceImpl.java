package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.response.EntityImagesResponse;
import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.dto.response.ImageDTO;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.repository.ImageRepository;
import com.mylstech.rentro.service.FileStorageService;
import com.mylstech.rentro.service.ImageEntityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger ( ImageEntityServiceImpl.class );

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
        Image savedImage = saveImage(fileUrl);
        
        return new FileUploadResponse(
            fileName,
            fileUrl,
            contentType,
            file.getSize(),
            new ImageDTO (savedImage)
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
        
        // Delete the file from storage
        try {
            fileStorageService.deleteImage(image.getImageUrl());
        } catch (IOException e) {
            // Log error but continue with database deletion
            e.printStackTrace();
        }
        
        // Delete from database
        imageRepository.delete(image);
    }

    @Override
    public List<EntityImagesResponse> getAllImages() {
        List<Image> images = imageRepository.findAll ( );
        return images.stream ().map ( image -> {
            return new EntityImagesResponse ( image.getImageId (), "image", image.getImageUrl ());
        }  ).toList ();

    }

    @Override
    public EntityImagesResponse getImageById(Long imageId) {
        Image image = imageRepository.findById ( imageId )
                .orElseThrow ( () -> new RuntimeException ( "Image not found with id: " + imageId ) );
        return new EntityImagesResponse ( image.getImageId (), "image", image.getImageUrl () );
    }

    @Transactional
    @Override
    public void cleanupOrphanedImages() {
        logger.info("Starting cleanup of orphaned images");
        
        // Find images not associated with any products
        List<Image> orphanedImages = imageRepository.findImagesWithNoProducts();
        
        if (orphanedImages.isEmpty()) {
            logger.info("No orphaned images found");
            return;
        }
        
        logger.info("Found {} orphaned images to clean up", orphanedImages.size());
        
        // Delete each orphaned image
        for (Image image : orphanedImages) {
            try {
                // Delete the file from storage
                fileStorageService.deleteImage(image.getImageUrl());
                
                // Delete from database
                imageRepository.delete(image);
                logger.debug("Deleted orphaned image with id: {}", image.getImageId());
            } catch (IOException e) {
                // Log error but continue with other deletions
                logger.error("Error deleting image file for image id: " + image.getImageId(), e);
                // Still delete from database
                imageRepository.delete(image);
            }
        }
        
        logger.info("Completed cleanup of orphaned images");
    }
}
