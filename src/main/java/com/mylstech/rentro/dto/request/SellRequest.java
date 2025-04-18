package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Sell;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellRequest {
    private Double actualPrice;
    private Double discountPrice;
    private List<String> benefits;

    public Sell requestToSell() {
        Sell sell = new Sell();
        sell.setActualPrice(actualPrice);
        sell.setDiscountPrice(discountPrice);
        sell.setBenefits ( benefits );
        return sell;
    }
}
