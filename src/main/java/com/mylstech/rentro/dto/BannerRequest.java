package com.mylstech.rentro.dto;

import com.mylstech.rentro.model.Banner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerRequest {
    private String title;
    private String imageUrl;

    public Banner requestToBanner(){
        Banner banner = new Banner ( );
        banner.setTitle ( title );
        banner.setImageUrl ( imageUrl );
        return banner;
    }
}
