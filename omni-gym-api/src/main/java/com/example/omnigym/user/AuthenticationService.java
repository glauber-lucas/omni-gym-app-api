package com.example.omnigym.user;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import com.example.omnigym.user.AuthenticationDTO;
import com.example.omnigym.user.LoginResponseDTO;
import com.example.omnigym.user.RegisterDTO;
import com.example.omnigym.user.Role;
import com.example.omnigym.user.User;
import com.example.omnigym.user.UserRepository;
import com.example.omnigym.core.security.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Transactional
    public void register(RegisterDTO dto) {
        if (userRepository.existsByUsername(dto.usuario())) {
            throw new IllegalArgumentException("Usuário já existe");
        }

        User user = new User();
        user.setUsername(dto.usuario());
        user.setPassword(passwordEncoder.encode(dto.senha()));
        
        Role targetRole = Role.ROLE_ALUNO;
        if (dto.role() != null && "INSTRUTOR".equalsIgnoreCase(dto.role())) {
            targetRole = Role.ROLE_INSTRUTOR;
        }
        user.setRole(targetRole);

        userRepository.save(user);
    }

    public LoginResponseDTO authenticate(AuthenticationDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.usuario(), dto.senha())
        );

        User user = userRepository.findByUsername(dto.usuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado após autenticação"));

        String token = tokenService.generateToken(user);
        return new LoginResponseDTO(token, "Bearer", tokenService.getExpirationMs());
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + username));
    }

    public boolean isTokenValid(String token) {
        return tokenService.validateToken(token);
    }

    public String refreshToken(String refreshToken) {
        String username = tokenService.getUsernameFromToken(refreshToken);
        User user = getUserByUsername(username);
        return tokenService.generateToken(user);
    }

    public String getUsernameFromToken(String token) {
        return tokenService.getUsernameFromToken(token);
    }
}

