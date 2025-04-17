package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Rent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentRequest {
    private Double monthlyPrice;
    private Double discountPrice;

    
    public Rent requestToRent() {
        Rent rent = new Rent();
        rent.setMonthlyPrice(monthlyPrice);
        rent.setDiscountPrice(discountPrice);

        return rent;
    }
}
