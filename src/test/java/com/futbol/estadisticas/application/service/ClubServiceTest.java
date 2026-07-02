package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.CrearClubRequest;
import com.futbol.estadisticas.application.port.dto.response.ClubResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.mapper.ClubMapper;
import com.futbol.estadisticas.application.port.mapper.JugadorMapper;
import com.futbol.estadisticas.application.port.out.ClubRepositoryPort;
import com.futbol.estadisticas.application.service.ClubService;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubServiceTest {

    @Mock
    private ClubRepositoryPort clubRepository;

    @Mock
    private ClubMapper clubMapper;

    @Mock
    private JugadorMapper jugadorMapper;

    @InjectMocks
    private ClubService clubService;

    private static final UUID ID_CLUB = UUID.fromString("44444444-5555-6666-7777-888888888888");
    private Club club;
    private ClubResponse response;
    private CrearClubRequest request;

    @BeforeEach
    void setUp() {
        request = new CrearClubRequest("Arsenal FC", "ARS", LocalDate.of(1886, 12, 1));

        club = Club.builder()
                .idEquipo(ID_CLUB)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .fechaFundacion(LocalDate.of(1886, 12, 1))
                .build();

        response = new ClubResponse(
                ID_CLUB, "Arsenal FC", "ARS", LocalDate.of(1886, 12, 1),
                0, 0, 0, 0.0, 0.0, null, null, null
        );
    }

    @Test
    @DisplayName("crearClub: debe crear un club exitosamente")
    void testCrearClub() {
        when(clubMapper.toEntity(request)).thenReturn(club);
        when(clubRepository.save(any(Club.class))).thenReturn(club);
        when(clubMapper.toResponse(club)).thenReturn(response);

        ClubResponse result = clubService.crearClub(request);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Arsenal FC");
        verify(clubMapper).toEntity(request);
        verify(clubRepository).save(club);
    }

    @Test
    @DisplayName("obtenerClubPorId: debe retornar el club cuando existe")
    void testObtenerClubPorId_CuandoExiste() {
        when(clubRepository.findById(ID_CLUB)).thenReturn(Optional.of(club));
        when(clubMapper.toResponse(club)).thenReturn(response);

        ClubResponse result = clubService.obtenerClubPorId(ID_CLUB);

        assertThat(result).isNotNull();
        assertThat(result.idEquipo()).isEqualTo(ID_CLUB);
        verify(clubRepository).findById(ID_CLUB);
    }

    @Test
    @DisplayName("obtenerClubPorId: debe lanzar excepción cuando no existe")
    void testObtenerClubPorId_CuandoNoExiste() {
        when(clubRepository.findById(ID_CLUB)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clubService.obtenerClubPorId(ID_CLUB))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Club no encontrado");
    }

    @Test
    @DisplayName("obtenerTodosLosClubs: debe retornar lista de clubs")
    void testObtenerTodosLosClubs() {
        when(clubRepository.findAll()).thenReturn(List.of(club));
        when(clubMapper.toResponse(club)).thenReturn(response);

        List<ClubResponse> result = clubService.obtenerTodosLosClubs();

        assertThat(result).hasSize(1);
        verify(clubRepository).findAll();
    }

    @Test
    @DisplayName("obtenerJugadoresActivosDeClub: debe retornar jugadores activos")
    void testObtenerJugadoresActivosDeClub() {
    // Given
    Jugador jugador = Jugador.builder()
            .idPersonal(UUID.randomUUID())
            .nombre("Bukayo")
            .apellido("Saka")
            .build();

    // Crear un contrato vigente para el jugador
    Contrato contrato = Contrato.builder()
            .idContrato(UUID.randomUUID())
            .personal(jugador)
            .club(club)
            .fechaInicio(LocalDateTime.now().minusMonths(6))
            .fechaFin(LocalDateTime.now().plusMonths(6))
            .estado(EstadoContrato.ACTIVO)
            .build();

    // Agregar el contrato al club (esto hace que getJugadoresActivos() lo detecte)
    club.agregarContrato(contrato);

    JugadorResponse jugadorResponse = JugadorResponse.builder()
            .idPersonal(jugador.getIdPersonal())
            .nombre("Bukayo")
            .apellido("Saka")
            .build();

    when(clubRepository.findById(ID_CLUB)).thenReturn(Optional.of(club));
    when(jugadorMapper.toResponse(jugador)).thenReturn(jugadorResponse);

    // When
    List<JugadorResponse> result = clubService.obtenerJugadoresActivosDeClub(ID_CLUB);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).nombre()).isEqualTo("Bukayo");
    verify(clubRepository).findById(ID_CLUB);
}

    @Test
    @DisplayName("eliminarClub: debe eliminar cuando existe")
    void testEliminarClub_CuandoExiste() {
        when(clubRepository.existsById(ID_CLUB)).thenReturn(true);
        doNothing().when(clubRepository).deleteById(ID_CLUB);

        clubService.eliminarClub(ID_CLUB);

        verify(clubRepository).deleteById(ID_CLUB);
    }

    @Test
    @DisplayName("eliminarClub: debe lanzar excepción cuando no existe")
    void testEliminarClub_CuandoNoExiste() {
        when(clubRepository.existsById(ID_CLUB)).thenReturn(false);

        assertThatThrownBy(() -> clubService.eliminarClub(ID_CLUB))
                .isInstanceOf(IllegalArgumentException.class);
    }
}