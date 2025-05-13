package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageUrl(String imageUrl);

    @Query("SELECT i FROM Image i WHERE i.products IS EMPTY")
    List<Image> findImagesWithNoProducts();

    @Query(value = "SELECT COUNT(*) FROM (" +
            "SELECT 1 FROM banners WHERE image_id = :imageId UNION ALL " +
            "SELECT 1 FROM clients WHERE image_id = :imageId UNION ALL " +
            "SELECT 1 FROM about_us WHERE image_id = :imageId UNION ALL " +
            "SELECT 1 FROM product_images WHERE image_id = :imageId UNION ALL " +
            "SELECT 1 FROM request_quotations WHERE image_id = :imageId" +
            ") AS usage_count", nativeQuery = true)
    int countUsagesOfImage(@Param("imageId") Long imageId);
}
