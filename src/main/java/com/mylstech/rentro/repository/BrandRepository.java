package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByImageImageId(Long imageId);
    
    @Modifying
    @Query(value = "UPDATE brands SET image_id = NULL WHERE brand_id = :brandId", nativeQuery = true)
    void clearBrandImage(@Param("brandId") Long brandId);
    
    @Query("SELECT b.name FROM Brand b ORDER BY b.name")
    List<String> findAllBrandNames();
    
    boolean existsByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCaseAndBrandIdNot(String name, Long brandId);
}
