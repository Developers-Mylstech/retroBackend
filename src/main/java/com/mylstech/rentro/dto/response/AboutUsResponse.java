package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.AboutUs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutUsResponse {
    private Long aboutUsId;
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    
    public AboutUsResponse(AboutUs aboutUs) {
        this.aboutUsId = aboutUs.getAboutUsId();
        this.title = aboutUs.getTitle();
        this.subtitle = aboutUs.getSubtitle();
        this.description = aboutUs.getDescription();
        this.imageUrl = aboutUs.getImageUrl();
    }
}