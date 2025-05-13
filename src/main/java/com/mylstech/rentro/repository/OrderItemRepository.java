package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /**
     * Find order items by order ID
     * @param orderId order ID
     * @return list of order items for the given order
     */
    List<OrderItem> findByOrderOrderId(Long orderId);
}
