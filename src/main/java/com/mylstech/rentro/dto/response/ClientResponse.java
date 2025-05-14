package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Client;
import com.mylstech.rentro.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long clientId;
    private String name;
    
    /**
     * @deprecated This field is only for backward compatibility.
     * Use image.imageUrl instead.
     */
//    @Deprecated
//    private String imageUrl;
    
    private ImageDTO image;

    public ClientResponse(Client client) {
        this.clientId = client.getClientId();
        this.name = client.getName();
        
        // Handle image - both for backward compatibility and new approach
        if (client.getImage() != null) {
//            this.imageUrl = client.getImage().getImageUrl();
            this.image = new ImageDTO(client.getImage());
        } else {
//            this.imageUrl = null;
            this.image = null;
        }
    }
}