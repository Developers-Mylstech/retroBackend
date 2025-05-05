package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Rent;
import com.mylstech.rentro.util.UNIT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DurationFormat;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentRequest {
    private Double monthlyPrice;
    private UNIT discountUnit;
    private Double discountValue;
    private List<String> benefits;
    private Boolean isVatIncluded;

    public Rent requestToRent() {
        Rent rent = new Rent();
        rent.setMonthlyPrice(monthlyPrice);
        if(discountUnit == UNIT.AED){
            rent.setDiscountUnit(UNIT.AED);
            rent.setDiscountPrice(monthlyPrice-discountValue);
            rent.setDiscountValue(discountValue);
        } else if (discountUnit == UNIT.PERCENTAGE) {
            rent.setDiscountUnit(UNIT.PERCENTAGE);
            rent.setDiscountPrice(monthlyPrice-(monthlyPrice * (discountValue / 100)));
            rent.setDiscountValue(discountValue);
        }
        if ( getIsVatIncluded ( ).booleanValue ( ) ) {
            rent.setVat ( 5.0 );
        } else {
            rent.setVat ( 0.0 );
        }
        rent.setBenefits(benefits != null ? benefits : new ArrayList<>());
        return rent;
    }
}
