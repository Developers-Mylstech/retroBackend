package com.mylstech.rentro.dto.request.cart;

import com.mylstech.rentro.model.CartItem;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.util.ProductType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Product type is required")
    private ProductType productType;
    @Min(value = 1, message = "Rent period must be at least 1")
    private Integer rentPeriod;
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer sellQuantity;
    
    /**
     * Validates that the appropriate fields are provided based on product type
     * @return true if validation passes, false otherwise
     */
    public boolean isValid() {
        if (productType == ProductType.RENT) {
            return rentPeriod != null && rentPeriod > 0;
        } else if (productType == ProductType.SELL) {
            return sellQuantity != null && sellQuantity > 0;
        }
        return false;
    }
    
    /**
     * Converts request to CartItem entity
     * @param product the product to add to cart
     * @return CartItem entity
     */
    public CartItem toCartItem(Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setProductType(productType);
        
        // Set quantity based on product type
        if (productType == ProductType.SELL) {
            cartItem.setSellQuantity (sellQuantity);
            // Reset rent period for sell items
            cartItem.setRentPeriod(null);
        } else if (productType == ProductType.RENT) {
            cartItem.setSellQuantity (1); // For rent, quantity is always 1
            cartItem.setRentPeriod(rentPeriod);
        }
        return cartItem;
    }
}
