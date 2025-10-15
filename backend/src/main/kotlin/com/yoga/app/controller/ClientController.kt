package com.yoga.app.controller

import com.yoga.app.dto.ClientDto
import com.yoga.app.dto.CreateClientRequest
import com.yoga.app.dto.UpdateClientRequest
import com.yoga.app.service.ClientService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = ["*"])
class ClientController(
    private val clientService: ClientService
) {
    
    @GetMapping
    fun getAllClients(pageable: Pageable): ResponseEntity<Page<ClientDto>> {
        val clients = clientService.getAllClients(pageable)
        return ResponseEntity.ok(clients)
    }
    
    @GetMapping("/search")
    fun searchClients(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) phone: String?,
        @RequestParam(required = false) email: String?,
        pageable: Pageable
    ): ResponseEntity<Page<ClientDto>> {
        val clients = clientService.searchClients(name, phone, email, pageable)
        return ResponseEntity.ok(clients)
    }
    
    @GetMapping("/{id}")
    fun getClientById(@PathVariable id: Long): ResponseEntity<ClientDto> {
        val client = clientService.getClientById(id)
        return ResponseEntity.ok(client)
    }
    
    @PostMapping
    fun createClient(@Valid @RequestBody request: CreateClientRequest): ResponseEntity<ClientDto> {
        val client = clientService.createClient(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(client)
    }
    
    @PutMapping("/{id}")
    fun updateClient(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateClientRequest
    ): ResponseEntity<ClientDto> {
        val client = clientService.updateClient(id, request)
        return ResponseEntity.ok(client)
    }
    
    @DeleteMapping("/{id}")
    fun deleteClient(@PathVariable id: Long): ResponseEntity<Void> {
        clientService.deleteClient(id)
        return ResponseEntity.noContent().build()
    }
}


