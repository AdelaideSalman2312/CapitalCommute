package com.CapitalCommute.commute.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CapitalCommute.commute.model.Client;
import com.CapitalCommute.commute.Service.ClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client savedClient = clientService.saveClient(client);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    @GetMapping("/national-id/{nationalId}")
    public ResponseEntity<Client> getClientByNationalId(@PathVariable String nationalId) {
        Client client = clientService.getClientByNationalId(nationalId);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/client-id/{clientId}")
    public ResponseEntity<Client> getClientByClientId(@PathVariable UUID clientId) {
        Client client = clientService.getClientByClientId(clientId);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {
        Client client = clientService.getClientByEmail(email);
        return ResponseEntity.ok(client);
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @PutMapping("/{nationalId}")
    public ResponseEntity<Client> updateClient(
            @PathVariable String nationalId,
            @RequestBody Client client) {
        Client updatedClient = clientService.updateClient(nationalId, client);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{nationalId}")
    public ResponseEntity<Void> deleteClient(@PathVariable String nationalId) {
        clientService.deleteClient(nationalId);
        return ResponseEntity.noContent().build();
    }
}
