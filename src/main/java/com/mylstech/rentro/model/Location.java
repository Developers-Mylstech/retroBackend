package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;
    
    private String street;
    
    private String area;
    
    private String building;
    
    private String villaNo;
    
    private String country;
    
    private String gmapLink;
    
    // Helper method to create a formatted location string
    public String getFormattedLocation() {
        StringBuilder sb = new StringBuilder();
        
        if (building != null && !building.isEmpty()) {
            sb.append(building);
            
            if (villaNo != null && !villaNo.isEmpty()) {
                sb.append(", Villa ").append(villaNo);
            }
        }
        
        if (sb.length() > 0) {
            sb.append(", ");
        }
        
        if (street != null && !street.isEmpty()) {
            sb.append(street).append(", ");
        }
        
        if (area != null && !area.isEmpty()) {
            sb.append(area);
        }
        
        if (country != null && !country.isEmpty()) {
            sb.append(", ").append(country);
        }
        
        return sb.toString();
    }
}
