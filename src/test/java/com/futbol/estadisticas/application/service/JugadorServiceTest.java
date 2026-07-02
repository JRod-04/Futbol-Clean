package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.ActualizarJugadorRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearJugadorRequest;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.mapper.JugadorMapper;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JugadorServiceTest {

    @Mock
    private JugadorRepositoryPort jugadorRepository;

    @Mock
    private JugadorMapper jugadorMapper;

    @InjectMocks
    private JugadorService jugadorService;

    private static final UUID ID_JUGADOR = UUID.randomUUID();
    private static final UUID ID_JUGADOR_2 = UUID.randomUUID();
    private static final UUID ID_CLUB = UUID.randomUUID();
    private static final UUID ID_CLUB_2 = UUID.randomUUID();

    private Jugador jugador;
    private Jugador jugador2;
    private DatosDeportivos datosDeportivos;
    private Club club;
    private Club club2;
    private Lesion lesion;
    private Lesion lesion2;
    private JugadorResponse response;
    private JugadorResponse response2;
    private CrearJugadorRequest crearRequest;
    private ActualizarJugadorRequest actualizarRequest;

    @BeforeEach
    void setUp() {
        // Crear clubes
        club = Club.builder()
                .idEquipo(ID_CLUB)
                .nombre("Arsenal FC")
                .nombreCorto("Arsenal")
                .fechaFundacion(LocalDate.of(1886, 10, 1))
                .build();

        club2 = Club.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("FC Barcelona")
                .nombreCorto("Barça")
                .fechaFundacion(LocalDate.of(1899, 11, 29))
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
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFin(LocalDate.now().plusDays(5))
                .curada(false)
                .build();

        lesion2 = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión de rodilla")
                .gravedad(Gravedad.LEVE)
                .fechaInicio(LocalDate.now().minusDays(20))
                .fechaFin(LocalDate.now().minusDays(5))
                .curada(true)
                .build();

        // Crear jugador
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

        // Crear jugador 2 (sin datos deportivos y sin lesiones)
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
                .lesiones(new ArrayList<>())
                .build();

        // Crear response
        response = JugadorResponse.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .nombreCompleto("Bukayo Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .edad(23)
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(178)
                .peso(70)
                .posicion(PosicionJugador.EXTREMO_DERECHO)
                .dorsal(7)
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(85_000_000.0)
                .valorMercadoEnMillones(85.0)
                .clubActual("Arsenal FC")
                .idClubActual(ID_CLUB)
                .disponible(true)
                .lesionesActivas(1)
                .build();

        response2 = JugadorResponse.builder()
                .idPersonal(ID_JUGADOR_2)
                .nombre("Nuevo")
                .apellido("Jugador")
                .nombreCompleto("Nuevo Jugador")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .edad(24)
                .nacionalidad(Nacion.ESPAÑA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .disponible(false)
                .lesionesActivas(0)
                .build();

        // Crear requests
        crearRequest = CrearJugadorRequest.builder()
                .nombre("Nuevo")
                .apellido("Jugador")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .nacionalidad(Nacion.ESPAÑA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .build();

        actualizarRequest = ActualizarJugadorRequest.builder()
                .nombre("Bukayo Actualizado")
                .apellido("Saka Actualizado")
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .dorsal(10)
                .posicion(PosicionJugador.DELANTERO)
                .valorMercado(100_000_000.0)
                .build();
    }


    @Test
    @DisplayName("crearJugador: debe crear un nuevo jugador correctamente")
    void testCrearJugador() {
        Jugador nuevoJugador = Jugador.builder()
                .idPersonal(UUID.randomUUID())
                .nombre("Nuevo")
                .apellido("Jugador")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .nacionalidad(Nacion.ESPAÑA)
                .tipoPersonal(TipoPersonal.JUGADOR)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .fechaActualizacion(LocalDate.now())
                .lesiones(new ArrayList<>())
                .build();

        when(jugadorMapper.toEntity(crearRequest)).thenReturn(nuevoJugador);
        when(jugadorRepository.save(nuevoJugador)).thenReturn(nuevoJugador);
        when(jugadorMapper.toResponse(nuevoJugador)).thenReturn(response2);

        JugadorResponse result = jugadorService.crearJugador(crearRequest);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Nuevo");
        assertThat(result.apellido()).isEqualTo("Jugador");
        assertThat(result.lesionesActivas()).isEqualTo(0);

        verify(jugadorMapper).toEntity(crearRequest);
        verify(jugadorRepository).save(nuevoJugador);
        verify(jugadorMapper).toResponse(nuevoJugador);
    }

    // ── TESTS: OBTENER POR ID ──

    @Test
    @DisplayName("obtenerJugadorPorId: debe retornar el jugador cuando existe")
    void testObtenerJugadorPorId_Existe() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        JugadorResponse result = jugadorService.obtenerJugadorPorId(ID_JUGADOR);

        assertThat(result).isNotNull();
        assertThat(result.idPersonal()).isEqualTo(ID_JUGADOR);
        assertThat(result.nombre()).isEqualTo("Bukayo");
        assertThat(result.apellido()).isEqualTo("Saka");
        assertThat(result.nombreCompleto()).isEqualTo("Bukayo Saka");
        assertThat(result.posicion()).isEqualTo(PosicionJugador.EXTREMO_DERECHO);
        assertThat(result.dorsal()).isEqualTo(7);
        assertThat(result.estadoJugador()).isEqualTo(EstadoJugador.TITULAR);
        assertThat(result.clubActual()).isEqualTo("Arsenal FC");
        assertThat(result.idClubActual()).isEqualTo(ID_CLUB);
        assertThat(result.lesionesActivas()).isEqualTo(1);

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(jugadorMapper).toResponse(jugador);
    }

    @Test
    @DisplayName("obtenerJugadorPorId: debe lanzar excepción cuando el jugador no existe")
    void testObtenerJugadorPorId_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(jugadorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jugadorService.obtenerJugadorPorId(idInexistente))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Jugador no encontrado con id: " + idInexistente);

        verify(jugadorRepository).findById(idInexistente);
        verify(jugadorMapper, never()).toResponse(any());
    }

    // ── TESTS: OBTENER TODOS ──

    @Test
    @DisplayName("obtenerTodosLosJugadores: debe retornar todos los jugadores")
    void testObtenerTodosLosJugadores() {
        List<Jugador> jugadores = List.of(jugador);
        List<JugadorResponse> responses = List.of(response);

        when(jugadorRepository.findAll()).thenReturn(jugadores);
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        List<JugadorResponse> result = jugadorService.obtenerTodosLosJugadores();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Bukayo");
        assertThat(result.get(0).apellido()).isEqualTo("Saka");

        verify(jugadorRepository).findAll();
        verify(jugadorMapper).toResponse(jugador);
    }

    @Test
    @DisplayName("obtenerTodosLosJugadores: debe retornar lista vacía cuando no hay jugadores")
    void testObtenerTodosLosJugadores_Vacio() {
        when(jugadorRepository.findAll()).thenReturn(List.of());

        List<JugadorResponse> result = jugadorService.obtenerTodosLosJugadores();

        assertThat(result).isEmpty();
        verify(jugadorRepository).findAll();
        verify(jugadorMapper, never()).toResponse(any());
    }

    // ── TESTS: OBTENER POR CLUB ──

    @Test
    @DisplayName("obtenerJugadoresPorClub: debe retornar jugadores de un club")
    void testObtenerJugadoresPorClub() {
        List<Jugador> jugadores = List.of(jugador);
        List<JugadorResponse> responses = List.of(response);

        when(jugadorRepository.findByClub(ID_CLUB)).thenReturn(jugadores);
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        List<JugadorResponse> result = jugadorService.obtenerJugadoresPorClub(ID_CLUB);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).clubActual()).isEqualTo("Arsenal FC");
        assertThat(result.get(0).idClubActual()).isEqualTo(ID_CLUB);

        verify(jugadorRepository).findByClub(ID_CLUB);
        verify(jugadorMapper).toResponse(jugador);
    }

    @Test
    @DisplayName("obtenerJugadoresPorClub: debe retornar lista vacía cuando el club no tiene jugadores")
    void testObtenerJugadoresPorClub_Vacio() {
        UUID clubSinJugadores = UUID.randomUUID();
        when(jugadorRepository.findByClub(clubSinJugadores)).thenReturn(List.of());

        List<JugadorResponse> result = jugadorService.obtenerJugadoresPorClub(clubSinJugadores);

        assertThat(result).isEmpty();
        verify(jugadorRepository).findByClub(clubSinJugadores);
        verify(jugadorMapper, never()).toResponse(any());
    }

    // ── TESTS: OBTENER POR POSICIÓN ──

    @Test
    @DisplayName("obtenerJugadoresPorPosicion: debe retornar jugadores por posición")
    void testObtenerJugadoresPorPosicion() {
        List<Jugador> jugadores = List.of(jugador);
        List<JugadorResponse> responses = List.of(response);

        when(jugadorRepository.findByPosicion(PosicionJugador.EXTREMO_DERECHO)).thenReturn(jugadores);
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        List<JugadorResponse> result = jugadorService.obtenerJugadoresPorPosicion(PosicionJugador.EXTREMO_DERECHO);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).posicion()).isEqualTo(PosicionJugador.EXTREMO_DERECHO);

        verify(jugadorRepository).findByPosicion(PosicionJugador.EXTREMO_DERECHO);
        verify(jugadorMapper).toResponse(jugador);
    }

    @Test
    @DisplayName("obtenerJugadoresPorPosicion: debe retornar lista vacía cuando no hay jugadores en esa posición")
    void testObtenerJugadoresPorPosicion_Vacio() {
        when(jugadorRepository.findByPosicion(PosicionJugador.PORTERO)).thenReturn(List.of());

        List<JugadorResponse> result = jugadorService.obtenerJugadoresPorPosicion(PosicionJugador.PORTERO);

        assertThat(result).isEmpty();
        verify(jugadorRepository).findByPosicion(PosicionJugador.PORTERO);
        verify(jugadorMapper, never()).toResponse(any());
    }

    // ── TESTS: OBTENER DISPONIBLES ──

    @Test
    @DisplayName("obtenerJugadoresDisponibles: debe retornar jugadores disponibles")
    void testObtenerJugadoresDisponibles() {
        List<Jugador> jugadores = List.of(jugador);
        List<JugadorResponse> responses = List.of(response);

        when(jugadorRepository.findDisponibles()).thenReturn(jugadores);
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        List<JugadorResponse> result = jugadorService.obtenerJugadoresDisponibles();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).disponible()).isTrue();

        verify(jugadorRepository).findDisponibles();
        verify(jugadorMapper).toResponse(jugador);
    }


    @Test
    @DisplayName("obtenerJugadoresLesionados: debe retornar jugadores lesionados")
    void testObtenerJugadoresLesionados() {
        DatosDeportivos datosLesionado = DatosDeportivos.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .estadoJugador(EstadoJugador.LESIONADO)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.CENTRAL)))
                .dorsal(3)
                .fechaActualizacion(LocalDate.now())
                .build();

        Lesion lesionActiva = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión de rodilla")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now().minusDays(5))
                .fechaFin(LocalDate.now().plusDays(10))
                .curada(false)
                .build();

        Jugador jugadorLesionado = Jugador.builder()
                .idPersonal(UUID.randomUUID())
                .nombre("Gerard")
                .apellido("Piqué")
                .datosDeportivos(datosLesionado)
                .lesiones(new ArrayList<>(List.of(lesionActiva)))
                .build();

        JugadorResponse responseLesionado = JugadorResponse.builder()
                .idPersonal(jugadorLesionado.getIdPersonal())
                .nombre("Gerard")
                .apellido("Piqué")
                .nombreCompleto("Gerard Piqué")
                .estadoJugador(EstadoJugador.LESIONADO)
                .disponible(false)
                .lesionesActivas(1)
                .build();

        when(jugadorRepository.findLesionados()).thenReturn(List.of(jugadorLesionado));
        when(jugadorMapper.toResponse(jugadorLesionado)).thenReturn(responseLesionado);

        List<JugadorResponse> result = jugadorService.obtenerJugadoresLesionados();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).estadoJugador()).isEqualTo(EstadoJugador.LESIONADO);
        assertThat(result.get(0).disponible()).isFalse();
        assertThat(result.get(0).lesionesActivas()).isEqualTo(1);

        verify(jugadorRepository).findLesionados();
        verify(jugadorMapper).toResponse(jugadorLesionado);
    }

    // ── TESTS: ACTUALIZAR ──

    @Test
    @DisplayName("actualizarJugador: debe actualizar un jugador existente")
    void testActualizarJugador() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(jugadorRepository.save(jugador)).thenReturn(jugador);
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        JugadorResponse result = jugadorService.actualizarJugador(ID_JUGADOR, actualizarRequest);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Bukayo");

        assertThat(jugador.getNombre()).isEqualTo("Bukayo Actualizado");
        assertThat(jugador.getApellido()).isEqualTo("Saka Actualizado");
        assertThat(jugador.getPieHabil()).isEqualTo(JuegoPies.DERECHO);
        assertThat(jugador.getAltura()).isEqualTo(180);
        assertThat(jugador.getPeso()).isEqualTo(75);
        assertThat(jugador.getDatosDeportivos().getDorsal()).isEqualTo(10);
        assertThat(jugador.getDatosDeportivos().getPosiciones()).contains(PosicionJugador.DELANTERO);
        assertThat(jugador.getDatosDeportivos().getValorMercado()).isEqualTo(100_000_000.0);

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(jugadorRepository).save(jugador);
        verify(jugadorMapper).toResponse(jugador);
    }

    @Test
    @DisplayName("actualizarJugador: debe lanzar excepción cuando el jugador no existe")
    void testActualizarJugador_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(jugadorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jugadorService.actualizarJugador(idInexistente, actualizarRequest))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Jugador no encontrado con id: " + idInexistente);

        verify(jugadorRepository).findById(idInexistente);
        verify(jugadorRepository, never()).save(any());
        verify(jugadorMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("actualizarJugador: debe actualizar solo los campos proporcionados")
    void testActualizarJugador_Parcial() {
        ActualizarJugadorRequest requestSoloNombre = ActualizarJugadorRequest.builder()
                .nombre("Nuevo Nombre")
                .build();

        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(jugadorRepository.save(jugador)).thenReturn(jugador);
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        jugadorService.actualizarJugador(ID_JUGADOR, requestSoloNombre);

        assertThat(jugador.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(jugador.getApellido()).isEqualTo("Saka");
        assertThat(jugador.getAltura()).isEqualTo(178);

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(jugadorRepository).save(jugador);
    }

    @Test
    @DisplayName("actualizarJugador: debe manejar datos deportivos nulos correctamente")
    void testActualizarJugador_SinDatosDeportivos() {
        when(jugadorRepository.findById(ID_JUGADOR_2)).thenReturn(Optional.of(jugador2));
        when(jugadorRepository.save(jugador2)).thenReturn(jugador2);
        when(jugadorMapper.toResponse(jugador2)).thenReturn(response2);

        JugadorResponse result = jugadorService.actualizarJugador(ID_JUGADOR_2, actualizarRequest);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Nuevo");
        assertThat(jugador2.getDatosDeportivos()).isNull();

        verify(jugadorRepository).findById(ID_JUGADOR_2);
        verify(jugadorRepository).save(jugador2);
        verify(jugadorMapper).toResponse(jugador2);
    }

    @Test
    @DisplayName("actualizarJugador: debe actualizar fecha de actualización")
    void testActualizarJugador_ActualizaFecha() {
        LocalDate fechaAntes = jugador.getFechaActualizacion();
        
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(jugadorRepository.save(jugador)).thenReturn(jugador);
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        jugadorService.actualizarJugador(ID_JUGADOR, actualizarRequest);

        assertThat(jugador.getFechaActualizacion()).isAfterOrEqualTo(fechaAntes);
        assertThat(jugador.getFechaActualizacion()).isEqualTo(LocalDate.now());

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(jugadorRepository).save(jugador);
    }

    // ── TESTS: CAMBIAR ESTADO ──

    @Test
    @DisplayName("cambiarEstadoJugador: debe cambiar el estado del jugador")
    void testCambiarEstadoJugador() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(jugadorRepository.save(jugador)).thenReturn(jugador);
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        JugadorResponse result = jugadorService.cambiarEstadoJugador(ID_JUGADOR, EstadoJugador.SUPLENTE);

        assertThat(result).isNotNull();
        assertThat(jugador.getDatosDeportivos().getEstadoJugador()).isEqualTo(EstadoJugador.SUPLENTE);

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(jugadorRepository).save(jugador);
        verify(jugadorMapper).toResponse(jugador);
    }

    @Test
    @DisplayName("cambiarEstadoJugador: debe lanzar excepción cuando el jugador no existe")
    void testCambiarEstadoJugador_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(jugadorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jugadorService.cambiarEstadoJugador(idInexistente, EstadoJugador.SUPLENTE))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Jugador no encontrado con id: " + idInexistente);

        verify(jugadorRepository).findById(idInexistente);
        verify(jugadorRepository, never()).save(any());
        verify(jugadorMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("cambiarEstadoJugador: debe lanzar excepción cuando el jugador no tiene datos deportivos")
    void testCambiarEstadoJugador_SinDatosDeportivos() {
        when(jugadorRepository.findById(ID_JUGADOR_2)).thenReturn(Optional.of(jugador2));

        assertThatThrownBy(() -> jugadorService.cambiarEstadoJugador(ID_JUGADOR_2, EstadoJugador.SUPLENTE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene datos deportivos registrados");

        verify(jugadorRepository).findById(ID_JUGADOR_2);
        verify(jugadorRepository, never()).save(any());
        verify(jugadorMapper, never()).toResponse(any());
    }

   @Test
@DisplayName("cambiarEstadoJugador: debe lanzar excepción si el estado es nulo")
void testCambiarEstadoJugador_EstadoNulo() {
    when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));

    assertThatThrownBy(() -> jugadorService.cambiarEstadoJugador(ID_JUGADOR, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("El estado no puede ser nulo");

    verify(jugadorRepository).findById(ID_JUGADOR);
    verify(jugadorRepository, never()).save(any());
}

@Test
@DisplayName("cambiarEstadoJugador: debe lanzar excepción si el jugador está retirado y se intenta cambiar de estado")
void testCambiarEstadoJugador_JugadorRetirado() {
    // Primero cambiamos el estado a RETIRADO
    DatosDeportivos datosRetirado = DatosDeportivos.builder()
            .idHistorialDeportivo(UUID.randomUUID())
            .estadoJugador(EstadoJugador.RETIRADO)
            .posiciones(new ArrayList<>(List.of(PosicionJugador.DELANTERO)))
            .dorsal(9)
            .fechaActualizacion(LocalDate.now())
            .build();
    
    Jugador jugadorRetirado = Jugador.builder()
            .idPersonal(UUID.randomUUID())
            .nombre("Jugador")
            .apellido("Retirado")
            .datosDeportivos(datosRetirado)
            .build();
    
    when(jugadorRepository.findById(jugadorRetirado.getIdPersonal())).thenReturn(Optional.of(jugadorRetirado));

    assertThatThrownBy(() -> jugadorService.cambiarEstadoJugador(jugadorRetirado.getIdPersonal(), EstadoJugador.TITULAR))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Un jugador retirado no puede cambiar de estado");

    verify(jugadorRepository).findById(jugadorRetirado.getIdPersonal());
    verify(jugadorRepository, never()).save(any());
}

    @Test
    @DisplayName("actualizarValorMercado: debe actualizar el valor de mercado del jugador")
    void testActualizarValorMercado() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(jugadorRepository.save(jugador)).thenReturn(jugador);
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        JugadorResponse result = jugadorService.actualizarValorMercado(ID_JUGADOR, 150_000_000.0);

        assertThat(result).isNotNull();
        assertThat(jugador.getDatosDeportivos().getValorMercado()).isEqualTo(150_000_000.0);

        verify(jugadorRepository).findById(ID_JUGADOR);
        verify(jugadorRepository).save(jugador);
        verify(jugadorMapper).toResponse(jugador);
    }

    @Test
    @DisplayName("actualizarValorMercado: debe lanzar excepción cuando el jugador no existe")
    void testActualizarValorMercado_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(jugadorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jugadorService.actualizarValorMercado(idInexistente, 100_000_000.0))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Jugador no encontrado con id: " + idInexistente);

        verify(jugadorRepository).findById(idInexistente);
        verify(jugadorRepository, never()).save(any());
        verify(jugadorMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("actualizarValorMercado: debe lanzar excepción cuando el jugador no tiene datos deportivos")
    void testActualizarValorMercado_SinDatosDeportivos() {
        when(jugadorRepository.findById(ID_JUGADOR_2)).thenReturn(Optional.of(jugador2));

        assertThatThrownBy(() -> jugadorService.actualizarValorMercado(ID_JUGADOR_2, 100_000_000.0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene datos deportivos registrados");

        verify(jugadorRepository).findById(ID_JUGADOR_2);
        verify(jugadorRepository, never()).save(any());
        verify(jugadorMapper, never()).toResponse(any());
    }


    @Test
    @DisplayName("eliminarJugador: debe eliminar un jugador existente")
    void testEliminarJugador() {
        when(jugadorRepository.existsById(ID_JUGADOR)).thenReturn(true);
        doNothing().when(jugadorRepository).deleteById(ID_JUGADOR);

        jugadorService.eliminarJugador(ID_JUGADOR);

        verify(jugadorRepository).existsById(ID_JUGADOR);
        verify(jugadorRepository).deleteById(ID_JUGADOR);
    }

    @Test
    @DisplayName("eliminarJugador: debe lanzar excepción cuando el jugador no existe")
    void testEliminarJugador_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(jugadorRepository.existsById(idInexistente)).thenReturn(false);

        assertThatThrownBy(() -> jugadorService.eliminarJugador(idInexistente))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Jugador no encontrado con id: " + idInexistente);

        verify(jugadorRepository).existsById(idInexistente);
        verify(jugadorRepository, never()).deleteById(any());
    }


    @Test
    @DisplayName("obtenerJugadorPorId: debe retornar información completa del jugador")
    void testObtenerJugadorPorId_InformacionCompleta() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(jugadorMapper.toResponse(jugador)).thenReturn(response);

        JugadorResponse result = jugadorService.obtenerJugadorPorId(ID_JUGADOR);

        assertThat(result.nombreCompleto()).isEqualTo("Bukayo Saka");
        assertThat(result.edad()).isNotNull();
        assertThat(result.edad()).isGreaterThan(0);
        assertThat(result.valorMercadoEnMillones()).isEqualTo(85.0);
        assertThat(result.lesionesActivas()).isEqualTo(1);
        assertThat(result.disponible()).isTrue();
    }

    @Test
    @DisplayName("actualizarJugador: debe manejar actualización de dorsal con datos deportivos nulos")
    void testActualizarJugador_DorsalSinDatos() {
        ActualizarJugadorRequest requestSoloDorsal = ActualizarJugadorRequest.builder()
                .dorsal(10)
                .build();

        when(jugadorRepository.findById(ID_JUGADOR_2)).thenReturn(Optional.of(jugador2));
        when(jugadorRepository.save(jugador2)).thenReturn(jugador2);
        when(jugadorMapper.toResponse(jugador2)).thenReturn(response2);

        jugadorService.actualizarJugador(ID_JUGADOR_2, requestSoloDorsal);

        assertThat(jugador2.getDatosDeportivos()).isNull();

        verify(jugadorRepository).findById(ID_JUGADOR_2);
        verify(jugadorRepository).save(jugador2);
    }

    @Test
    @DisplayName("obtenerJugadoresLesionados: debe retornar lista vacía cuando no hay lesionados")
    void testObtenerJugadoresLesionados_Vacio() {
        when(jugadorRepository.findLesionados()).thenReturn(List.of());

        List<JugadorResponse> result = jugadorService.obtenerJugadoresLesionados();

        assertThat(result).isEmpty();
        verify(jugadorRepository).findLesionados();
        verify(jugadorMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("obtenerJugadoresDisponibles: debe retornar lista vacía cuando no hay disponibles")
    void testObtenerJugadoresDisponibles_Vacio() {
        when(jugadorRepository.findDisponibles()).thenReturn(List.of());

        List<JugadorResponse> result = jugadorService.obtenerJugadoresDisponibles();

        assertThat(result).isEmpty();
        verify(jugadorRepository).findDisponibles();
        verify(jugadorMapper, never()).toResponse(any());
    }
}