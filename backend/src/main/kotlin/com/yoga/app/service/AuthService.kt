package com.yoga.app.service

import com.yoga.app.dto.AuthResponse
import com.yoga.app.dto.LoginRequest
import com.yoga.app.dto.RegisterRequest
import com.yoga.app.dto.UserDto
import com.yoga.app.entity.User
import com.yoga.app.entity.UserRole
import com.yoga.app.repository.UserRepository
import com.yoga.app.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {
    
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("User with email ${request.email} already exists")
        }
        
        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name,
            role = UserRole.TRAINER
        )
        
        val savedUser = userRepository.save(user)
        val token = jwtService.generateToken(savedUser)
        
        return AuthResponse(
            accessToken = token,
            user = UserDto.from(savedUser)
        )
    }
    
    fun login(request: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("User not found")
        
        val token = jwtService.generateToken(user)
        
        return AuthResponse(
            accessToken = token,
            user = UserDto.from(user)
        )
    }
}


