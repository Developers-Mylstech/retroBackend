package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.OurService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface OurServiceRepository extends JpaRepository<OurService, Long> {
    List<OurService> findByImageImageId(Long imageId);
    
    @Modifying
    @Query(value = "UPDATE our_services SET image_id = NULL, image_url = NULL WHERE our_service_id = :ourServiceId", nativeQuery = true)
    void clearOurServiceImage(@Param("ourServiceId") Long ourServiceId);
}
