package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long addressId;
    private String streetAddress;
    private String buildingName;
    private String flatNo;
    private String area;
    private String emirate;
    private String country;
    private String landmark;
    private String addressType;
    private boolean isDefault;
    private String formattedAddress;
    
    /**
     * Constructs a response from an Address entity
     * @param address the address entity
     */
    public AddressResponse(Address address) {
        this.addressId = address.getAddressId();
        this.streetAddress = address.getStreetAddress();
        this.buildingName = address.getBuildingName();
        this.flatNo = address.getFlatNo();
        this.area = address.getArea();
        this.emirate = address.getEmirate();
        this.country = address.getCountry();
        this.landmark = address.getLandmark();
        this.addressType = address.getAddressType();
        this.isDefault = address.isDefault();
        this.formattedAddress = address.getFormattedAddress();
    }
}