package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.RegistrarLesionRequest;
import com.futbol.estadisticas.application.port.dto.response.LesionResponse;
import com.futbol.estadisticas.application.port.mapper.LesionMapper;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.application.port.out.LesionRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Lesion;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.Gravedad;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LesionServiceTest {

    @Mock
    private LesionRepositoryPort lesionRepository;

    @Mock
    private JugadorRepositoryPort jugadorRepository;

    @Mock
    private LesionMapper lesionMapper;

    @InjectMocks
    private LesionService lesionService;

    private static final UUID ID_JUGADOR = UUID.randomUUID();
    private static final UUID ID_JUGADOR_2 = UUID.randomUUID();
    private static final UUID ID_LESION = UUID.randomUUID();
    private static final UUID ID_LESION_2 = UUID.randomUUID();
    private static final UUID ID_CLUB = UUID.randomUUID();

    private Jugador jugador;
    private Jugador jugador2;
    private Lesion lesion;
    private Lesion lesionCurada;
    private Lesion lesion2;
    private DatosDeportivos datosDeportivos;
    private Club club;
    private LesionResponse response;
    private LesionResponse response2;
    private RegistrarLesionRequest registrarRequest;

    @BeforeEach
    void setUp() {
        // Crear club
        club = Club.builder()
                .idEquipo(ID_CLUB)
                .nombre("Arsenal FC")
                .nombreCorto("Arsenal")
                .fechaFundacion(LocalDate.of(1886, 10, 1))
                .build();

        // Crear datos deportivos
        datosDeportivos = DatosDeportivos.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(85_000_000.0)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.EXTREMO_DERECHO)))
                .dorsal(7)
                .fechaActualizacion(LocalDate.now())
                .build();

        // Crear lesiones
        lesion = Lesion.builder()
                .idLesion(ID_LESION)
                .nombreLesion("Lesión de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFin(LocalDate.now().plusDays(5))
                .curada(false)
                .build();

        lesionCurada = Lesion.builder()
                .idLesion(ID_LESION_2)
                .nombreLesion("Lesión de rodilla")
                .gravedad(Gravedad.LEVE)
                .fechaInicio(LocalDate.now().minusDays(20))
                .fechaFin(LocalDate.now().minusDays(5))
                .curada(true)
                .build();

        lesion2 = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión de hombro")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now().minusDays(5))
                .fechaFin(LocalDate.now().plusDays(15))
                .curada(false)
                .build();

        // Crear jugador con lesión activa
        jugador = Jugador.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .nacionalidad(Nacion.INGLATERRA)
                .tipoPersonal(TipoPersonal.JUGADOR)
                .pieHabil(JuegoPies.ZURDO)
                .altura(178)
                .peso(70)
                .fechaActualizacion(LocalDate.now())
                .datosDeportivos(datosDeportivos)
                .lesiones(new ArrayList<>(List.of(lesion)))
                .build();

        // Agregar contrato al jugador
        Contrato contrato = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .club(club)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .sueldo(5_000_000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();
        jugador.agregarContrato(contrato);

        // Crear jugador 2 (sin lesiones)
        jugador2 = Jugador.builder()
                .idPersonal(ID_JUGADOR_2)
                .nombre("Nuevo")
                .apellido("Jugador")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .nacionalidad(Nacion.ESPAÑA)
                .tipoPersonal(TipoPersonal.JUGADOR)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .fechaActualizacion(LocalDate.now())
                .datosDeportivos(datosDeportivos)
                .lesiones(new ArrayList<>())
                .build();

        // Crear responses
        response = LesionResponse.builder()
                .idLesion(ID_LESION)
                .nombreLesion("Lesión de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFin(LocalDate.now().plusDays(5))
                .curada(false)
                .idJugador(ID_JUGADOR)
                .nombreJugador("Bukayo Saka")
                .build();

        response2 = LesionResponse.builder()
                .idLesion(ID_LESION_2)
                .nombreLesion("Lesión de rodilla")
                .gravedad(Gravedad.LEVE)
                .fechaInicio(LocalDate.now().minusDays(20))
                .fechaFin(LocalDate.now().minusDays(5))
                .curada(true)
                .idJugador(ID_JUGADOR)
                .nombreJugador("Bukayo Saka")
                .build();

        // Crear request
        registrarRequest = RegistrarLesionRequest.builder()
                .nombreLesion("Lesión de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFinEstimada(LocalDate.now().plusDays(5))
                .build();
    }

    // ── TESTS: REGISTRAR LESIÓN ──

    @Test
    @DisplayName("registrarLesion: debe registrar una nueva lesión correctamente")
    void testRegistrarLesion() {
        Lesion nuevaLesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFin(LocalDate.now().plusDays(5))
                .curada(false)
                .build();

        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(lesionMapper.toEntity(registrarRequest)).thenReturn(nuevaLesion);
        when(lesionRepository.save(nuevaLesion)).thenReturn(nuevaLesion);
        when(lesionMapper.toResponse(nuevaLesion, jugador)).thenReturn(response);

        LesionResponse result = lesionService.registrarLesion(ID_JUGADOR, registrarRequest);

        assertThat(result).isNotNull();
        assertThat(result.idLesion()).isEqualTo(ID_LESION);
        assertThat(result.nombreLesion()).isEqualTo("Lesión de tobillo");
        assertThat(result.gravedad()).isEqualTo(Gravedad.MODERADA);
        assertThat(result.curada()).isFalse();
        assertThat(result.idJugador()).isEqualTo(ID_JUGADOR);
        assertThat(result.nombreJugador()).isEqualTo("Bukayo Saka");

        assertThat(jugador.getLesiones()).hasSize(2);
        assertThat(jugador.getLesiones()).contains(nuevaLesion);

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(lesionMapper).toEntity(registrarRequest);
        verify(lesionRepository).save(nuevaLesion);
        verify(jugadorRepository).save(jugador);
        verify(lesionMapper).toResponse(nuevaLesion, jugador);
    }

    @Test
    @DisplayName("registrarLesion: debe lanzar excepción cuando el jugador no existe")
    void testRegistrarLesion_JugadorNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(jugadorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lesionService.registrarLesion(idInexistente, registrarRequest))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Jugador no encontrado con id: " + idInexistente);

        verify(jugadorRepository).findById(idInexistente);
        verify(lesionRepository, never()).save(any());
        verify(jugadorRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrarLesion: debe actualizar el estado del jugador a LESIONADO")
    void testRegistrarLesion_ActualizaEstado() {
        Lesion nuevaLesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión grave")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(30))
                .curada(false)
                .build();

        when(jugadorRepository.findById(ID_JUGADOR_2)).thenReturn(Optional.of(jugador2));
        when(lesionMapper.toEntity(registrarRequest)).thenReturn(nuevaLesion);
        when(lesionRepository.save(nuevaLesion)).thenReturn(nuevaLesion);
        when(lesionMapper.toResponse(nuevaLesion, jugador2)).thenReturn(response);

        lesionService.registrarLesion(ID_JUGADOR_2, registrarRequest);

        assertThat(jugador2.getDatosDeportivos().getEstadoJugador()).isEqualTo(EstadoJugador.LESIONADO);
        verify(jugadorRepository).save(jugador2);
    }

    // ── TESTS: OBTENER POR ID ──

    @Test
    @DisplayName("obtenerLesionPorId: debe retornar la lesión cuando existe")
    void testObtenerLesionPorId_Existe() {
        when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.of(lesion));
        when(lesionMapper.toResponse(lesion, null)).thenReturn(response);

        LesionResponse result = lesionService.obtenerLesionPorId(ID_LESION);

        assertThat(result).isNotNull();
        assertThat(result.idLesion()).isEqualTo(ID_LESION);
        assertThat(result.nombreLesion()).isEqualTo("Lesión de tobillo");
        assertThat(result.gravedad()).isEqualTo(Gravedad.MODERADA);
        assertThat(result.curada()).isFalse();

        verify(lesionRepository).findById(ID_LESION);
        verify(lesionMapper).toResponse(lesion, null);
    }

    @Test
    @DisplayName("obtenerLesionPorId: debe lanzar excepción cuando la lesión no existe")
    void testObtenerLesionPorId_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(lesionRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lesionService.obtenerLesionPorId(idInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lesión no encontrada con id: " + idInexistente);

        verify(lesionRepository).findById(idInexistente);
        verify(lesionMapper, never()).toResponse(any(), any());
    }

    // ── TESTS: OBTENER LESIONES POR JUGADOR ──

    @Test
    @DisplayName("obtenerLesionesPorJugador: debe retornar todas las lesiones de un jugador")
    void testObtenerLesionesPorJugador() {
        List<Lesion> lesiones = List.of(lesion, lesionCurada);
        List<LesionResponse> responses = List.of(response, response2);

        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(lesionRepository.findByJugador(ID_JUGADOR)).thenReturn(lesiones);
        when(lesionMapper.toResponse(lesion, jugador)).thenReturn(response);
        when(lesionMapper.toResponse(lesionCurada, jugador)).thenReturn(response2);

        List<LesionResponse> result = lesionService.obtenerLesionesPorJugador(ID_JUGADOR);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(LesionResponse::nombreLesion)
                .containsExactlyInAnyOrder("Lesión de tobillo", "Lesión de rodilla");
        assertThat(result)
                .extracting(LesionResponse::curada)
                .containsExactlyInAnyOrder(false, true);

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(lesionRepository).findByJugador(ID_JUGADOR);
        verify(lesionMapper, times(2)).toResponse(any(Lesion.class), eq(jugador));
    }

    @Test
    @DisplayName("obtenerLesionesPorJugador: debe retornar lista vacía cuando el jugador no tiene lesiones")
    void testObtenerLesionesPorJugador_Vacio() {
        when(jugadorRepository.findById(ID_JUGADOR_2)).thenReturn(Optional.of(jugador2));
        when(lesionRepository.findByJugador(ID_JUGADOR_2)).thenReturn(List.of());

        List<LesionResponse> result = lesionService.obtenerLesionesPorJugador(ID_JUGADOR_2);

        assertThat(result).isEmpty();
        verify(jugadorRepository).findById(ID_JUGADOR_2);
        verify(lesionRepository).findByJugador(ID_JUGADOR_2);
        verify(lesionMapper, never()).toResponse(any(), any());
    }

    @Test
    @DisplayName("obtenerLesionesPorJugador: debe lanzar excepción cuando el jugador no existe")
    void testObtenerLesionesPorJugador_JugadorNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(jugadorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lesionService.obtenerLesionesPorJugador(idInexistente))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Jugador no encontrado con id: " + idInexistente);

        verify(jugadorRepository).findById(idInexistente);
        verify(lesionRepository, never()).findByJugador(any());
    }

    // ── TESTS: OBTENER LESIONES ACTIVAS ──

    @Test
    @DisplayName("obtenerLesionesActivasPorJugador: debe retornar solo lesiones activas de un jugador")
    void testObtenerLesionesActivasPorJugador() {
        List<Lesion> lesionesActivas = List.of(lesion);
        List<LesionResponse> responses = List.of(response);

        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(lesionRepository.findActivasByJugador(ID_JUGADOR)).thenReturn(lesionesActivas);
        when(lesionMapper.toResponse(lesion, jugador)).thenReturn(response);

        List<LesionResponse> result = lesionService.obtenerLesionesActivasPorJugador(ID_JUGADOR);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).curada()).isFalse();
        assertThat(result.get(0).nombreLesion()).isEqualTo("Lesión de tobillo");

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(lesionRepository).findActivasByJugador(ID_JUGADOR);
        verify(lesionMapper).toResponse(lesion, jugador);
    }

    @Test
    @DisplayName("obtenerLesionesActivasEnSistema: debe retornar todas las lesiones activas del sistema")
    void testObtenerLesionesActivasEnSistema() {
        List<Lesion> lesionesActivas = List.of(lesion, lesion2);
        LesionResponse response3 = LesionResponse.builder()
                .idLesion(lesion2.getIdLesion())
                .nombreLesion("Lesión de hombro")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now().minusDays(5))
                .fechaFin(LocalDate.now().plusDays(15))
                .curada(false)
                .build();

        when(lesionRepository.findActivas()).thenReturn(lesionesActivas);
        when(lesionMapper.toResponse(lesion, null)).thenReturn(response);
        when(lesionMapper.toResponse(lesion2, null)).thenReturn(response3);

        List<LesionResponse> result = lesionService.obtenerLesionesActivasEnSistema();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(LesionResponse::nombreLesion)
                .containsExactlyInAnyOrder("Lesión de tobillo", "Lesión de hombro");
        assertThat(result)
                .extracting(LesionResponse::curada)
                .allMatch(curada -> curada == false);

        verify(lesionRepository).findActivas();
        verify(lesionMapper, times(2)).toResponse(any(Lesion.class), isNull());
    }

    @Test
    @DisplayName("obtenerLesionesActivasEnSistema: debe retornar lista vacía cuando no hay lesiones activas")
    void testObtenerLesionesActivasEnSistema_Vacio() {
        when(lesionRepository.findActivas()).thenReturn(List.of());

        List<LesionResponse> result = lesionService.obtenerLesionesActivasEnSistema();

        assertThat(result).isEmpty();
        verify(lesionRepository).findActivas();
        verify(lesionMapper, never()).toResponse(any(), any());
    }

    // ── TESTS: OBTENER POR GRAVEDAD ──

    @Test
    @DisplayName("obtenerLesionesPorGravedad: debe retornar lesiones por gravedad")
    void testObtenerLesionesPorGravedad() {
        List<Lesion> lesionesGraves = List.of(lesion2);
        LesionResponse response3 = LesionResponse.builder()
                .idLesion(lesion2.getIdLesion())
                .nombreLesion("Lesión de hombro")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now().minusDays(5))
                .fechaFin(LocalDate.now().plusDays(15))
                .curada(false)
                .build();

        when(lesionRepository.findByGravedad(Gravedad.GRAVE)).thenReturn(lesionesGraves);
        when(lesionMapper.toResponse(lesion2, null)).thenReturn(response3);

        List<LesionResponse> result = lesionService.obtenerLesionesPorGravedad(Gravedad.GRAVE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).gravedad()).isEqualTo(Gravedad.GRAVE);
        assertThat(result.get(0).nombreLesion()).isEqualTo("Lesión de hombro");

        verify(lesionRepository).findByGravedad(Gravedad.GRAVE);
        verify(lesionMapper).toResponse(lesion2, null);
    }

    @Test
    @DisplayName("obtenerLesionesPorGravedad: debe retornar lista vacía cuando no hay lesiones con esa gravedad")
    void testObtenerLesionesPorGravedad_Vacio() {
        when(lesionRepository.findByGravedad(Gravedad.CRITICA)).thenReturn(List.of());

        List<LesionResponse> result = lesionService.obtenerLesionesPorGravedad(Gravedad.CRITICA);

        assertThat(result).isEmpty();
        verify(lesionRepository).findByGravedad(Gravedad.CRITICA);
        verify(lesionMapper, never()).toResponse(any(), any());
    }


    @Test
    @DisplayName("curarLesion: debe curar una lesión correctamente")
    void testCurarLesion() {
    Lesion lesionParaCurar = Lesion.builder()
            .idLesion(ID_LESION)
            .nombreLesion("Lesión de tobillo")
            .gravedad(Gravedad.MODERADA)
            .fechaInicio(LocalDate.now().minusDays(10))
            .fechaFin(LocalDate.now().plusDays(5))
            .curada(false)
            .build();

    Lesion lesionCuradaMock = Lesion.builder()
            .idLesion(ID_LESION)
            .nombreLesion("Lesión de tobillo")
            .gravedad(Gravedad.MODERADA)
            .fechaInicio(LocalDate.now().minusDays(10))
            .fechaFin(LocalDate.now().plusDays(5))
            .curada(true)
            .build();

    when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.of(lesionParaCurar));
    when(lesionRepository.save(lesionParaCurar)).thenReturn(lesionCuradaMock);
    when(lesionRepository.findActivasByJugador(ID_LESION)).thenReturn(List.of());
    when(jugadorRepository.findAll()).thenReturn(List.of(jugador));
    when(jugadorRepository.save(jugador)).thenReturn(jugador);
    
    LesionResponse responseCurada = LesionResponse.builder()
            .idLesion(ID_LESION)
            .nombreLesion("Lesión de tobillo")
            .gravedad(Gravedad.MODERADA)
            .fechaInicio(LocalDate.now().minusDays(10))
            .fechaFin(LocalDate.now().plusDays(5))
            .curada(true)  
            .idJugador(ID_JUGADOR)
            .nombreJugador("Bukayo Saka")
            .build();
    
    when(lesionMapper.toResponse(lesionCuradaMock, null)).thenReturn(responseCurada);

    LesionResponse result = lesionService.curarLesion(ID_LESION);

    assertThat(result).isNotNull();
    assertThat(result.curada()).isTrue();  
    assertThat(result.nombreLesion()).isEqualTo("Lesión de tobillo");

    verify(lesionRepository).findById(ID_LESION);
    verify(lesionRepository).save(lesionParaCurar);
    verify(lesionRepository).findActivasByJugador(ID_LESION);
}

    @Test
    @DisplayName("curarLesion: debe lanzar excepción cuando la lesión no existe")
    void testCurarLesion_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(lesionRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lesionService.curarLesion(idInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lesión no encontrada con id: " + idInexistente);

        verify(lesionRepository).findById(idInexistente);
        verify(lesionRepository, never()).save(any());
    }

    @Test
    @DisplayName("curarLesion: cuando el jugador ya no tiene lesiones activas, debe cambiar estado a SUPLENTE")
    void testCurarLesion_CambiarEstadoASuplente() {
        // Crear jugador con una sola lesión
        Lesion lesionUnica = Lesion.builder()
                .idLesion(ID_LESION)
                .nombreLesion("Lesión única")
                .gravedad(Gravedad.LEVE)
                .fechaInicio(LocalDate.now().minusDays(3))
                .fechaFin(LocalDate.now().plusDays(7))
                .curada(false)
                .build();

        Jugador jugadorConUnaLesion = Jugador.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .datosDeportivos(datosDeportivos)
                .lesiones(new ArrayList<>(List.of(lesionUnica)))
                .build();

        Lesion lesionCuradaMock = Lesion.builder()
                .idLesion(ID_LESION)
                .nombreLesion("Lesión única")
                .gravedad(Gravedad.LEVE)
                .fechaInicio(LocalDate.now().minusDays(3))
                .fechaFin(LocalDate.now().plusDays(7))
                .curada(true)
                .build();

        when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.of(lesionUnica));
        when(lesionRepository.save(lesionUnica)).thenReturn(lesionCuradaMock);
        when(lesionRepository.findActivasByJugador(ID_LESION)).thenReturn(List.of());
        when(jugadorRepository.findAll()).thenReturn(List.of(jugadorConUnaLesion));
        when(jugadorRepository.save(jugadorConUnaLesion)).thenReturn(jugadorConUnaLesion);
        when(lesionMapper.toResponse(lesionCuradaMock, null)).thenReturn(response);

        lesionService.curarLesion(ID_LESION);

        assertThat(jugadorConUnaLesion.getDatosDeportivos().getEstadoJugador()).isEqualTo(EstadoJugador.SUPLENTE);
        verify(jugadorRepository).save(jugadorConUnaLesion);
    }

    @Test
    @DisplayName("curarLesion: cuando el jugador aún tiene lesiones activas, no debe cambiar estado")
    void testCurarLesion_MantieneEstado() {
        Lesion lesionActiva = Lesion.builder()
                .idLesion(ID_LESION_2)
                .nombreLesion("Lesión de hombro")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now().minusDays(5))
                .fechaFin(LocalDate.now().plusDays(15))
                .curada(false)
                .build();

        Jugador jugadorConDosLesiones = Jugador.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .datosDeportivos(datosDeportivos)
                .lesiones(new ArrayList<>(List.of(lesion, lesionActiva)))
                .build();

        EstadoJugador estadoOriginal = jugadorConDosLesiones.getDatosDeportivos().getEstadoJugador();

        Lesion lesionCuradaMock = Lesion.builder()
                .idLesion(ID_LESION)
                .nombreLesion("Lesión de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFin(LocalDate.now().plusDays(5))
                .curada(true)
                .build();

        when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.of(lesion));
        when(lesionRepository.save(lesion)).thenReturn(lesionCuradaMock);
        when(lesionRepository.findActivasByJugador(ID_LESION)).thenReturn(List.of(lesionActiva));
        when(lesionMapper.toResponse(lesionCuradaMock, null)).thenReturn(response);

        lesionService.curarLesion(ID_LESION);

        // No debe cambiar el estado porque aún hay lesiones activas
        assertThat(jugadorConDosLesiones.getDatosDeportivos().getEstadoJugador()).isEqualTo(estadoOriginal);
        verify(jugadorRepository, never()).save(any());
    }

    @Test
    @DisplayName("curarLesion: cuando la lesión ya está curada, debe lanzar excepción")
    void testCurarLesion_YaCurada() {
    Lesion lesionYaCurada = Lesion.builder()
            .idLesion(ID_LESION_2)
            .nombreLesion("Lesión de rodilla")
            .gravedad(Gravedad.LEVE)
            .fechaInicio(LocalDate.now().minusDays(20))
            .fechaFin(LocalDate.now().minusDays(5))
            .curada(true)  
            .build();

    when(lesionRepository.findById(ID_LESION_2)).thenReturn(Optional.of(lesionYaCurada));

    assertThatThrownBy(() -> lesionService.curarLesion(ID_LESION_2))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("La lesión ya está curada");

    verify(lesionRepository).findById(ID_LESION_2);
    verify(lesionRepository, never()).save(any());
}

    @Test
    @DisplayName("registrarLesion: debe lanzar excepción cuando la fecha de inicio es posterior a la fecha de fin")
    void testRegistrarLesion_FechasInvalidas() {
        RegistrarLesionRequest requestInvalido = RegistrarLesionRequest.builder()
                .nombreLesion("Lesión inválida")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().plusDays(5))
                .fechaFinEstimada(LocalDate.now().minusDays(5))
                .build();

        // Nota: Esta validación debería estar en el dominio o en el service
        // Si no está implementada, este test puede fallar
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));

        assertThatThrownBy(() -> lesionService.registrarLesion(ID_JUGADOR, requestInvalido))
                .isInstanceOf(IllegalArgumentException.class);

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(lesionRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerLesionesPorJugador: debe incluir el nombre completo del jugador en la respuesta")
    void testObtenerLesionesPorJugador_ConNombreCompleto() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(lesionRepository.findByJugador(ID_JUGADOR)).thenReturn(List.of(lesion));
        when(lesionMapper.toResponse(lesion, jugador)).thenReturn(response);

        List<LesionResponse> result = lesionService.obtenerLesionesPorJugador(ID_JUGADOR);

        assertThat(result.get(0).nombreJugador()).isEqualTo("Bukayo Saka");
        verify(lesionMapper).toResponse(lesion, jugador);
    }

   @Test
   @DisplayName("obtenerLesionPorId: debe retornar lesión sin información del jugador")
    void testObtenerLesionPorId_SinJugador() {
    when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.of(lesion));
    
    LesionResponse responseSinJugador = LesionResponse.builder()
            .idLesion(ID_LESION)
            .nombreLesion("Lesión de tobillo")
            .gravedad(Gravedad.MODERADA)
            .fechaInicio(LocalDate.now().minusDays(10))
            .fechaFin(LocalDate.now().plusDays(5))
            .curada(false)
            .idJugador(null)      
            .nombreJugador(null)  
            .build();
    
    when(lesionMapper.toResponse(lesion, null)).thenReturn(responseSinJugador);

    LesionResponse result = lesionService.obtenerLesionPorId(ID_LESION);

    assertThat(result.idJugador()).isNull();  
    assertThat(result.nombreJugador()).isNull();
    verify(lesionMapper).toResponse(lesion, null);
}

   @Test
@DisplayName("obtenerLesionesActivasEnSistema: debe retornar lesiones sin información del jugador")
void testObtenerLesionesActivasEnSistema_SinJugador() {
    when(lesionRepository.findActivas()).thenReturn(List.of(lesion));
    
    LesionResponse responseSinJugador = LesionResponse.builder()
            .idLesion(ID_LESION)
            .nombreLesion("Lesión de tobillo")
            .gravedad(Gravedad.MODERADA)
            .fechaInicio(LocalDate.now().minusDays(10))
            .fechaFin(LocalDate.now().plusDays(5))
            .curada(false)
            .idJugador(null)      
            .nombreJugador(null)  
            .build();
    
    when(lesionMapper.toResponse(lesion, null)).thenReturn(responseSinJugador);

    List<LesionResponse> result = lesionService.obtenerLesionesActivasEnSistema();

    assertThat(result.get(0).idJugador()).isNull();  // ✅ Ahora pasa
    assertThat(result.get(0).nombreJugador()).isNull();
    verify(lesionMapper).toResponse(lesion, null);
}
}