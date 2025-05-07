package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long clientId;
    private String name;
    private String imageUrl;

    public ClientResponse(Client client) {
        this.clientId = client.getClientId ( );
        this.name = client.getName ( );
        this.imageUrl = client.getImageUrl ( );
    }
}
