package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Client;
import lombok.Data;

@Data
public class ClientRequest {
    private Long clientId;
    private String name;
    private String imageUrl;

    public Client requestToClient() {
        Client client = new Client ( );
        setName ( name );
        setImageUrl ( imageUrl );
        return client;
    }
}
