package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Address;
import com.mylstech.rentro.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(AppUser user);
    List<Address> findByUserOrderByIsDefaultDesc(AppUser user);
    Optional<Address> findByUserAndIsDefaultTrue(AppUser user);
    long countByUser(AppUser user);
}