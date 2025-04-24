package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Rent;
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
    private Double discountPrice;
    private Double vat;
    private List<String> benefits;
    private Boolean isWarrantyAvailable;
    private Integer warrantPeriod;

    public RentResponse(Rent rent) {
        this.rentId = rent.getRentId();
        this.monthlyPrice = rent.getMonthlyPrice();
        this.discountPrice = rent.getDiscountPrice();
        this.vat = rent.getVat();
        this.benefits = rent.getBenefits();
        this.isWarrantyAvailable = rent.getIsWarrantyAvailable();
        this.warrantPeriod = rent.getWarrantPeriod();
    }
}
