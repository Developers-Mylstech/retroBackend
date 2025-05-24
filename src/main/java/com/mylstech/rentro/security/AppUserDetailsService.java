package com.mylstech.rentro.security;

import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new AppUserSecurityDetails(user);
    }
}
/*
* AboutUs
* Address
* AppUser
* Banner
* Brand
* Cart
* CartItem
* Category
* Checkout
* Client
* Feature
* Image
* Inventory
* JobApplicant
* JobPost
* Location
* Order
* OrderItem
* OurService
* Product
* ProductFor
* RefreshToken
* Rent
* RequestQuotation
* Sell
* Service
* ServiceField
* Specification
* SpecificationField
* Wishlist*/