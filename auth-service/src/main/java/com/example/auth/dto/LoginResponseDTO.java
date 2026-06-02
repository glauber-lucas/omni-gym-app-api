package com.example.auth.dto;

public record LoginResponseDTO(String token, String tipoToken, long expiraEmMillis) {}

