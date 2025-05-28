package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.response.EntityImagesResponse;
import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
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

    private static final String OURSERVICES = "ourservices";
    private static final String OUR_SERVICES_NOT_FOUND_WITH_ID = "OurServices not found with id: ";
    private static final String PRODUCT = "product";
    private static final String PRODUCT_NOT_FOUND_WITH_ID = "Product not found with id: ";
    private static final String BRAND = "brand";
    private static final String BRAND_NOT_FOUND_WITH_ID = "Brand not found with id: ";
    private static final String CATEGORY = "category";
    private static final String CATEGORY_NOT_FOUND_WITH_ID = "Category not found with id: ";
    private static final String JOBPOST = "jobpost";
    private static final String JOB_POST_NOT_FOUND_WITH_ID = "JobPost not found with id: ";
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
        String originalFilename = file.getOriginalFilename ( );
        if ( originalFilename == null ) {
            throw new IllegalArgumentException ( "File name cannot be null" );
        }
        String fileName = StringUtils.cleanPath ( originalFilename );
        String fileUrl = fileStorageService.storeImageAsWebP ( file, quality, fallbackToJpeg );
        String contentType = fileUrl.endsWith ( ".webp" ) ? "image/webp" : "image/jpeg";

        return new FileUploadResponse (
                fileName,
                fileUrl,
                contentType,
                file.getSize ( )
        );
    }

    @Override
    public List<FileUploadResponse> uploadMultipleImages(
            MultipartFile[] files,
            int quality,
            boolean fallbackToJpeg) throws IOException {
        List<FileUploadResponse> responses = new ArrayList<> ( );

        for (MultipartFile file : files) {
            try {
                FileUploadResponse response = uploadImage ( file, quality, fallbackToJpeg );
                responses.add ( response );
            }
            catch ( IOException e ) {
                // Log error but continue processing other files
                e.printStackTrace ( );
            }
        }

        if ( responses.isEmpty ( ) ) {
            throw new IOException ( "Failed to upload any files" );
        }

        return responses;
    }

    @Override
    public EntityImagesResponse getEntityImages(String entityType, Long entityId) {
        EntityImagesResponse response = new EntityImagesResponse ( );
        response.setEntityId ( entityId );
        response.setEntityType ( entityType );

        switch (entityType.toLowerCase ( )) {
            case PRODUCT:
                Product product = productRepository.findById ( entityId )
                        .orElseThrow ( () -> new ResourceNotFoundException ( PRODUCT_NOT_FOUND_WITH_ID + entityId ) );
                response.setImageUrls ( product.getImageUrls ( ) );
                break;

            case BRAND:
                Brand brand = brandRepository.findById ( entityId )
                        .orElseThrow ( () -> new ResourceNotFoundException ( BRAND_NOT_FOUND_WITH_ID + entityId ) );
                response.setSingleImage ( brand.getImage ( ).getImageUrl ( ) );
                break;

            case CATEGORY:
                Category category = categoryRepository.findById ( entityId )
                        .orElseThrow ( () -> new ResourceNotFoundException ( CATEGORY_NOT_FOUND_WITH_ID + entityId ) );
                response.setImageUrls ( category.getImageUrls ( ) );
                break;

            case JOBPOST:
                JobPost jobPost = jobPostRepository.findById ( entityId )
                        .orElseThrow ( () -> new ResourceNotFoundException ( JOB_POST_NOT_FOUND_WITH_ID + entityId ) );
                response.setSingleImage ( jobPost.getImage ( ).getImageUrl ( ) );
                break;

            case OURSERVICES:
                OurService ourServices = ourServicesRepository.findById ( entityId )
                        .orElseThrow ( () -> new ResourceNotFoundException ( OUR_SERVICES_NOT_FOUND_WITH_ID + entityId ) );
                // Set as single image instead of image URLs list
                response.setSingleImage ( ourServices.getImage ( ).getImageUrl ( ) );
                break;

            default:
                throw new IllegalArgumentException ( "Unknown entity type: " + entityType );
        }

        return response;
    }

    @Override
    public List<EntityImagesResponse> getAllImagesByEntityType(String entityType) {
        switch (entityType.toLowerCase ( )) {
            case PRODUCT:
                return productRepository.findAll ( ).stream ( )
                        .map ( pi -> new EntityImagesResponse (
                                pi.getProductId ( ),
                                PRODUCT,
                                pi.getImageUrls ( ),
                                null ) )
                        .toList ( );

            case BRAND:
                return brandRepository.findAll ( ).stream ( )
                        .map ( b -> new EntityImagesResponse (
                                b.getBrandId ( ),
                                BRAND,
                                null,
                                b.getImage ( ).getImageUrl ( ) ) )
                        .toList ( );

            case CATEGORY:
                return categoryRepository.findAll ( ).stream ( )
                        .map ( c -> new EntityImagesResponse (
                                c.getCategoryId ( ),
                                CATEGORY,
                                c.getImageUrls ( ),
                                null ) )
                        .toList ( );

            case JOBPOST:
                return jobPostRepository.findAll ( ).stream ( )
                        .map ( jp -> new EntityImagesResponse (
                                jp.getJobPostId ( ),
                                JOBPOST,
                                null,
                                jp.getImage ( ).getImageUrl ( ) ) )
                        .toList ( );

            case OURSERVICES:
                return ourServicesRepository.findAll ( ).stream ( )
                        .map ( os -> new EntityImagesResponse (
                                os.getOurServiceId ( ),
                                OURSERVICES,
                                null,
                                os.getImage ( ).getImageUrl ( ) )
                        ).toList ( );

            default:
                throw new IllegalArgumentException ( "Unknown entity type: " + entityType );
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
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }
        String fileName = StringUtils.cleanPath(originalFilename);
        String fileUrl = fileStorageService.storeImageAsWebP ( file, quality, fallbackToJpeg );
        String contentType = fileUrl.endsWith ( ".webp" ) ? "image/webp" : "image/jpeg";

        // Create response object
        FileUploadResponse response = new FileUploadResponse (
                fileName,
                fileUrl,
                contentType,
                file.getSize ( )
        );

        // Save image record
        imageEntityService.saveImage ( fileUrl );

        // Associate the image URL with the appropriate entity
        switch (entityType.toLowerCase ( )) {
            case PRODUCT:
                handleProductImage ( entityId, fileUrl );
                break;
            case BRAND:
                handleBrandImage ( entityId, fileUrl );
                break;
            case CATEGORY:
                handleCategoryImage ( entityId, fileUrl );
                break;
            case JOBPOST:
                handleJobPostImage ( entityId, fileUrl );
                break;
            case OURSERVICES:
                handleOurServicesImage ( entityId, fileUrl );
                break;
            default:
                throw new IllegalArgumentException ( "Unknown entity type: " + entityType );
        }

        return response;
    }

    @Override
    @Transactional
    public void deleteEntityImage(String entityType, Long entityId) {
        switch (entityType.toLowerCase ( )) {
            case PRODUCT:
                deleteProductImage ( entityId );
                break;
            case BRAND:
                deleteBrandImage ( entityId );
                break;
            case CATEGORY:
                deleteCategoryImage ( entityId );
                break;
            case JOBPOST:
                deleteJobPostImage ( entityId );
                break;
            case OURSERVICES:
                deleteOurServicesImage ( entityId );
                break;
            default:
                throw new IllegalArgumentException ( "Unknown entity type: " + entityType );
        }
    }

    private void handleProductImage(Long productId, String fileUrl) {
        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new RuntimeException ( PRODUCT_NOT_FOUND_WITH_ID + productId ) );

        if ( product.getImageUrls ( ) == null ) {
            product.setImageUrls ( new ArrayList<> ( ) );
        }
        product.getImageUrls ( ).add ( fileUrl );
        productRepository.save ( product );
    }

    private void handleBrandImage(Long brandId, String fileUrl) {
        Brand brand = brandRepository.findById ( brandId )
                .orElseThrow ( () -> new RuntimeException ( BRAND_NOT_FOUND_WITH_ID + brandId ) );

        // Create a new Image entity
        Image image = new Image ( );
        image.setImageUrl ( fileUrl );
        image = imageRepository.save ( image );

        // Set the image for the brand
        brand.setImage ( image );
        brandRepository.save ( brand );
    }

    private void handleCategoryImage(Long categoryId, String fileUrl) {
        Category category = categoryRepository.findById ( categoryId )
                .orElseThrow ( () -> new RuntimeException ( CATEGORY_NOT_FOUND_WITH_ID + categoryId ) );

        if ( category.getImageUrls ( ) == null ) {
            category.setImageUrls ( new ArrayList<> ( ) );
        }
        category.getImageUrls ( ).add ( fileUrl );
        categoryRepository.save ( category );
    }

    private void handleJobPostImage(Long jobPostId, String fileUrl) {
        JobPost jobPost = jobPostRepository.findById ( jobPostId )
                .orElseThrow ( () -> new RuntimeException ( JOB_POST_NOT_FOUND_WITH_ID + jobPostId ) );

        jobPostRepository.save ( jobPost );
    }

    private void deleteProductImage(Long productId) {
        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new RuntimeException ( PRODUCT_NOT_FOUND_WITH_ID + productId ) );

        if ( product.getImageUrls ( ) != null && ! product.getImageUrls ( ).isEmpty ( ) ) {
            String imageUrl = product.getImageUrls ( ).get ( product.getImageUrls ( ).size ( ) - 1 );
            product.getImageUrls ( ).remove ( imageUrl );
            productRepository.save ( product );
            deleteImageFile ( imageUrl );
        }
    }

    private void deleteBrandImage(Long brandId) {
        Brand brand = brandRepository.findById ( brandId )
                .orElseThrow ( () -> new RuntimeException ( BRAND_NOT_FOUND_WITH_ID + brandId ) );

        // Store the image ID before clearing it
        Long imageId = null;
        if ( brand.getImage ( ) != null ) {
            imageId = brand.getImage ( ).getImageId ( );
        }

        // Clear the image reference
        brand.setImage ( null );
        brandRepository.save ( brand );

        // Delete the image if it exists and is not used elsewhere
        if ( imageId != null ) {
            int usageCount = imageRepository.countUsagesOfImage ( imageId );
            if ( usageCount == 0 ) {
                imageRepository.deleteById ( imageId );
            }
        }
    }

    private void deleteCategoryImage(Long categoryId) {
        Category category = categoryRepository.findById ( categoryId )
                .orElseThrow ( () -> new RuntimeException ( CATEGORY_NOT_FOUND_WITH_ID + categoryId ) );

        if ( category.getImageUrls ( ) != null && ! category.getImageUrls ( ).isEmpty ( ) ) {
            String imageUrl = category.getImageUrls ( ).get ( category.getImageUrls ( ).size ( ) - 1 );
            category.getImageUrls ( ).remove ( imageUrl );
            categoryRepository.save ( category );
            deleteImageFile ( imageUrl );
        }
    }

    private void deleteJobPostImage(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById ( jobPostId )
                .orElseThrow ( () -> new RuntimeException ( JOB_POST_NOT_FOUND_WITH_ID + jobPostId ) );

        if ( jobPost.getImage ( ).getImageUrl ( ) != null ) {
            String imageUrl = jobPost.getImage ( ).getImageUrl ( );

            jobPostRepository.save ( jobPost );
            deleteImageFile ( imageUrl );
        }
    }

    private void handleOurServicesImage(Long ourServicesId, String fileUrl) {
        OurService ourServices = ourServicesRepository.findById ( ourServicesId )
                .orElseThrow ( () -> new RuntimeException ( OUR_SERVICES_NOT_FOUND_WITH_ID + ourServicesId ) );


        ourServicesRepository.save ( ourServices );
    }

    private void deleteOurServicesImage(Long ourServicesId) {
        OurService ourServices = ourServicesRepository.findById ( ourServicesId )
                .orElseThrow ( () -> new RuntimeException ( OUR_SERVICES_NOT_FOUND_WITH_ID + ourServicesId ) );

        if ( ourServices.getImage ( ) != null ) {
            String imageUrl = ourServices.getImage ( ).getImageUrl ( );

            ourServicesRepository.save ( ourServices );
            deleteImageFile ( imageUrl );
        }
    }

    private void deleteImageFile(String imageUrl) {
        try {
            String filePath = imageUrl.substring ( imageUrl.lastIndexOf ( "/" ) + 1 );
            fileStorageService.deleteFile ( filePath );
        }
        catch ( IOException e ) {
            // Log error but don't throw - the database update was successful
            e.printStackTrace ( );
        }
    }
}
