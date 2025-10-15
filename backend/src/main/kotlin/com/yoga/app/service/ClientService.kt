package com.yoga.app.service

import com.yoga.app.dto.ClientDto
import com.yoga.app.dto.CreateClientRequest
import com.yoga.app.dto.UpdateClientRequest
import com.yoga.app.entity.Client
import com.yoga.app.repository.ClientRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ClientService(
    private val clientRepository: ClientRepository
) {
    
    fun getAllClients(pageable: Pageable): Page<ClientDto> {
        return clientRepository.findByIsActiveTrue(pageable)
            .map { ClientDto.from(it) }
    }
    
    fun getClientById(id: Long): ClientDto {
        val client = clientRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Client not found with id: $id") }
        return ClientDto.from(client)
    }
    
    fun searchClients(name: String?, phone: String?, email: String?, pageable: Pageable): Page<ClientDto> {
        return clientRepository.searchClients(name, phone, email, pageable)
            .map { ClientDto.from(it) }
    }
    
    fun createClient(request: CreateClientRequest): ClientDto {
        val client = Client(
            name = request.name,
            phone = request.phone,
            email = request.email,
            birthDate = request.birthDate,
            notes = request.notes
        )
        
        val savedClient = clientRepository.save(client)
        return ClientDto.from(savedClient)
    }
    
    fun updateClient(id: Long, request: UpdateClientRequest): ClientDto {
        val client = clientRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Client not found with id: $id") }
        
        val updatedClient = client.copy(
            name = request.name ?: client.name,
            phone = request.phone ?: client.phone,
            email = request.email ?: client.email,
            birthDate = request.birthDate ?: client.birthDate,
            notes = request.notes ?: client.notes,
            isActive = request.isActive ?: client.isActive,
            updatedAt = LocalDateTime.now()
        )
        
        val savedClient = clientRepository.save(updatedClient)
        return ClientDto.from(savedClient)
    }
    
    fun deleteClient(id: Long) {
        val client = clientRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Client not found with id: $id") }
        
        val deactivatedClient = client.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )
        
        clientRepository.save(deactivatedClient)
    }
}


