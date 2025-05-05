package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.response.OrderResponse;
import com.mylstech.rentro.model.CheckOut;
import com.mylstech.rentro.util.ORDER_STATUS;

import java.util.List;

public interface OrderService {
    /**
     * Create an order from a checkout
     * @param checkout the checkout to create an order from
     * @return the created order response
     */
    OrderResponse createOrderFromCheckout(CheckOut checkout);
    
    /**
     * Get all orders
     * @return list of all orders
     */
    List<OrderResponse> getAllOrders();
    
    /**
     * Get order by ID
     * @param id order ID
     * @return order response
     */
    OrderResponse getOrderById(Long id);
    
    /**
     * Get orders by user ID
     * @param userId user ID
     * @return list of orders for the given user
     */
    List<OrderResponse> getOrdersByUserId(Long userId);
    
    /**
     * Get orders by status
     * @param status order status
     * @return list of orders with the given status
     */
    List<OrderResponse> getOrdersByStatus(ORDER_STATUS status);
    
    /**
     * Update order status
     * @param id order ID
     * @param status new status
     * @return updated order response
     */
    OrderResponse updateOrderStatus(Long id, ORDER_STATUS status);
    
    /**
     * Mark order as paid
     * @param id order ID
     * @param paymentId payment ID
     * @return updated order response
     */
    OrderResponse markOrderAsPaid(Long id, String paymentId);
}