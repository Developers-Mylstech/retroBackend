package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Rent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentResponse {
    private Long rentId;
    private Double monthlyPrice;
    private Double discountPrice;
    private Double vat;

    public RentResponse(Rent rent) {
        this.rentId = rent.getRentId();
        this.monthlyPrice = rent.getMonthlyPrice();
        this.discountPrice = rent.getDiscountPrice();
        this.vat = rent.getVat();
    }
}
