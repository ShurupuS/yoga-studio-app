package com.yoga.app.repository

import com.yoga.app.entity.Client
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository : JpaRepository<Client, Long> {
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Client>
    fun findByPhoneContaining(phone: String, pageable: Pageable): Page<Client>
    fun findByEmailContainingIgnoreCase(email: String, pageable: Pageable): Page<Client>
    fun findByIsActiveTrue(pageable: Pageable): Page<Client>
    
    @Query("SELECT c FROM Client c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
            "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    fun searchClients(
        @Param("name") name: String?,
        @Param("phone") phone: String?,
        @Param("email") email: String?,
        pageable: Pageable
    ): Page<Client>
}


