package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.CrearCompeticionRequest;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.mapper.CompeticionMapper;
import com.futbol.estadisticas.application.port.mapper.EquipoMapper;
import com.futbol.estadisticas.application.port.mapper.PartidoMapper;
import com.futbol.estadisticas.application.port.out.CompeticionRepositoryPort;
import com.futbol.estadisticas.application.port.out.EquipoRepositoryPort;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.enums.EstadoCompeticion;
import com.futbol.estadisticas.domain.model.enums.Temporada;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompeticionServiceTest {

    @Mock private EquipoRepositoryPort clubRepository;
    @Mock private CompeticionRepositoryPort competicionRepository;
    @Mock private PartidoRepositoryPort partidoRepository;
    @Mock private CompeticionMapper competicionMapper;
    @Mock private PartidoMapper partidoMapper;
    @Mock private EquipoMapper equipoMapper;

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
                Temporada.T2024_25,
                LocalDateTime.of(2024, 8, 16, 0, 0),
                LocalDateTime.of(2025, 5, 25, 23, 59)
        );

        competicion = Competicion.builder()
                .idCompeticion(ID_COMPETICION)
                .nombre("Premier League")
                .temporada(Temporada.T2024_25)
                .fechaInicio(LocalDateTime.of(2024, 8, 16, 0, 0))
                .fechaFin(LocalDateTime.of(2025, 5, 25, 23, 59))
                .estado(EstadoCompeticion.POR_INICIAR)
                .build();

        response = CompeticionResponse.builder()
                .idCompeticion(ID_COMPETICION)
                .nombre("Premier League")
                .nombreCompleto("Premier League 2024-25")
                .fechaInicio(LocalDateTime.of(2024, 8, 16, 0, 0))
                .fechaFin(LocalDateTime.of(2025, 5, 25, 23, 59))
                .estado(EstadoCompeticion.POR_INICIAR)
                .activa(false)
                .finalizada(false)
                .noHaComenzado(true)
                .totalPartidos(0)
                .partidosJugados(0)
                .partidosPendientes(0)
                .porcentajeAvance(0.0)
                .build();
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
    @DisplayName("crearCompeticion: lanza excepción si fechaFin es anterior a fechaInicio")
    void testCrearCompeticion_FechaFinInvalida() {
        CrearCompeticionRequest requestInvalido = new CrearCompeticionRequest(
                "Premier League",
                Temporada.T2024_25,
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
        when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of());
        when(competicionMapper.toResponse(competicion)).thenReturn(response);

        CompeticionResponse result = competicionService.obtenerCompeticionPorId(ID_COMPETICION);

        assertThat(result).isNotNull();
        verify(competicionRepository).findById(ID_COMPETICION);
    }

    @Test
    @DisplayName("obtenerCompeticionPorId: lanza excepción cuando no existe")
    void testObtenerCompeticionPorId_CuandoNoExiste() {
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competicionService.obtenerCompeticionPorId(ID_COMPETICION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Competición no encontrada");
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
    @DisplayName("eliminarCompeticion: elimina si existe")
    void testEliminarCompeticion() {
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));

        competicionService.eliminarCompeticion(ID_COMPETICION);

        verify(competicionRepository).deleteById(ID_COMPETICION);
    }

    @Test
    @DisplayName("eliminarCompeticion: lanza excepción si no existe")
    void testEliminarCompeticion_NoExiste() {
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competicionService.eliminarCompeticion(ID_COMPETICION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Competición no encontrada");
    }
}