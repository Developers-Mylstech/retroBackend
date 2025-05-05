package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Cart;
import com.mylstech.rentro.model.CheckOut;
import com.mylstech.rentro.util.CHECKOUT_STATUS;
import com.mylstech.rentro.util.PAYMENT_OPTION;
import com.mylstech.rentro.validation.FutureDateTime;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequest {
    @NotNull(message = "Cart ID is required")
    private Long cartId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Address ID is required to select an existing address
    @NotNull(message = "Address ID is required")
    private Long addressId;



    @NotNull(message = "Payment option is required")
    private PAYMENT_OPTION paymentOption;
    
    private CHECKOUT_STATUS status;

    /**
     * Converts request to CheckOut entity
     * @param cart the cart to associate with this checkout
     * @return CheckOut entity
     */
    public CheckOut toCheckOut(Cart cart) {
        CheckOut checkOut = new CheckOut();
        checkOut.setCart(cart);
        checkOut.setName(name);
        checkOut.setMobile(mobile);
        checkOut.setEmail(email);

        checkOut.setPaymentOption(paymentOption);
        
        if (status != null) {
            checkOut.setStatus(status);
        } else {
            checkOut.setStatus(CHECKOUT_STATUS.PENDING);
        }
        
        return checkOut;
    }
}