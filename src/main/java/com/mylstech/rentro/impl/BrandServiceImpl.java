package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.BrandRequest;
import com.mylstech.rentro.dto.response.BrandResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.exception.UniqueConstraintViolationException;
import com.mylstech.rentro.model.Brand;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.repository.BrandRepository;
import com.mylstech.rentro.repository.ImageRepository;
import com.mylstech.rentro.repository.ProductRepository;
import com.mylstech.rentro.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final Logger logger = LoggerFactory.getLogger(BrandServiceImpl.class);

    @Override
    @Cacheable(value = "brands", key = "'allBrands'")
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(BrandResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "brands", key = "#id")
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        return new BrandResponse(brand);
    }
    
    @Override
    @Cacheable(value = "brandNames")
    public List<String> getAllBrandNames() {
        logger.debug("Fetching all brand names");
        return brandRepository.findAllBrandNames();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"brands", "brandNames"}, allEntries = true)
    public BrandResponse createBrand(BrandRequest request) {
        try {
            logger.info("Creating new brand with name: {}", request.getName());
            
            // Check if brand name already exists
            if (brandRepository.existsByNameIgnoreCase(request.getName())) {
                throw new UniqueConstraintViolationException ("A brand with name '" + request.getName() + "' already exists");
            }
            
            Brand brand = request.requestToBrand();
            
            // Handle image if imageId is provided
            if (request.getImageId() != null) {
                Image image = imageRepository.findById(request.getImageId())
                        .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));
                brand.setImage(image);
            }
            
            Brand savedBrand = brandRepository.save(brand);
            logger.info("Successfully created brand with id: {}", savedBrand.getBrandId());
            
            return new BrandResponse(savedBrand);
        } catch (UniqueConstraintViolationException e) {
            logger.error("Brand name already exists: {}", request.getName());
            throw e;
        } catch (Exception e) {
            logger.error("Error creating brand: {}", request.getName(), e);
            throw new RuntimeException("Failed to create brand", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"brands", "brandNames"}, allEntries = true)
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        try {
            logger.info("Updating brand with id: {}", id);
            Brand brand = brandRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
            
            if (request.getName() != null && !request.getName().equals(brand.getName())) {
                // Check if another brand with the same name already exists
                if (brandRepository.existsByNameIgnoreCaseAndBrandIdNot(request.getName(), id)) {
                    throw new UniqueConstraintViolationException("A brand with name '" + request.getName() + "' already exists");
                }
                brand.setName(request.getName());
            }
            
            // Handle image if imageId is provided
            if (request.getImageId() != null) {
                Image image = imageRepository.findById(request.getImageId())
                        .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));
                brand.setImage(image);
            } 
            // For backward compatibility, handle imageUrls if provided
//            else if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
//                String imageUrl = request.getImageUrls().get(0);
//
//                // Try to find or create an Image entity for this URL
//                Image image = imageRepository.findByImageUrl(imageUrl)
//                        .orElseGet(() -> {
//                            Image newImage = new Image();
//                            newImage.setImageUrl(imageUrl);
//                            return imageRepository.save(newImage);
//                        });
//                brand.setImage(image);
//            }
            
            Brand updatedBrand = brandRepository.save(brand);
            logger.info("Successfully updated brand with id: {}", id);
            
            return new BrandResponse(updatedBrand);
        } catch (ResourceNotFoundException e) {
            logger.error("Brand not found with id: {}", id);
            throw e;
        } catch (UniqueConstraintViolationException e) {
            logger.error("Brand name already exists: {}", request.getName());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating brand with id: {}", id, e);
            throw new RuntimeException("Failed to update brand", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"brands", "brandNames"}, allEntries = true)
    public void deleteBrand(Long id) {

        try {
            Brand brand = brandRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
            // Check if any products are associated with this brand using ProductRepository
            List<Product> associatedProducts = productRepository.findByBrandBrandId(id);
            if (associatedProducts != null && !associatedProducts.isEmpty()) {

                throw new RuntimeException("Cannot delete brand with associated products. Found " + 
                                          associatedProducts.size() + " products using this brand.");
            }
            
            // Clear images (will be deleted due to orphanRemoval=true)
//            if (brand.getImages() != null) {
//                brand.getImages().clear();
//            }
//
//            // Clear deprecated imageUrls
//            if (brand.getImageUrls() != null) {
//                brand.getImageUrls().clear();
//            }
            
            // Save the brand with cleared collections first
            brandRepository.save(brand);
            
            // Now delete the brand
            brandRepository.delete(brand);
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public BrandResponse setBrandImage(Long brandId, Long imageId) {
        logger.debug("Setting image {} for brand {}", imageId, brandId);
        
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", brandId));
        
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        
        logger.debug("Found brand: {}, image: {}", brand.getBrandId(), image.getImageId());
        
        // Check if this image is already used by another brand
        List<Brand> brandsWithImage = brandRepository.findByImageImageId(imageId);
        for (Brand existingBrand : brandsWithImage) {
            if (!existingBrand.getBrandId().equals(brandId)) {
                logger.debug("Image {} is already used by brand {}. Creating a copy.", 
                        imageId, existingBrand.getBrandId());
                
                // Create a copy of the image
                Image imageCopy = new Image();
                imageCopy.setImageUrl(image.getImageUrl());
                image = imageRepository.save(imageCopy);
                logger.debug("Created image copy with ID: {}", image.getImageId());
                break;
            }
        }
        
        // Set the image for the brand
        brand.setImage(image);
        
        // Save the brand
        Brand savedBrand = brandRepository.save(brand);
        logger.debug("Set image and saved brand");
        
        return new BrandResponse(savedBrand);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public BrandResponse removeBrandImage(Long brandId) {
        logger.debug("Removing image from brand {}", brandId);
        
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", brandId));
        
        // Log the current state
        if (brand.getImage() != null) {
            logger.debug("Current image ID: {}, URL: {}", 
                    brand.getImage().getImageId(), 
                    brand.getImage().getImageUrl());
        } else {
            logger.debug("No image currently associated with brand");
        }
        
        try {
            // First approach: Use JPA entity
            brand.setImage(null);
            
            // Save and flush to ensure changes are committed immediately
            Brand savedBrand = brandRepository.saveAndFlush(brand);
            
            // Verify the image was cleared
            if (savedBrand.getImage() == null) {
                logger.debug("Successfully cleared image from brand using JPA");
            } else {
                logger.warn("Failed to clear image using JPA, trying native query...");
                
                // Second approach: Use native query as fallback
                brandRepository.clearBrandImage(brandId);
                
                // Refresh the entity from the database
                brandRepository.flush();
                savedBrand = brandRepository.findById(brandId)
                        .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", brandId));
                
                if (savedBrand.getImage() == null) {
                    logger.debug("Successfully cleared image using native query");
                } else {
                    logger.error("Failed to clear image even with native query!");
                }
            }
            
            return new BrandResponse(savedBrand);
        } catch (Exception e) {
            logger.error("Error while removing image from brand", e);
            throw e;
        }
    }
}
