package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Order;
import com.mylstech.rentro.util.ORDER_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Find orders by user ID
     * @param userId user ID
     * @return list of orders for the given user
     */
    List<Order> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find orders by status
     * @param status order status
     * @return list of orders with the given status
     */
    List<Order> findByStatusOrderByCreatedAtDesc(ORDER_STATUS status);
    
    /**
     * Find orders by user ID and status
     * @param userId user ID
     * @param status order status
     * @return list of orders for the given user with the given status
     */
    List<Order> findByUserUserIdAndStatusOrderByCreatedAtDesc(Long userId, ORDER_STATUS status);
    
    /**
     * Find order by order number
     * @param orderNumber order number
     * @return order with the given order number
     */
    Order findByOrderNumber(String orderNumber);
}