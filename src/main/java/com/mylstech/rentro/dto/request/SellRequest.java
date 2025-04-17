package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Sell;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellRequest {
    private Double actualPrice;
    private Double discountPrice;


    public Sell requestToSell() {
        Sell sell = new Sell();
        sell.setActualPrice(actualPrice);
        sell.setDiscountPrice(discountPrice);
        return sell;
    }
}
