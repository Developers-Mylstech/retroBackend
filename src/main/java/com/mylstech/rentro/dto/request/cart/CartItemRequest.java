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
    
//    @Min(value = 1, message = "Rent period must be at least 1")
//    private Integer rentPeriod;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    

    /**
     * Converts request to CartItem entity with proper price calculation
     * @param product the product to add to cart
     * @return CartItem entity
     */
    public CartItem toCartItem(Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setProductType(productType);
        
        // Set quantity for both SELL and RENT
        cartItem.setQuantity(quantity);
        

        // Calculate price

        
        return cartItem;
    }
}
