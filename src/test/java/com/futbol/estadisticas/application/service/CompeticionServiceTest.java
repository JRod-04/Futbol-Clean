/*package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.CrearCompeticionRequest;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.mapper.CompeticionMapper;
import com.futbol.estadisticas.application.port.mapper.PartidoMapper;
import com.futbol.estadisticas.application.port.out.CompeticionRepositoryPort;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.application.service.CompeticionService;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.Partido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompeticionServiceTest {

    @Mock
    private CompeticionRepositoryPort competicionRepository;

    @Mock
    private PartidoRepositoryPort partidoRepository;

    @Mock
    private CompeticionMapper competicionMapper;

    @Mock
    private PartidoMapper partidoMapper;

    @InjectMocks
    private CompeticionService competicionService;

    private static final UUID ID_COMPETICION = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private Competicion competicion;
    private CompeticionResponse response;
    private CrearCompeticionRequest request;

    @BeforeEach
    void setUp() {
        request = new CrearCompeticionRequest(
                "Premier League",
                LocalDateTime.of(2024, 8, 16, 0, 0),
                LocalDateTime.of(2025, 5, 25, 23, 59)
        );

        competicion = Competicion.builder()
                .idCompeticion(ID_COMPETICION)
                .nombre("Premier League")
                .fechaInicio(LocalDateTime.of(2024, 8, 16, 0, 0))
                .fechaFin(LocalDateTime.of(2025, 5, 25, 23, 59))
                .build();

        response = new CompeticionResponse(
                ID_COMPETICION, "Premier League",
                LocalDateTime.of(2024, 8, 16, 0, 0),
                LocalDateTime.of(2025, 5, 25, 23, 59),
                false, false, false, 0, 0, 0, 0.0
        );
    }

    @Test
    @DisplayName("crearCompeticion: debe crear una competición exitosamente")
    void testCrearCompeticion() {
        when(competicionMapper.toEntity(request)).thenReturn(competicion);
        when(competicionRepository.save(any(Competicion.class))).thenReturn(competicion);
        when(competicionMapper.toResponse(competicion)).thenReturn(response);

        CompeticionResponse result = competicionService.crearCompeticion(request);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Premier League");
        verify(competicionMapper).toEntity(request);
        verify(competicionRepository).save(competicion);
    }

    @Test
    @DisplayName("crearCompeticion: debe lanzar excepción cuando fechaFin es anterior a fechaInicio")
    void testCrearCompeticion_FechaFinInvalida() {
        CrearCompeticionRequest requestInvalido = new CrearCompeticionRequest(
                "Premier League",
                LocalDateTime.of(2025, 5, 25, 23, 59),
                LocalDateTime.of(2024, 8, 16, 0, 0)
        );

        assertThatThrownBy(() -> competicionService.crearCompeticion(requestInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fecha de fin no puede ser anterior");
    }

    @Test
    @DisplayName("obtenerCompeticionPorId: debe retornar cuando existe")
    void testObtenerCompeticionPorId_CuandoExiste() {
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
        when(competicionMapper.toResponse(competicion)).thenReturn(response);

        CompeticionResponse result = competicionService.obtenerCompeticionPorId(ID_COMPETICION);

        assertThat(result).isNotNull();
        verify(competicionRepository).findById(ID_COMPETICION);
    }

    @Test
    @DisplayName("obtenerCompeticionPorId: debe lanzar excepción cuando no existe")
    void testObtenerCompeticionPorId_CuandoNoExiste() {
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competicionService.obtenerCompeticionPorId(ID_COMPETICION))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("obtenerCompeticionesActivas: debe retornar competiciones activas")
    void testObtenerCompeticionesActivas() {
        when(competicionRepository.findActivas()).thenReturn(List.of(competicion));
        when(competicionMapper.toResponse(competicion)).thenReturn(response);

        List<CompeticionResponse> result = competicionService.obtenerCompeticionesActivas();

        assertThat(result).hasSize(1);
        verify(competicionRepository).findActivas();
    }

    @Test
    @DisplayName("eliminarCompeticion: debe lanzar excepción si está activa")
    void testEliminarCompeticion_Activa() {
    Competicion competicionMock = mock(Competicion.class);
    when(competicionMock.estaActiva()).thenReturn(true);
    
    when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicionMock));

    assertThatThrownBy(() -> competicionService.eliminarCompeticion(ID_COMPETICION))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No se puede eliminar una competición activa");
}
}*/