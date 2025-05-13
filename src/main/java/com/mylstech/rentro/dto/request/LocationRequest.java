package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    private String street;
    private String area;
    private String building;
    private String villaNo;
    private String country;
    private String gmapLink;
    
    /**
     * Converts request to Location entity
     * @return Location entity
     */
    public Location toLocation() {
        Location location = new Location();
        location.setStreet(street);
        location.setArea(area);
        location.setBuilding(building);
        location.setVillaNo(villaNo);
        location.setCountry(country);
        location.setGmapLink(gmapLink);
        return location;
    }
}