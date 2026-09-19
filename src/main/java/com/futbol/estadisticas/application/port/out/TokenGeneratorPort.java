package com.futbol.estadisticas.application.port.out;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenGeneratorPort {
    String generateToken(UserDetails userDetails);

}
