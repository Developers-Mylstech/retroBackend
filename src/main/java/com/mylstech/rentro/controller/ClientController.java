package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.ClientRequest;
import com.mylstech.rentro.dto.response.ClientResponse;
import com.mylstech.rentro.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final Logger logger = LoggerFactory.getLogger ( ClientController.class );
    private final ClientService clientService;

    @PostMapping
    private ResponseEntity<ClientResponse> createClient(@RequestBody ClientRequest clientRequest) {
        ClientResponse clientResponse = clientService.createClient ( clientRequest );
        return new ResponseEntity<> ( clientResponse, HttpStatus.OK );
    }

    @GetMapping("/{clientId}")
    private ResponseEntity<ClientResponse> findByClientId(@PathVariable Long clientId){
        return new ResponseEntity<> ( clientService.getClientById ( clientId ),HttpStatus.OK);
    }

    @GetMapping
    private ResponseEntity<List<ClientResponse>> getAllClient(){
        return new ResponseEntity<> ( clientService.getAllClients (),HttpStatus.OK);
    }

    @PutMapping("/{clientId}")
    private ResponseEntity<ClientResponse> updateClientDetails(@PathVariable Long clientId,@RequestBody ClientRequest clientRequest){
        return new ResponseEntity<> ( clientService.updateClient ( clientId,clientRequest ) ,HttpStatus.OK);
    }
    @DeleteMapping("/{clientId}")
    private ResponseEntity<Void> deleteClient(@PathVariable Long clientId){
        clientService.deleteClient ( clientId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @PutMapping("/{id}/image/{imageId}")
    @Operation(summary = "Set client image", description = "Sets the image for a client")
    public ResponseEntity<ClientResponse> setClientImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {
        try {
            logger.debug("Setting image with ID {} for client with ID: {}", imageId, id);
            ClientResponse client = clientService.setClientImage(id, imageId);
            logger.debug("Set image for client: {}", client);
            return ResponseEntity.ok(client);
        } catch (Exception e) {
            logger.error("Error setting image for client with ID: " + id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}/image")
    @Operation(summary = "Remove client image", description = "Removes the image from a client")
    public ResponseEntity<ClientResponse> removeClientImage(@PathVariable Long id) {
        try {
            logger.debug("Removing image for client with ID: {}", id);
            ClientResponse client = clientService.removeClientImage(id);
            logger.debug("Removed image for client: {}", client);
            return ResponseEntity.ok(client);
        } catch (Exception e) {
            logger.error("Error removing image for client with ID: " + id, e);
            throw e;
        }
    }
}
