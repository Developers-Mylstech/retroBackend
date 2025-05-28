package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    private String name;

    private Long imageId;

    public Client requestToClient() {
        Client client = new Client ( );
        client.setName ( name );

        return client;
    }
}
