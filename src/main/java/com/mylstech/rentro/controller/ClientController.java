package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.ClientRequest;
import com.mylstech.rentro.dto.response.ClientResponse;
import com.mylstech.rentro.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
public class ClientController {

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
}
