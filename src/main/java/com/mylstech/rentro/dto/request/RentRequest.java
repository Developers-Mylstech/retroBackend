package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Rent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentRequest {
    private Double monthlyPrice;
    private Double discountPrice;
    private List<String> benefits;
    private Boolean isWarrantyAvailable;
    private Integer warrantPeriod;
    public Rent requestToRent() {
        Rent rent = new Rent();
        rent.setMonthlyPrice(monthlyPrice);
        rent.setDiscountPrice(discountPrice);
        rent.setBenefits(benefits != null ? benefits : new ArrayList<>());
        if (Boolean.TRUE.equals(isWarrantyAvailable) ) {
            rent.setWarrantPeriod ( warrantPeriod );
        }
        rent.setIsWarrantyAvailable(isWarrantyAvailable);
        return rent;
    }
}
