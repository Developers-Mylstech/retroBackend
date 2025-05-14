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
//    private String imageUrl;
    private Long imageId;

    public Client requestToClient() {
        Client client = new Client ( );
        client.setName ( name );
//        client.setImageUrl ( imageUrl );
        return client;
    }
}
