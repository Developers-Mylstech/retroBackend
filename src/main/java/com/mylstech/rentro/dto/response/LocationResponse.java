package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long locationId;
    private String street;
    private String area;
    private String building;
    private String villaNo;
    private String country;
    private String gmapLink;
    private String formattedLocation;
    
    /**
     * Constructs a response from a Location entity
     * @param location the location entity
     */
    public LocationResponse(Location location) {
        this.locationId = location.getLocationId();
        this.street = location.getStreet();
        this.area = location.getArea();
        this.building = location.getBuilding();
        this.villaNo = location.getVillaNo();
        this.country = location.getCountry();
        this.gmapLink = location.getGmapLink();
        this.formattedLocation = location.getFormattedLocation();
    }
}