package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Order;
import com.mylstech.rentro.util.ORDER_STATUS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String userName;
    private List<OrderItemResponse> items;
    private Double totalAmount;
    private ORDER_STATUS status;
    private AddressResponse deliveryAddress;
    private LocalDateTime deliveryDate;
    private String paymentId;
    private String paymentMethod;
    private Boolean isPaid;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public OrderResponse(Order order) {
        this.orderId = order.getOrderId();
        this.orderNumber = order.getOrderNumber();
        
        if (order.getUser() != null) {
            this.userId = order.getUser().getUserId();
            this.userName = order.getUser().getName();
        }
        
        this.items = order.getItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
                
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        
        if (order.getDeliveryAddress() != null) {
            this.deliveryAddress = new AddressResponse(order.getDeliveryAddress());
        }
        
        this.deliveryDate = order.getDeliveryDate();
        this.paymentId = order.getPaymentId();
        this.paymentMethod = order.getPaymentMethod();
        this.isPaid = order.getIsPaid();
        this.paidAt = order.getPaidAt();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}