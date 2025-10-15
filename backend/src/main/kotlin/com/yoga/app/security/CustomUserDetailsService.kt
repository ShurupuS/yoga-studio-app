package com.yoga.app.security

import com.yoga.app.entity.User
import com.yoga.app.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")
        
        return UserPrincipal(
            id = user.id,
            email = user.email,
            password = user.password,
            authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}")),
            user = user
        )
    }
}

class UserPrincipal(
    val id: Long,
    val email: String,
    val password: String,
    val authorities: Collection<SimpleGrantedAuthority>,
    val user: User
) : UserDetails {
    
    override fun getAuthorities(): Collection<SimpleGrantedAuthority> = authorities
    
    override fun getPassword(): String = password
    
    override fun getUsername(): String = email
    
    override fun isAccountNonExpired(): Boolean = true
    
    override fun isAccountNonLocked(): Boolean = true
    
    override fun isCredentialsNonExpired(): Boolean = true
    
    override fun isEnabled(): Boolean = user.isActive
}


