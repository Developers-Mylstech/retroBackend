package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.dto.response.cart.CartResponse;
import com.mylstech.rentro.model.CheckOut;
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
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private String homeAddress;
    private AddressResponse deliveryAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long orderId;

    public CheckOutResponse(CheckOut checkOut) {
        this.checkoutId = checkOut.getCheckoutId ( );
        if ( checkOut.getCart ( ) != null ) {
            this.cart = new CartResponse ( checkOut.getCart ( ) );
        }
        this.firstName = checkOut.getFirstName ( );
        this.lastName = checkOut.getLastName ( );
        this.mobile = checkOut.getMobile ( );
        this.email = checkOut.getEmail ( );
        this.homeAddress = checkOut.getHomeAddress ( );
        if ( checkOut.getDeliveryAddress ( ) != null ) {
            this.deliveryAddress = new AddressResponse ( checkOut.getDeliveryAddress ( ) );
        }

        this.createdAt = checkOut.getCreatedAt ( );
        this.updatedAt = checkOut.getUpdatedAt ( );
    }
}