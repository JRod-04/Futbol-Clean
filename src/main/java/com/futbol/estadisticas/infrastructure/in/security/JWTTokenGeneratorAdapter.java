package com.futbol.estadisticas.infrastructure.in.security;

import com.futbol.estadisticas.application.port.out.TokenGeneratorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JWTTokenGeneratorAdapter implements TokenGeneratorPort {

    private final JWTService jwtService;

    @Override
    public String generateToken(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }
}