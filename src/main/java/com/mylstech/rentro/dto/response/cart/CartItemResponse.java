package com.mylstech.rentro.dto.response.cart;

import com.mylstech.rentro.model.CartItem;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.util.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productDescription;
    private ProductType productType;
    private CartProductResponse productDetail;
    private List<String> productImages;
    private Double price;

    public CartItemResponse(CartItem cartItem) {
        this.cartItemId = cartItem.getCartItemId ();
        this.productName = cartItem.getProduct ().getName ();
        this.productImages = cartItem.getProduct ().getImages ().stream ().map ( Image::getImageUrl ).toList ();
        this.productDescription = cartItem.getProduct ().getDescription ();
        this.productId = cartItem.getProduct ().getProductId ();
        this.productType = cartItem.getProductType ();
        this.productDetail = new CartProductResponse ( cartItem );
        this.price = cartItem.getPrice ();
    }
}
