package com.mylstech.rentro.util;

/**
 * Enum representing the possible statuses of a checkout
 */
public enum CHECKOUT_STATUS {
    /**
     * Initial status when checkout is created
     */
    PENDING,
    
    /**
     * Payment has been confirmed
     */
    PAYMENT_CONFIRMED,
    
    /**
     * Order is being processed
     */
    PROCESSING,
    
    /**
     * Order is ready for delivery
     */
    READY_FOR_DELIVERY,
    
    /**
     * Order is out for delivery
     */
    OUT_FOR_DELIVERY,
    
    /**
     * Order has been delivered successfully
     */
    DELIVERED,
    
    /**
     * Order has been cancelled
     */
    CANCELLED,
    
    /**
     * Payment failed
     */
    PAYMENT_FAILED,
    
    /**
     * Order has been returned
     */
    RETURNED
}