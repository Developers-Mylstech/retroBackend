package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Wishlist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
    private Long wishlistId;
    private Long userId;
    private String userName;
    private List<ProductResponse> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WishlistResponse(Wishlist wishlist) {
        this.wishlistId = wishlist.getWishlistId ( );

        if ( wishlist.getUser ( ) != null ) {
            this.userId = wishlist.getUser ( ).getUserId ( );
            this.userName = wishlist.getUser ( ).getName ( );
        }

        if ( wishlist.getProducts ( ) != null ) {
            this.products = wishlist.getProducts ( ).stream ( )
                    .map ( ProductResponse::new )
                    .toList ( );
        } else {
            this.products = new ArrayList<> ( );
        }

        this.createdAt = wishlist.getCreatedAt ( );
        this.updatedAt = wishlist.getUpdatedAt ( );
    }
}