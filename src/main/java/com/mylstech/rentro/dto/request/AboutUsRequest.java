package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.AboutUs;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutUsRequest {
    private String title;
    private String subtitle;
    
    @Size(max = 1000)
    private String description;
    

    // New field for Image entity integration
    private Long imageId;
    
    public AboutUs requestToAboutUs() {
        AboutUs aboutUs = new AboutUs();
        aboutUs.setTitle(this.title);
        aboutUs.setSubtitle(this.subtitle);
        aboutUs.setDescription(this.description);

        // Note: image will be set in the service layer
        return aboutUs;
    }
}