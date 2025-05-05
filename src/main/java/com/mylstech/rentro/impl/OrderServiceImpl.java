package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.response.OrderResponse;
import com.mylstech.rentro.model.*;
import com.mylstech.rentro.repository.OrderItemRepository;
import com.mylstech.rentro.repository.OrderRepository;
import com.mylstech.rentro.service.OrderService;
import com.mylstech.rentro.util.ORDER_STATUS;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    @Override
    @Transactional
    public OrderResponse createOrderFromCheckout(CheckOut checkout) {
        logger.debug("Creating order from checkout with ID: {}", checkout.getCheckoutId());
        
        // Create the order
        Order order = new Order();
        order.setCheckout(checkout);
        
        // Get cart information
        Cart cart = checkout.getCart();
        if (cart == null) {
            throw new RuntimeException("Checkout has no associated cart");
        }
        
        order.setUser(cart.getUser());
        order.setDeliveryAddress(checkout.getDeliveryAddress());
        order.setDeliveryDate(checkout.getDeliveryDate().atStartOfDay ());
        order.setPaymentMethod(checkout.getPaymentOption().toString());
        
        // Generate order number
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        
        // Create order items from cart items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setProductName(cartItem.getProduct().getName());
                    
                    // Set product image if available
                    if (cartItem.getProduct().getImageUrls() != null && !cartItem.getProduct().getImageUrls().isEmpty()) {
                        orderItem.setProductImage(cartItem.getProduct().getImageUrls().get(0));
                    }
                    
                    orderItem.setProductType(cartItem.getProductType());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice() / cartItem.getQuantity()); // Unit price
                    orderItem.setTotalPrice(cartItem.getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());
        
        order.setItems(orderItems);
        
        // Calculate total amount
        order.calculateTotalAmount();
        
        // Save the order
        Order savedOrder = orderRepository.save(order);
        
        logger.debug("Created order with ID: {} and order number: {}", 
                savedOrder.getOrderId(), savedOrder.getOrderNumber());
        
        return new OrderResponse(savedOrder);
    }
    
    @Override
    public List<OrderResponse> getAllOrders() {
        logger.debug("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(OrderResponse::new)
                .toList();
    }
    
    @Override
    public OrderResponse getOrderById(Long id) {
        logger.debug("Fetching order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return new OrderResponse(order);
    }
    
    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        logger.debug("Fetching orders for user with ID: {}", userId);
        return orderRepository.findByUserUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderResponse::new)
                .toList();
    }
    
    @Override
    public List<OrderResponse> getOrdersByStatus(ORDER_STATUS status) {
        logger.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(OrderResponse::new)
                .toList();
    }
    
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, ORDER_STATUS status) {
        logger.debug("Updating status of order with ID: {} to {}", id, status);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setStatus(status);
        
        Order updatedOrder = orderRepository.save(order);
        
        logger.debug("Updated status of order with ID: {} to {}", id, status);
        
        return new OrderResponse(updatedOrder);
    }
    
    @Override
    @Transactional
    public OrderResponse markOrderAsPaid(Long id, String paymentId) {
        logger.debug("Marking order with ID: {} as paid with payment ID: {}", id, paymentId);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setIsPaid(true);
        order.setPaidAt(LocalDateTime.now());
        order.setPaymentId(paymentId);
        order.setStatus(ORDER_STATUS.PAYMENT_CONFIRMED);
        
        Order updatedOrder = orderRepository.save(order);
        
        logger.debug("Marked order with ID: {} as paid", id);
        
        return new OrderResponse(updatedOrder);
    }
}