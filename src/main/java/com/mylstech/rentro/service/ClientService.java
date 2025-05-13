package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.ClientRequest;
import com.mylstech.rentro.dto.response.ClientResponse;

import java.util.List;

public interface ClientService {
        List<ClientResponse> getAllClients();
    ClientResponse getClientById(Long clientId);
    ClientResponse createClient(ClientRequest request);
    ClientResponse updateClient(Long clientId, ClientRequest request);
    void deleteClient(Long clientId);
    /**
     * Set the image for a client
     * @param clientId the client ID
     * @param imageId the image ID
     * @return the updated client response
     */
    ClientResponse setClientImage(Long clientId, Long imageId);

    /**
     * Remove the image from a client
     * @param clientId the client ID
     * @return the updated client response
     */
    ClientResponse removeClientImage(Long clientId);
}
