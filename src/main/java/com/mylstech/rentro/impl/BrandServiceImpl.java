package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.BrandRequest;
import com.mylstech.rentro.dto.response.BrandResponse;
import com.mylstech.rentro.model.Brand;
import com.mylstech.rentro.repository.BrandRepository;
import com.mylstech.rentro.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll ( ).stream ( ).map ( BrandResponse::new ).toList ( );
    }

    @Override
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( "Brand not found with id: " + id ) );
        return new BrandResponse ( brand );
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
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById ( id ).orElseThrow ( () -> new RuntimeException ( "Brand not found with id:" + id ) );
        brandRepository.delete ( brand );
    }
}
