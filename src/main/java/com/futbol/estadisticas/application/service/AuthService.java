package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.SecurityDTORequest.LoginRequest;
import com.futbol.estadisticas.application.port.dto.request.SecurityDTORequest.RegisterRequest;
import com.futbol.estadisticas.application.port.dto.response.SecurityDTOResponse.AuthResponse;
import com.futbol.estadisticas.application.port.in.AuthUseCase;
import com.futbol.estadisticas.application.port.out.UsuarioRepositoryPort;
import com.futbol.estadisticas.domain.model.Usuario;
import com.futbol.estadisticas.domain.model.enums.Rol;
import com.futbol.estadisticas.domain.model.exception.UsuarioYaExisteException;
import com.futbol.estadisticas.infrastructure.in.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService    userDetailsService;
    private final JWTService            jwtService;
    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder       passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        return new AuthResponse(jwtService.generateToken(user));
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new UsuarioYaExisteException(
                    "El usuario ya existe: " + request.username());
        }

        Usuario usuario = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .rol(Rol.USUARIO)
                .activo(true)
                .build();

        usuarioRepository.save(usuario);

        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        return new AuthResponse(jwtService.generateToken(user));
    }
}