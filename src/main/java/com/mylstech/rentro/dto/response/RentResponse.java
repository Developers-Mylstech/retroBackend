package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Rent;
import com.mylstech.rentro.util.UNIT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentResponse {
    private Long rentId;
    private Double monthlyPrice;
    private Double discountedPrice;
    private Double vat;
    private List<String> benefits;
    private Double discountValue;
    private UNIT discountUnit;


    public RentResponse(Rent rent) {
        this.rentId = rent.getRentId();
        this.monthlyPrice = rent.getMonthlyPrice();
        this.discountedPrice = rent.getDiscountPrice();
        this.vat = rent.getVat();
        this.benefits = rent.getBenefits();
        this.discountValue = rent.getDiscountValue();
        this.discountUnit = rent.getDiscountUnit();
    }
}
