package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.response.EntityImagesResponse;
import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.model.*;
import com.mylstech.rentro.repository.*;
import com.mylstech.rentro.service.FileStorageService;
import com.mylstech.rentro.service.ImageEntityService;
import com.mylstech.rentro.service.ImageService;
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
public class ImageServiceImpl implements ImageService {

    private final FileStorageService fileStorageService;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final JobPostRepository jobPostRepository;
    private final OurServiceRepository ourServicesRepository;
    private final ImageEntityService imageEntityService;
    private final ImageRepository imageRepository;

    @Override
    public FileUploadResponse uploadImage(MultipartFile file, int quality, boolean fallbackToJpeg) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileUrl = fileStorageService.storeImageAsWebP(file, quality, fallbackToJpeg);
        String contentType = fileUrl.endsWith(".webp") ? "image/webp" : "image/jpeg";
        
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
    public EntityImagesResponse getEntityImages(String entityType, Long entityId) {
        EntityImagesResponse response = new EntityImagesResponse();
        response.setEntityId(entityId);
        response.setEntityType(entityType);

        switch (entityType.toLowerCase()) {
            case "product":
                Product product = productRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + entityId));
                response.setImageUrls(product.getImageUrls());
                break;

            case "brand":
                Brand brand = brandRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("Brand not found with id: " + entityId));
                response.setSingleImage (brand.getImage().getImageUrl ());
                break;

            case "category":
                Category category = categoryRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("Category not found with id: " + entityId));
                response.setImageUrls(category.getImageUrls());
                break;

            case "jobpost":
                JobPost jobPost = jobPostRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("JobPost not found with id: " + entityId));
                response.setSingleImage(jobPost.getImageUrl ());
                break;

            case "ourservices":
                OurService ourServices = ourServicesRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("OurServices not found with id: " + entityId));
                // Set as single image instead of image URLs list
                response.setSingleImage(ourServices.getImageUrl());
                break;

            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }

        return response;
    }

    @Override
    public List<EntityImagesResponse> getAllImagesByEntityType(String entityType) {
        switch (entityType.toLowerCase()) {
            case "product":
                return productRepository.findAll().stream()
                        .map(pi -> new EntityImagesResponse(
                                pi.getProductId(),
                                "product",
                                pi.getImageUrls(),
                                null))
                        .toList();

            case "brand":
                return brandRepository.findAll().stream()
                        .map(b -> new EntityImagesResponse(
                                b.getBrandId(),
                                "brand",
                                null,
                                b.getImage().getImageUrl ()))
                        .toList();

            case "category":
                return categoryRepository.findAll().stream()
                        .map(c -> new EntityImagesResponse(
                                c.getCategoryId(),
                                "category",
                                c.getImageUrls(),
                                null))
                        .toList();

            case "jobpost":
                return jobPostRepository.findAll().stream()
                        .map(jp -> new EntityImagesResponse (
                                jp.getJobPostId(),
                                "jobpost",
                                null,
                                jp.getImageUrl ()))
                        .toList();

            case "ourservices":
                return ourServicesRepository.findAll().stream()
                    .map(os -> new EntityImagesResponse(
                        os.getOurServiceId (),
                        "ourservices",
                        null,
                        os.getImageUrl ())).toList();
                
            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }

    @Override
    @Transactional
    public FileUploadResponse uploadAndAssociateImage(
            String entityType,
            Long entityId,
            MultipartFile file,
            int quality,
            boolean fallbackToJpeg) throws IOException {
        
        // Upload the file first
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileUrl = fileStorageService.storeImageAsWebP(file, quality, fallbackToJpeg);
        String contentType = fileUrl.endsWith(".webp") ? "image/webp" : "image/jpeg";
        
        // Create response object
        FileUploadResponse response = new FileUploadResponse(
            fileName,
            fileUrl,
            contentType,
            file.getSize()
        );

        // Save image record
        imageEntityService.saveImage(fileUrl);

        // Associate the image URL with the appropriate entity
        switch (entityType.toLowerCase()) {
            case "product":
                handleProductImage(entityId, fileUrl);
                break;
            case "brand":
                handleBrandImage(entityId, fileUrl);
                break;
            case "category":
                handleCategoryImage(entityId, fileUrl);
                break;
            case "jobpost":
                handleJobPostImage(entityId, fileUrl);
                break;
            case "ourservices":
                handleOurServicesImage(entityId, fileUrl);
                break;
            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }

        return response;
    }

    @Override
    @Transactional
    public void deleteEntityImage(String entityType, Long entityId) {
        switch (entityType.toLowerCase()) {
            case "product":
                deleteProductImage(entityId);
                break;
            case "brand":
                deleteBrandImage(entityId);
                break;
            case "category":
                deleteCategoryImage(entityId);
                break;
            case "jobpost":
                deleteJobPostImage(entityId);
                break;
            case "ourservices":
                deleteOurServicesImage(entityId);
                break;
            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }

    private void handleProductImage(Long productId, String fileUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (product.getImageUrls() == null) {
            product.setImageUrls(new ArrayList<>());
        }
        product.getImageUrls().add(fileUrl);
        productRepository.save(product);
    }

    private void handleBrandImage(Long brandId, String fileUrl) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + brandId));
        
        // Create a new Image entity
        Image image = new Image();
        image.setImageUrl(fileUrl);
        image = imageRepository.save(image);
        
        // Set the image for the brand
        brand.setImage(image);
        brandRepository.save(brand);
    }

    private void handleCategoryImage(Long categoryId, String fileUrl) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        
        if (category.getImageUrls() == null) {
            category.setImageUrls(new ArrayList<>());
        }
        category.getImageUrls().add(fileUrl);
        categoryRepository.save(category);
    }

    private void handleJobPostImage(Long jobPostId, String fileUrl) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new RuntimeException("JobPost not found with id: " + jobPostId));
        jobPost.setImageUrl (fileUrl);
        jobPostRepository.save(jobPost);
    }

    private void deleteProductImage(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            String imageUrl = product.getImageUrls().get(product.getImageUrls().size() - 1);
            product.getImageUrls().remove(imageUrl);
            productRepository.save(product);
            deleteImageFile(imageUrl);
        }
    }

    private void deleteBrandImage(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + brandId));
        
        // Store the image ID before clearing it
        Long imageId = null;
        if (brand.getImage() != null) {
            imageId = brand.getImage().getImageId();
        }
        
        // Clear the image reference
        brand.setImage(null);
        brandRepository.save(brand);
        
        // Delete the image if it exists and is not used elsewhere
        if (imageId != null) {
            int usageCount = imageRepository.countUsagesOfImage(imageId);
            if (usageCount == 0) {
                imageRepository.deleteById(imageId);
            }
        }
    }

    private void deleteCategoryImage(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        
        if (category.getImageUrls() != null && !category.getImageUrls().isEmpty()) {
            String imageUrl = category.getImageUrls().get(category.getImageUrls().size() - 1);
            category.getImageUrls().remove(imageUrl);
            categoryRepository.save(category);
            deleteImageFile(imageUrl);
        }
    }

    private void deleteJobPostImage(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new RuntimeException("JobPost not found with id: " + jobPostId));
        
        if (jobPost.getImageUrl () != null) {
            String imageUrl = jobPost.getImageUrl ();
            jobPost.setImageUrl (null);
            jobPostRepository.save(jobPost);
            deleteImageFile(imageUrl);
        }
    }

    private void handleOurServicesImage(Long ourServicesId, String fileUrl) {
        OurService ourServices = ourServicesRepository.findById(ourServicesId)
                .orElseThrow(() -> new RuntimeException("OurServices not found with id: " + ourServicesId));
        
        if (ourServices.getImageUrl() != null) {
            ourServices.setImageUrl(fileUrl);
        }

        ourServicesRepository.save(ourServices);
    }

    private void deleteOurServicesImage(Long ourServicesId) {
        OurService ourServices = ourServicesRepository.findById(ourServicesId)
                .orElseThrow(() -> new RuntimeException("OurServices not found with id: " + ourServicesId));
        
        if (ourServices.getImageUrl() != null) {
            String imageUrl = ourServices.getImageUrl();
            ourServices.setImageUrl(null);
            ourServicesRepository.save(ourServices);
            deleteImageFile(imageUrl);
        }
    }

    private void deleteImageFile(String imageUrl) {
        try {
            String filePath = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            fileStorageService.deleteFile(filePath);
        } catch (IOException e) {
            // Log error but don't throw - the database update was successful
            e.printStackTrace();
        }
    }
}
