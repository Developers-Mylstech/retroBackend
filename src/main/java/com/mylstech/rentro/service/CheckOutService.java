package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.CheckOutRequest;
import com.mylstech.rentro.dto.response.CheckOutResponse;
import com.mylstech.rentro.util.CHECKOUT_STATUS;

import java.util.List;

public interface CheckOutService {
    /**
     * Get all checkouts
     * @return list of all checkouts
     */
    List<CheckOutResponse> getAllCheckOuts();
    
    /**
     * Get checkout by ID
     * @param id checkout ID
     * @return checkout with the given ID
     */
    CheckOutResponse getCheckOutById(Long id);
    
    /**
     * Create a new checkout
     * @param request checkout details
     * @return created checkout
     */
    CheckOutResponse createCheckOut(CheckOutRequest request);
    
    /**
     * Update an existing checkout
     * @param id checkout ID
     * @param request updated checkout details
     * @return updated checkout
     */
    CheckOutResponse updateCheckOut(Long id, CheckOutRequest request);
    
    /**
     * Delete a checkout
     * @param id checkout ID
     */
    void deleteCheckOut(Long id);
    
    /**
     * Get checkouts by user ID
     * @param userId user ID
     * @return list of checkouts for the given user
     */
    List<CheckOutResponse> getCheckOutsByUserId(Long userId);
    
    /**
     * Get checkouts by status
     * @param status checkout status
     * @return list of checkouts with the given status
     */
    List<CheckOutResponse> getCheckOutsByStatus(CHECKOUT_STATUS status);
    
    /**
     * Update checkout status
     * @param id checkout ID
     * @param status new status
     * @return updated checkout
     */
    CheckOutResponse updateCheckOutStatus(Long id, CHECKOUT_STATUS status);
    
    /**
     * Place an order from a checkout
     * @param checkoutId checkout ID
     * @return checkout response
     */
    CheckOutResponse placeOrder(Long checkoutId);
}
