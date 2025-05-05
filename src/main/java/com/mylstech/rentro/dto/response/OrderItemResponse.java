package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.OrderItem;
import com.mylstech.rentro.util.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private String productImage;
    private ProductType productType;
    private Integer quantity;
    private Integer rentPeriod;
    private Double price;
    private Double totalPrice;
    
    public OrderItemResponse(OrderItem orderItem) {
        this.orderItemId = orderItem.getOrderItemId();
        
        if (orderItem.getProduct() != null) {
            this.productId = orderItem.getProduct().getProductId();
        }
        
        this.productName = orderItem.getProductName();
        this.productImage = orderItem.getProductImage();
        this.productType = orderItem.getProductType();
        this.quantity = orderItem.getQuantity();
        this.rentPeriod = orderItem.getRentPeriod();
        this.price = orderItem.getPrice();
        this.totalPrice = orderItem.getTotalPrice();
    }
}