package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.BrandRequest;
import com.mylstech.rentro.dto.response.BrandResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Brand;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.repository.BrandRepository;
import com.mylstech.rentro.repository.ProductRepository;
import com.mylstech.rentro.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final  ProductRepository productRepository;

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll ( ).stream ( ).map ( BrandResponse::new ).toList ( );
    }

    @Override
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException ("Brand", "id", id));
        return new BrandResponse(brand);
    }

    @Override
    public BrandResponse createBrand(BrandRequest request) {
        return new BrandResponse ( brandRepository.save ( request.requestToBrand ( ) ) );
    }

    @Override
    public BrandResponse updateBrand(Long id, BrandRequest brandDetails) {
        Brand brand = brandRepository.findById ( id ).orElseThrow ( () -> new RuntimeException ( "Brand not found with id:" + id ) );
        if (brand.getName ( ) != null ) {
            brand.setName ( brandDetails.getName ( ) );
        }
        if ( brand.getImages () != null ) {
            brand.getImages ().clear ();
            brand.setImages ( brandDetails.getImageUrls () );
        }

        return new BrandResponse ( brandRepository.save ( brand ));
    }

    @Override
    @Transactional
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
            if (brand.getImages() != null) {
                brand.getImages().clear();
            }
            
            // Clear deprecated imageUrls
            if (brand.getImageUrls() != null) {
                brand.getImageUrls().clear();
            }
            
            // Save the brand with cleared collections first
            brandRepository.save(brand);
            
            // Now delete the brand
            brandRepository.delete(brand);
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }
}
