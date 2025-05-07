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
}
