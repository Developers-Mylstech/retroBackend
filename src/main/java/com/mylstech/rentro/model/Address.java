package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    
    @Column(nullable = false)
    private String streetAddress;
    
    private String buildingName;
    
    private String flatNo;
    
    @Column(nullable = false)
    private String area;
    
    @Column(nullable = false)
    private String emirate;
    
    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'United Arab Emirates'")
    private String country = "United Arab Emirates";
    
    private String landmark;
    
    private String addressType; // e.g., "HOME", "WORK", "OTHER"
    
    private boolean isDefault;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    // Helper method to create a formatted address string
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        
        if (buildingName != null && !buildingName.isEmpty()) {
            sb.append(buildingName);
            
            if (flatNo != null && !flatNo.isEmpty()) {
                sb.append(", Flat ").append(flatNo);
            }
        }
        
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(streetAddress);
        
        sb.append(", ").append(area);
        
        sb.append(", ").append(emirate);
        
        sb.append(", ").append(country);
        
        if (landmark != null && !landmark.isEmpty()) {
            sb.append(" (Near ").append(landmark).append(")");
        }
        
        return sb.toString();
    }
}