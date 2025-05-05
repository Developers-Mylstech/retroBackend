package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Address;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    @NotBlank(message = "Street address is required")
    private String streetAddress;
    
    private String buildingName;
    
    private String flatNo;
    
    @NotBlank(message = "Area/Locality is required")
    private String area;
    
    @NotBlank(message = "Emirate is required")
    private String emirate;
    
    private String country = "United Arab Emirates";
    
    private String landmark;
    
    private String addressType;
    
    private boolean isDefault;
    
    /**
     * Converts request to Address entity
     * @return Address entity
     */
    public Address toAddress() {
        Address address = new Address();
        address.setStreetAddress(streetAddress);
        address.setBuildingName(buildingName);
        address.setFlatNo(flatNo);
        address.setArea(area);
        address.setEmirate(emirate);
        address.setCountry(country != null && !country.isEmpty() ? country : "United Arab Emirates");
        address.setLandmark(landmark);
        address.setAddressType(addressType);
        address.setDefault(isDefault);
        return address;
    }
}