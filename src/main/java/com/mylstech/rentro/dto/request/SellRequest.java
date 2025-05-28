package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Sell;
import com.mylstech.rentro.util.UNIT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellRequest {
    private Double actualPrice;
    private UNIT discountUnit;
    private Double discountValue;
    private List<String> benefits;
    private Boolean isVatIncluded;
    private Integer warrantPeriod;
    private String discountPrice;

    public Sell requestToSell() {
        Sell sell = new Sell();
        sell.setActualPrice(actualPrice);
        if ( getIsVatIncluded ( ) ) {
            sell.setVat ( 5.0 );
        } else {
            sell.setVat ( 0.0 );
        }
        if(discountUnit == UNIT.AED){
            sell.setDiscountUnit(UNIT.AED);
            sell.setDiscountPrice(actualPrice-discountValue);
            sell.setDiscountValue(discountValue);
        } else if (discountUnit == UNIT.PERCENTAGE) {
            sell.setDiscountUnit(UNIT.PERCENTAGE);
            sell.setDiscountPrice(actualPrice-(actualPrice * (discountValue / 100)));
            sell.setDiscountValue(discountValue);
        }
        sell.setBenefits(benefits != null ? benefits : new ArrayList<> ());
        if (warrantPeriod<= 0)  {
            sell.setWarrantPeriod(0);
        }
        else {
            sell.setWarrantPeriod (warrantPeriod);
        }
        return sell;
    }
}
