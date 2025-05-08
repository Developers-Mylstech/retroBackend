package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    private Long imageId;
    private String imageUrl;

    public ImageDTO(Image image ){
        this.imageId = image.getImageId ();
        this.imageUrl =image.getImageUrl ();
    }
}