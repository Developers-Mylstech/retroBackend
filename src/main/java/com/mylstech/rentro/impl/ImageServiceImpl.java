package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.response.EntityImagesResponse;
import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.model.*;
import com.mylstech.rentro.repository.*;
import com.mylstech.rentro.service.FileStorageService;
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
                response.setImageUrls(brand.getImages());
                break;

            case "category":
                Category category = categoryRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("Category not found with id: " + entityId));
                response.setImageUrls(category.getImageUrls());
                break;

            case "jobpost":
                JobPost jobPost = jobPostRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("JobPost not found with id: " + entityId));
                response.setSingleImage(jobPost.getImage());
                break;

            case "ourservices":
                OurService ourServices = ourServicesRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("OurServices not found with id: " + entityId));
                response.setImageUrls(ourServices.getImageUrl());
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
                                b.getImages(),
                                null))
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
                                jp.getImage()))
                        .toList();

            case "ourservices":
                return ourServicesRepository.findAll().stream()
                    .map(os -> new EntityImagesResponse(
                        os.getOurServiceId (),
                        "ourservices",
                        os.getImageUrl (),
                        null))
                    .toList();
                
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
        
        if (brand.getImages() == null) {
            brand.setImages(new ArrayList<>());
        }
        brand.getImages().add(fileUrl);
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
        jobPost.setImage(fileUrl);
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
        
        if (brand.getImages() != null && !brand.getImages().isEmpty()) {
            String imageUrl = brand.getImages().get(brand.getImages().size() - 1);
            brand.getImages().remove(imageUrl);
            brandRepository.save(brand);
            deleteImageFile(imageUrl);
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
        
        if (jobPost.getImage() != null) {
            String imageUrl = jobPost.getImage();
            jobPost.setImage(null);
            jobPostRepository.save(jobPost);
            deleteImageFile(imageUrl);
        }
    }

    private void handleOurServicesImage(Long ourServicesId, String fileUrl) {
        OurService ourServices = ourServicesRepository.findById(ourServicesId)
                .orElseThrow(() -> new RuntimeException("OurServices not found with id: " + ourServicesId));
        
        if (ourServices.getImageUrl() == null) {
            ourServices.setImageUrl(new ArrayList<>());
        }
        ourServices.getImageUrl().add(fileUrl);
        ourServicesRepository.save(ourServices);
    }

    private void deleteOurServicesImage(Long ourServicesId) {
        OurService ourServices = ourServicesRepository.findById(ourServicesId)
                .orElseThrow(() -> new RuntimeException("OurServices not found with id: " + ourServicesId));
        
        if (ourServices.getImageUrl() != null && !ourServices.getImageUrl().isEmpty()) {
            String imageUrl = ourServices.getImageUrl().get(ourServices.getImageUrl().size() - 1);
            ourServices.getImageUrl().remove(imageUrl);
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