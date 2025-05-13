package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.ClientRequest;
import com.mylstech.rentro.dto.response.ClientResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Client;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.repository.ClientRepository;
import com.mylstech.rentro.repository.ImageRepository;
import com.mylstech.rentro.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ImageRepository imageRepository;

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
        Client savedClient = request.requestToClient();
        
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));
            savedClient.setImage(image);
            
            // For backward compatibility, also set imageUrl
            // This line can be removed once the migration is complete
            savedClient.setImageUrl(image.getImageUrl());
        }

        return new ClientResponse(clientRepository.save(savedClient));
    }

    @Override
    public ClientResponse updateClient(Long clientId, ClientRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("client not found"));
        
        if (request.getName() != null) {
            client.setName(request.getName());
        }
        
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));
            client.setImage(image);
            
            // For backward compatibility, also set imageUrl
            // This line can be removed once the migration is complete
            client.setImageUrl(image.getImageUrl());
        }
        
        return new ClientResponse(clientRepository.save(client));
    }

    @Override
    public void deleteClient(Long clientId) {
        Client client = clientRepository.findById ( clientId ).orElseThrow ( () -> new ResourceNotFoundException ( "client not found" ) );
        clientRepository.delete ( client );
    }

    @Override
    @Transactional
    public ClientResponse setClientImage(Long clientId, Long imageId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
        
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        
        client.setImage(image);
        
        // For backward compatibility, also set imageUrl
        // This line can be removed once the migration is complete
        client.setImageUrl(image.getImageUrl());
        
        return new ClientResponse(clientRepository.save(client));
    }

    @Override
    @Transactional
    public ClientResponse removeClientImage(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
        
        client.setImage(null);
        
        // For backward compatibility, also clear imageUrl
        // This line can be removed once the migration is complete
        client.setImageUrl(null);
        
        return new ClientResponse(clientRepository.save(client));
    }
}
