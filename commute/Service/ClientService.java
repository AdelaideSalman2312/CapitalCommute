package com.CapitalCommute.commute.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.CapitalCommute.commute.model.Client;
import com.CapitalCommute.commute.repository.ClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {
    
    private final ClientRepository clientRepository;

    public Client saveClient(Client client) {
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (clientRepository.existsByPhoneNumber(client.getPhoneNumber())) {
            throw new RuntimeException("Phone number already in use");
        }
        if (clientRepository.existsById(client.getNationalId())) {
            throw new RuntimeException("National ID already registered");
        }
        return clientRepository.save(client);
    }

    public Client getClientByNationalId(String nationalId) {
        return clientRepository.findById(nationalId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Client getClientByClientId(UUID clientId) {
        return clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client updateClient(String nationalId, Client updatedClient) {
        Client existingClient = clientRepository.findById(nationalId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        
        // Update Person fields
        existingClient.setFirstName(updatedClient.getFirstName());
        existingClient.setMiddleName(updatedClient.getMiddleName());
        existingClient.setLastName(updatedClient.getLastName());
        existingClient.setEmail(updatedClient.getEmail());
        existingClient.setPhoneNumber(updatedClient.getPhoneNumber());
        existingClient.setResidence(updatedClient.getResidence());
        existingClient.setDateOfBirth(updatedClient.getDateOfBirth());
        existingClient.setGender(updatedClient.getGender());
        existingClient.setPassportNumber(updatedClient.getPassportNumber());
        existingClient.setActive(updatedClient.isActive());
        
        return clientRepository.save(existingClient);
    }

    public void deleteClient(String nationalId) {
        if (!clientRepository.existsById(nationalId)) {
            throw new RuntimeException("Client not found");
        }
        clientRepository.deleteById(nationalId);
    }
    
}
