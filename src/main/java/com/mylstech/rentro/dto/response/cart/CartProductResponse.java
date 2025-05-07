package com.mylstech.rentro.dto.response.cart;

import com.mylstech.rentro.dto.response.RentResponse;
import com.mylstech.rentro.dto.response.SellResponse;
import com.mylstech.rentro.dto.response.ServiceFieldResponse;
import com.mylstech.rentro.model.CartItem;
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
    private SellResponse sell;
    private int quantity;
    private ServiceFieldResponse mmc;
    private ServiceFieldResponse ots;
    private ServiceFieldResponse amcBasic;
    private ServiceFieldResponse amcGold;



    public CartProductResponse(CartItem cartItem) {
        if ( cartItem.getProductType ( ) == ProductType.RENT ) {
            this.rent = new RentResponse ( cartItem.getProduct ( ).getProductFor ( ).getRent ( ) );
            this.quantity = cartItem.getQuantity ( );
        } else if ( cartItem.getProductType ( ) == ProductType.SELL ) {
            this.sell = new SellResponse ( cartItem.getProduct ( ).getProductFor ( ).getSell ( ) );
            this.quantity = cartItem.getQuantity ( );
        }
        else if ( cartItem.getProductType ()==ProductType.OTS ){
            this.ots=new ServiceFieldResponse ( cartItem.getProduct ().getProductFor ().getServices ().getOts () );
        }else if ( cartItem.getProductType ()==ProductType.MMC ){
            this.mmc=new ServiceFieldResponse ( cartItem.getProduct ().getProductFor ().getServices ().getMmc () );
        }else if ( cartItem.getProductType ()==ProductType.AMC_BASIC ){
            this.amcBasic=new ServiceFieldResponse ( cartItem.getProduct ().getProductFor ().getServices ().getAmcBasic () );
        }else if ( cartItem.getProductType ()==ProductType.AMC_GOLD ){
            this.amcGold=new ServiceFieldResponse ( cartItem.getProduct ().getProductFor ().getServices ().getAmcGold () );
        }
    }
}
