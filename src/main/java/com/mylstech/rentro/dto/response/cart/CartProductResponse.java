package com.mylstech.rentro.dto.response.cart;

import com.mylstech.rentro.dto.response.RentResponse;
import com.mylstech.rentro.dto.response.SellResponse;
import com.mylstech.rentro.model.CartItem;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.util.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartProductResponse {

    private RentResponse rent;
    private int rentPeriod;
    private SellResponse sell;
    private int quantity;

    public CartProductResponse( CartItem cartItem) {
        if(cartItem.getProductType ()==ProductType.RENT){
        this.rent = new RentResponse ( cartItem.getProduct().getProductFor ().getRent() );
        this.rentPeriod = cartItem.getRentPeriod ( );
        } else if ( cartItem.getProductType ()==ProductType.SELL ) {
        this.sell = new SellResponse ( cartItem.getProduct().getProductFor ().getSell() );
        this.quantity = cartItem.getSellQuantity ();
        }
    }
}
