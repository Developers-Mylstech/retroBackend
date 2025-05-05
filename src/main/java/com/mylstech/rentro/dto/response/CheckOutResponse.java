package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.dto.response.cart.CartResponse;
import com.mylstech.rentro.model.CheckOut;
import com.mylstech.rentro.util.CHECKOUT_STATUS;
import com.mylstech.rentro.util.PAYMENT_OPTION;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponse {
    private Long checkoutId;
    private CartResponse cart;
    private String name;
    private String mobile;
    private String email;
    private String homeAddress;
    private AddressResponse deliveryAddress;
    private LocalDateTime deliveryDate;
    private PAYMENT_OPTION paymentOption;
    private CHECKOUT_STATUS status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public CheckOutResponse(CheckOut checkOut) {
        this.checkoutId = checkOut.getCheckoutId();
        if (checkOut.getCart() != null) {
            this.cart = new CartResponse(checkOut.getCart());
        }
        this.name = checkOut.getName();
        this.mobile = checkOut.getMobile();
        this.email = checkOut.getEmail();
        this.homeAddress = checkOut.getHomeAddress();
        if (checkOut.getDeliveryAddress() != null) {
            this.deliveryAddress = new AddressResponse(checkOut.getDeliveryAddress());
        }
        this.deliveryDate = checkOut.getDeliveryDate().atStartOfDay ();
        this.paymentOption = checkOut.getPaymentOption();
        this.status = checkOut.getStatus();
        this.createdAt = checkOut.getCreatedAt();
        this.updatedAt = checkOut.getUpdatedAt();
    }
}