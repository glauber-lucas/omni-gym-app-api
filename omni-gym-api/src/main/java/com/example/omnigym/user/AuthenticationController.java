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
import com.example.omnigym.user.AuthenticationService;
import jakarta.validation.Valid;
import com.example.omnigym.user.LocalAuthDTO;
import com.example.omnigym.user.RefreshTokenDTO;
import com.example.omnigym.user.LoginUserDTO;
import com.example.omnigym.user.MeUserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO dto) {
        authenticationService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado");
    }

    @PostMapping("/local")
    public ResponseEntity<?> local(@RequestBody java.util.Map<String, Object> payload) {
        String identifier = null;
        String password = null;

        if (payload.containsKey("identifier")) {
            identifier = String.valueOf(payload.get("identifier"));
        } else if (payload.containsKey("usuario")) {
            identifier = String.valueOf(payload.get("usuario"));
        }

        if (payload.containsKey("password")) {
            password = String.valueOf(payload.get("password"));
        } else if (payload.containsKey("senha")) {
            password = String.valueOf(payload.get("senha"));
        }

        if (identifier == null || password == null) {
            return ResponseEntity.badRequest().body("dados de autenticação ausentes");
        }

        LoginResponseDTO response = authenticationService.authenticate(new AuthenticationDTO(identifier, password));

        Long userId = null;
        String documentId = null;
        String email = null;
        String role = null;
        try {
            User user = authenticationService.getUserByUsername(identifier);
            userId = user.getId();
            documentId = user.getDocumentId();
            email = user.getUsername();
            role = user.getRole().name();
        } catch (Exception ignored) {}

        LoginUserDTO userDto = new LoginUserDTO(userId, documentId, email, role);

        var body = java.util.Map.of(
                "jwt", response.token(),
                "user", userDto,
                "refreshToken", response.refreshToken()
        );

        return ResponseEntity.ok(body);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody java.util.Map<String, Object> payload) {
        String identifier = null;
        String password = null;

        if (payload.containsKey("usuario")) {
            identifier = String.valueOf(payload.get("usuario"));
            password = String.valueOf(payload.get("senha"));
        } else if (payload.containsKey("identifier")) {
            identifier = String.valueOf(payload.get("identifier"));
            password = String.valueOf(payload.get("password"));
        }

        if (identifier == null || password == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(authenticationService.authenticate(new AuthenticationDTO(identifier, password)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDTO dto) {
        String refresh = dto.refreshToken();
        if (refresh == null || refresh.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (!authenticationService.isRefreshTokenValid(refresh)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newToken = authenticationService.refreshToken(refresh);

        // load user from token
        String username = authenticationService.getUsernameFromToken(newToken);
        User user = authenticationService.getUserByUsername(username);

        LoginUserDTO userDto = new LoginUserDTO(user.getId(), user.getDocumentId(), user.getUsername(), user.getRole().name());

        String newRefreshToken = authenticationService.generateRefreshTokenForUser(user);

        var body = java.util.Map.of(
                "jwt", newToken,
                "user", userDto,
                "refreshToken", newRefreshToken
        );

        return ResponseEntity.ok(body);
    }

    @GetMapping("/me")
    public ResponseEntity<MeUserDTO> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = auth.getName();
        User user = authenticationService.getUserByUsername(username);
        MeUserDTO dto = new MeUserDTO(user.getId(), user.getDocumentId(), user.getName(), user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(dto);
    }
}
