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


    private ImageDTO image;

    public ClientResponse(Client client) {
        this.clientId = client.getClientId ( );
        this.name = client.getName ( );

        // Handle image - both for backward compatibility and new approach
        if ( client.getImage ( ) != null ) {

            this.image = new ImageDTO ( client.getImage ( ) );
        } else {

            this.image = null;
        }
    }
}