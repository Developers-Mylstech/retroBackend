package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Banner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerResponse {
    private Long bannerId;
    private String title;
    private String imageUrl;
    private ImageDTO image;

    public BannerResponse(Banner banner ) {
        this.bannerId = banner.getBannerId ();
        this.title = banner.getTitle ();
        if (banner.getImage() != null) {
            this.image = new ImageDTO(banner.getImage());
            this.imageUrl = banner.getImage ().getImageUrl ();
        }
    }
}
