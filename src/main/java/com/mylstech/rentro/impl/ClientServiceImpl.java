package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.ClientRequest;
import com.mylstech.rentro.dto.response.ClientResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Client;
import com.mylstech.rentro.repository.ClientRepository;
import com.mylstech.rentro.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @Override
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll ( ).stream ( ).map ( ClientResponse::new ).toList ( );
    }

    @Override
    public ClientResponse getClientById(Long clientId) {
        return new ClientResponse ( clientRepository.findById ( clientId ).orElseThrow ( () -> new ResourceNotFoundException ( "client not found" ) ) );
    }

    @Override
    public ClientResponse createClient(ClientRequest request) {
        Client client = request.requestToClient ( );
        Client save = clientRepository.save ( client );
        return new ClientResponse ( save );
    }

    @Override
    public ClientResponse updateClient(Long clientId, ClientRequest request) {
        Client client = clientRepository.findById ( clientId ).orElseThrow ( () -> new ResourceNotFoundException ( "client not found" ) );
        if (request.getName ()!=null ) {
            client.setName ( request.getName ( ) );
        }
        if(request.getImageUrl ()!=null){
            client.setImageUrl ( request.getImageUrl ( ) );
        }
        return new ClientResponse (  clientRepository.save ( client ) );
    }

    @Override
    public void deleteClient(Long clientId) {
        Client client = clientRepository.findById ( clientId ).orElseThrow ( () -> new ResourceNotFoundException ( "client not found" ) );
        clientRepository.delete ( client );
    }
}
