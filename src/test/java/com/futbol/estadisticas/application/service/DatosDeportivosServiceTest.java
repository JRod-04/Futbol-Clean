package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.response.DatosDeportivosResponse;
import com.futbol.estadisticas.application.port.mapper.DatosDeportivosMapper;
import com.futbol.estadisticas.application.port.out.DatosDeportivosRepositoryPort;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
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
class DatosDeportivosServiceTest {

    @Mock private DatosDeportivosRepositoryPort datosDeportivosRepository;
    @Mock private JugadorRepositoryPort jugadorRepository;
    @Mock private DatosDeportivosMapper datosDeportivosMapper;
    @InjectMocks private DatosDeportivosService datosDeportivosService;

    private static final UUID ID_JUGADOR = UUID.randomUUID();
    private static final UUID ID_CLUB = UUID.randomUUID();
    private Jugador jugador;
    private DatosDeportivos datos;
    private DatosDeportivosResponse response;
    private Equipo club;

    @BeforeEach
    void setUp() {
        club = Equipo.builder()
                .idEquipo(ID_CLUB)
                .nombre("Arsenal FC")
                .build();

        jugador = Jugador.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .build();

        // ✅ DatosDeportivos con lista de posiciones y dorsal
        datos = DatosDeportivos.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(85_000_000.0)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.EXTREMO_DERECHO)))
                .dorsal(7)
                .fechaActualizacion(LocalDate.now())
                .build();

        // ✅ Response actualizado (solo posicion y dorsal)
        response = new DatosDeportivosResponse(
                datos.getIdHistorialDeportivo(),
                datos.getPosicionActual(),  // ✅ Posición actual (última de la lista)
                datos.getDorsal(),
                datos.getEstadoJugador(),
                datos.getValorMercado(),
                datos.getValorMercadoEnMillones(),
                datos.getFechaActualizacion(),
                datos.esTitular(),
                datos.esSuplente(),
                datos.estaDisponible(),
                datos.estaLesionado(),
                ID_JUGADOR,
                "Bukayo Saka"
        );
    }

    // ── TESTS: OBTENER ──

    @Test
    @DisplayName("obtenerPorJugador: debe retornar datos deportivos")
    void testObtenerPorJugador() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(datosDeportivosMapper.toResponse(datos, jugador)).thenReturn(response);

        DatosDeportivosResponse result = datosDeportivosService.obtenerPorJugador(ID_JUGADOR);

        assertThat(result).isNotNull();
        assertThat(result.posicion()).isEqualTo(PosicionJugador.EXTREMO_DERECHO);
        assertThat(result.dorsal()).isEqualTo(7);
        verify(datosDeportivosRepository).findByJugador(ID_JUGADOR);
    }

    @Test
    @DisplayName("obtenerPorJugador: debe lanzar excepción cuando el jugador no existe")
    void testObtenerPorJugador_JugadorNoExiste() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datosDeportivosService.obtenerPorJugador(ID_JUGADOR))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Jugador no encontrado");
    }

    @Test
    @DisplayName("obtenerPorJugador: debe lanzar excepción cuando no tiene datos deportivos")
    void testObtenerPorJugador_SinDatos() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datosDeportivosService.obtenerPorJugador(ID_JUGADOR))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene datos deportivos registrados");
    }

    // ── TESTS: ACTUALIZAR VALOR MERCADO ──

    @Test
    @DisplayName("actualizarValorMercado: debe actualizar el valor de mercado")
    void testActualizarValorMercado() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(datosDeportivosRepository.save(any(DatosDeportivos.class))).thenReturn(datos);
        when(datosDeportivosMapper.toResponse(datos, jugador)).thenReturn(response);

        DatosDeportivosResponse result = datosDeportivosService.actualizarValorMercado(ID_JUGADOR, 100_000_000.0);

        assertThat(result).isNotNull();
        verify(datosDeportivosRepository).save(any(DatosDeportivos.class));
    }

    // ── TESTS: CAMBIAR POSICIÓN ──

    @Test
    @DisplayName("cambiarPosicion: debe agregar una nueva posición a la lista")
    void testCambiarPosicion() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(datosDeportivosRepository.save(any(DatosDeportivos.class))).thenReturn(datos);
        when(datosDeportivosMapper.toResponse(datos, jugador)).thenReturn(response);

        DatosDeportivosResponse result = datosDeportivosService.cambiarPosicion(ID_JUGADOR, PosicionJugador.DELANTERO);

        assertThat(result).isNotNull();
        // Verificar que se agregó la posición
        assertThat(datos.getPosiciones()).hasSize(2);
        assertThat(datos.getPosiciones()).contains(PosicionJugador.DELANTERO);
        assertThat(datos.getPosicionActual()).isEqualTo(PosicionJugador.DELANTERO);
        verify(datosDeportivosRepository).save(any(DatosDeportivos.class));
    }

    // ── TESTS: PROMOVER/CAMBIAR ESTADO ──

    @Test
    @DisplayName("promoverATitular: debe promover al jugador a titular")
    void testPromoverATitular() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(datosDeportivosRepository.save(any(DatosDeportivos.class))).thenReturn(datos);
        when(datosDeportivosMapper.toResponse(datos, jugador)).thenReturn(response);

        DatosDeportivosResponse result = datosDeportivosService.promoverATitular(ID_JUGADOR);

        assertThat(result).isNotNull();
        assertThat(datos.getEstadoJugador()).isEqualTo(EstadoJugador.TITULAR);
        verify(datosDeportivosRepository).save(any(DatosDeportivos.class));
    }

    @Test
    @DisplayName("cambiarASuplente: debe cambiar al jugador a suplente")
    void testCambiarASuplente() {
        datos.setEstadoJugador(EstadoJugador.TITULAR);
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(datosDeportivosRepository.save(any(DatosDeportivos.class))).thenReturn(datos);
        when(datosDeportivosMapper.toResponse(datos, jugador)).thenReturn(response);

        DatosDeportivosResponse result = datosDeportivosService.cambiarASuplente(ID_JUGADOR);

        assertThat(result).isNotNull();
        assertThat(datos.getEstadoJugador()).isEqualTo(EstadoJugador.SUPLENTE);
        verify(datosDeportivosRepository).save(any(DatosDeportivos.class));
    }

    @Test
    @DisplayName("actualizarEstado: debe actualizar el estado del jugador")
    void testActualizarEstado() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(datosDeportivosRepository.save(any(DatosDeportivos.class))).thenReturn(datos);
        when(datosDeportivosMapper.toResponse(datos, jugador)).thenReturn(response);

        DatosDeportivosResponse result = datosDeportivosService.actualizarEstado(ID_JUGADOR, EstadoJugador.LESIONADO);

        assertThat(result).isNotNull();
        assertThat(datos.getEstadoJugador()).isEqualTo(EstadoJugador.LESIONADO);
        verify(datosDeportivosRepository).save(any(DatosDeportivos.class));
    }

    // ── TESTS: DORSAL ──

    @Test
    @DisplayName("actualizarDorsal: debe actualizar el dorsal del jugador correctamente")
    void testActualizarDorsal() {
        // Given - jugador con club
        Contrato contrato = Contrato.builder()
                .equipo(club)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .estado(EstadoContrato.ACTIVO)
                .build();
        jugador.agregarContrato(contrato);
        jugador.setDatosDeportivos(datos);

        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(jugadorRepository.findByEquipo(ID_CLUB)).thenReturn(List.of(jugador));
        when(datosDeportivosRepository.save(any(DatosDeportivos.class))).thenReturn(datos);
        when(datosDeportivosMapper.toResponse(datos, jugador)).thenReturn(response);

        // When
        DatosDeportivosResponse result = datosDeportivosService.actualizarDorsal(ID_JUGADOR, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(datos.getDorsal()).isEqualTo(10);
        verify(jugadorRepository).findByEquipo(ID_CLUB);
        verify(datosDeportivosRepository).save(any(DatosDeportivos.class));
    }

    @Test
    @DisplayName("actualizarDorsal: debe lanzar excepción si el dorsal está ocupado en el club")
    void testActualizarDorsal_DorsalOcupado() {
        // Given - dos jugadores en el mismo club
        Jugador otroJugador = Jugador.builder()
                .idPersonal(UUID.randomUUID())
                .nombre("Otro")
                .apellido("Jugador")
                .build();
        DatosDeportivos datosOtro = DatosDeportivos.builder()
                .dorsal(10)
                .build();
        otroJugador.setDatosDeportivos(datosOtro);

        Contrato contrato = Contrato.builder()
                .equipo(club)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .estado(EstadoContrato.ACTIVO)
                .build();
        jugador.agregarContrato(contrato);
        jugador.setDatosDeportivos(datos);

        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(jugadorRepository.findByEquipo(ID_CLUB)).thenReturn(List.of(jugador, otroJugador));

        // When & Then
        assertThatThrownBy(() -> datosDeportivosService.actualizarDorsal(ID_JUGADOR, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya está asignado a otro jugador del club");
    }

    @Test
    @DisplayName("actualizarDorsal: debe permitir actualizar si el dorsal no está ocupado")
    void testActualizarDorsal_DorsalLibre() {
        // Given - jugador con club y dorsal libre
        Contrato contrato = Contrato.builder()
                .equipo(club)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .estado(EstadoContrato.ACTIVO)
                .build();
        jugador.agregarContrato(contrato);
        jugador.setDatosDeportivos(datos);

        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(jugadorRepository.findByEquipo(ID_CLUB)).thenReturn(List.of(jugador));
        when(datosDeportivosRepository.save(any(DatosDeportivos.class))).thenReturn(datos);
        when(datosDeportivosMapper.toResponse(datos, jugador)).thenReturn(response);

        // When
        DatosDeportivosResponse result = datosDeportivosService.actualizarDorsal(ID_JUGADOR, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(datos.getDorsal()).isEqualTo(10);
        verify(datosDeportivosRepository).save(any(DatosDeportivos.class));
    }

    @Test
    @DisplayName("actualizarDorsal: no debe validar unicidad si el jugador no tiene club")
    void testActualizarDorsal_SinClub() {
        // Given - jugador sin club
        jugador.setDatosDeportivos(datos);

        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));
        when(datosDeportivosRepository.save(any(DatosDeportivos.class))).thenReturn(datos);
        when(datosDeportivosMapper.toResponse(datos, jugador)).thenReturn(response);

        // When
        DatosDeportivosResponse result = datosDeportivosService.actualizarDorsal(ID_JUGADOR, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(datos.getDorsal()).isEqualTo(10);
        verify(jugadorRepository, never()).findByEquipo(any());
        verify(datosDeportivosRepository).save(any(DatosDeportivos.class));
    }

    @Test
    @DisplayName("actualizarDorsal: debe lanzar excepción si el dorsal es nulo")
    void testActualizarDorsal_Nulo() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));

        assertThatThrownBy(() -> datosDeportivosService.actualizarDorsal(ID_JUGADOR, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El dorsal no puede ser nulo");
    }

    @Test
    @DisplayName("actualizarDorsal: debe lanzar excepción si el dorsal es negativo")
    void testActualizarDorsal_Negativo() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));

        assertThatThrownBy(() -> datosDeportivosService.actualizarDorsal(ID_JUGADOR, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El dorsal debe ser positivo");
    }

    @Test
    @DisplayName("actualizarDorsal: debe lanzar excepción si el dorsal es cero")
    void testActualizarDorsal_Cero() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.of(datos));

        assertThatThrownBy(() -> datosDeportivosService.actualizarDorsal(ID_JUGADOR, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El dorsal debe ser positivo");
    }

    // ── TESTS: PROMOVER/CAMBIAR SIN DATOS ──

    @Test
    @DisplayName("promoverATitular: debe lanzar excepción si el jugador no tiene datos")
    void testPromoverATitular_SinDatos() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datosDeportivosService.promoverATitular(ID_JUGADOR))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene datos deportivos registrados");
    }

    @Test
    @DisplayName("cambiarASuplente: debe lanzar excepción si el jugador no tiene datos")
    void testCambiarASuplente_SinDatos() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datosDeportivosService.cambiarASuplente(ID_JUGADOR))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene datos deportivos registrados");
    }

    @Test
    @DisplayName("actualizarEstado: debe lanzar excepción si el jugador no tiene datos")
    void testActualizarEstado_SinDatos() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datosDeportivosService.actualizarEstado(ID_JUGADOR, EstadoJugador.SUPLENTE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene datos deportivos registrados");
    }

    @Test
    @DisplayName("cambiarPosicion: debe lanzar excepción si el jugador no tiene datos")
    void testCambiarPosicion_SinDatos() {
        when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
        when(datosDeportivosRepository.findByJugador(ID_JUGADOR)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datosDeportivosService.cambiarPosicion(ID_JUGADOR, PosicionJugador.DELANTERO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene datos deportivos registrados");
    }
}