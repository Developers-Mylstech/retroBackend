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
    private String imageUrl;
    
    public AboutUs requestToAboutUs() {
        AboutUs aboutUs = new AboutUs();
        aboutUs.setTitle(this.title);
        aboutUs.setSubtitle(this.subtitle);
        aboutUs.setDescription(this.description);
        aboutUs.setImageUrl(this.imageUrl);
        return aboutUs;
    }
}