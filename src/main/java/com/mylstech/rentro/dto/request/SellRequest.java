package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Sell;
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
    private Double discountPrice;
    private List<String> benefits;
    private Boolean isWarrantyAvailable;
    private Integer warrantPeriod;

    public Sell requestToSell() {
        Sell sell = new Sell();
        sell.setActualPrice(actualPrice);
        sell.setDiscountPrice(discountPrice);
        sell.setBenefits(benefits != null ? benefits : new ArrayList<> ());
        if (Boolean.TRUE.equals(this.isWarrantyAvailable) ) {
            sell.setWarrantPeriod(warrantPeriod);
        }
        else {
            sell.setWarrantPeriod (0);
        }
            sell.setIsWarrantyAvailable(isWarrantyAvailable);
        return sell;
    }
}
