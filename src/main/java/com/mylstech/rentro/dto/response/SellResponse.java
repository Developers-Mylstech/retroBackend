package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Sell;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellResponse {
    private Long sellId;
    private Double actualPrice;
    private Double discountPrice;
    private Double vat;
    private List<String> benefits;
    private Boolean isWarrantyAvailable;
    private Integer warrantPeriod;

    public SellResponse(Sell sell) {
        this.sellId = sell.getSellId ( );
        this.actualPrice = sell.getActualPrice ( );
        this.discountPrice = sell.getDiscountPrice ( );
        this.vat = sell.getVat ( );
        this.benefits = sell.getBenefits ( );
       this.isWarrantyAvailable = sell.getIsWarrantyAvailable ( );
       this.warrantPeriod = sell.getWarrantPeriod ( );
    }
}
