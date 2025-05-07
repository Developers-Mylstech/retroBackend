package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Cart;
import com.mylstech.rentro.model.CheckOut;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequest {
    @NotNull(message = "Cart ID is required")
    private Long cartId;

    @NotBlank(message = "Name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Address ID is required to select an existing address
    @NotNull(message = "Address ID is required")
    private Long addressId;




    /**
     * Converts request to CheckOut entity
     *
     * @param cart the cart to associate with this checkout
     * @return CheckOut entity
     */
    public CheckOut toCheckOut(Cart cart) {
        CheckOut checkOut = new CheckOut ( );
        checkOut.setCart ( cart );
        checkOut.setFirstName ( firstName );
        checkOut.setLastName ( lastName );
        checkOut.setMobile ( mobile );
        checkOut.setEmail ( email );




        return checkOut;
    }
}