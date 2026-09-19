package com.futbol.estadisticas.application.port.in;

import com.futbol.estadisticas.application.port.dto.request.SecurityDTORequest.*;
import com.futbol.estadisticas.application.port.dto.response.SecurityDTOResponse.*;

public interface AuthUseCase {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
