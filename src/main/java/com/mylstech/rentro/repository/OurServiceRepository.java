package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.OurService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OurServiceRepository extends JpaRepository<OurService, Long> {
}
