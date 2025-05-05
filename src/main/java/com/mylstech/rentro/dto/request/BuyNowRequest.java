package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.util.PAYMENT_OPTION;
import com.mylstech.rentro.util.ProductType;
import com.mylstech.rentro.validation.FutureDateTime;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyNowRequest {
    // Product details
    @NotNull(message = "Product type is required")
    private ProductType productType;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;
    
    @Min(value = 1, message = "Rent period must be at least 1")
    private Integer rentPeriod;
    
    // Customer details
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Mobile number is required")
    private String mobile;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    // Address details - either addressId or full address
    private Long addressId;
    
    // Or allow inline address creation
    private AddressRequest address;
    
    // Delivery details
    @NotNull(message = "Delivery date is required")
    @FutureDateTime(message = "Delivery date must be at least 1 hour in the future", minHours = 1)
    private LocalDateTime deliveryDate;
    
    @NotNull(message = "Payment option is required")
    private PAYMENT_OPTION paymentOption;
    
    /**
     * Validates that the appropriate fields are provided based on product type
     */
    public boolean isValid() {
        if (productType == ProductType.RENT) {
            return rentPeriod != null && rentPeriod > 0 && quantity != null && quantity > 0;
        } else if (productType == ProductType.SELL) {
            return quantity != null && quantity > 0;
        }
        return false;
    }
}