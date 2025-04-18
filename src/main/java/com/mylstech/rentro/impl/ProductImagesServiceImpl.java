package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.ProductImagesRequest;
import com.mylstech.rentro.dto.response.ProductImagesResponse;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.model.ProductImages;
import com.mylstech.rentro.repository.ProductImagesRepository;
import com.mylstech.rentro.repository.ProductRepository;
import com.mylstech.rentro.service.ProductImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImagesServiceImpl implements ProductImagesService {

    private final ProductImagesRepository productImagesRepository;
    private final ProductRepository productRepository;

    @Override
    public List<ProductImagesResponse> getAllProductImages() {
        return productImagesRepository.findAll().stream()
                .map(ProductImagesResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public ProductImagesResponse getProductImagesById(Long id) {
        ProductImages productImages = productImagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductImages not found with id: " + id));
        return new ProductImagesResponse(productImages);
    }

    @Override
    public ProductImagesResponse createProductImages(ProductImagesRequest request) {
        ProductImages productImages = new ProductImages();
        
        // Set product if productId is provided
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));
            productImages.setProduct(product);
        }
        
        // Set image URLs
        productImages.setImageUrls(request.getImageUrls() != null ? request.getImageUrls() : new ArrayList<>());
        
        return new ProductImagesResponse(productImagesRepository.save(productImages));
    }

    @Override
    public ProductImagesResponse updateProductImages(Long id, ProductImagesRequest request) {
        ProductImages productImages = productImagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductImages not found with id: " + id));
        
        // Update product if productId is provided
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));
            productImages.setProduct(product);
        }
        
        // Update image URLs if provided
        if (request.getImageUrls() != null) {
            productImages.setImageUrls(request.getImageUrls());
        }
        
        return new ProductImagesResponse(productImagesRepository.save(productImages));
    }

    @Override
    public void deleteProductImages(Long id) {
        ProductImages productImages = productImagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductImages not found with id: " + id));
        productImagesRepository.delete(productImages);
    }

    @Override
    public ProductImagesResponse addImageUrl(Long id, String imageUrl) {
        ProductImages productImages = productImagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductImages not found with id: " + id));
        
        List<String> imageUrls = productImages.getImageUrls();
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }
        
        // Add the new image URL if it doesn't already exist
        if (!imageUrls.contains(imageUrl)) {
            imageUrls.add(imageUrl);
            productImages.setImageUrls(imageUrls);
            productImagesRepository.save(productImages);
        }
        
        return new ProductImagesResponse(productImages);
    }

    @Override
    public ProductImagesResponse removeImageUrl(Long id, String imageUrl) {
        ProductImages productImages = productImagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductImages not found with id: " + id));
        
        List<String> imageUrls = productImages.getImageUrls();
        if (imageUrls != null) {
            // Remove the image URL if it exists
            imageUrls.remove(imageUrl);
            productImages.setImageUrls(imageUrls);
            productImagesRepository.save(productImages);
        }
        
        return new ProductImagesResponse(productImages);
    }
}
