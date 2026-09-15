package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.ActualizarJugadorRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearJugadorRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasPartidoJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.mapper.EstadisticasJugadorMapper;
import com.futbol.estadisticas.application.port.mapper.EstadisticasPartidoMapper;
import com.futbol.estadisticas.application.port.mapper.JugadorMapper;
import com.futbol.estadisticas.application.port.out.EventosPartidoRepositoryPort;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.domain.model.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JugadorServiceTest {

    @Mock private EventosPartidoRepositoryPort eventosRepository;
    @Mock private EstadisticasJugadorMapper estadisticasMapper;
    @Mock private PartidoRepositoryPort partidoRepository;
    @Mock private JugadorRepositoryPort jugadorRepository;
    @Mock private JugadorMapper jugadorMapper;
    @Mock private EstadisticasPartidoMapper partidoConEstadisticasJugadorMapper;

    @InjectMocks
    private JugadorService jugadorService;

    private static final UUID ID_JUGADOR = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_PARTIDO = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_EQUIPO = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private Jugador jugador;
    private DatosDeportivos datos;
    private JugadorResponse response;
    private CrearJugadorRequest crearRequest;
    private ActualizarJugadorRequest actualizarRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        datos = DatosDeportivos.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(85_000_000.0)
                .posiciones(new ArrayDeque<>(List.of(PosicionJugador.EXTREMO_DERECHO)))
                .dorsal(7)
                .fechaActualizacion(LocalDate.now())
                .build();

        jugador = Jugador.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(178)
                .peso(70)
                .datosDeportivos(datos)
                .fechaActualizacion(LocalDate.now())
                .build();

        response = JugadorResponse.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .nombreCompleto("Bukayo Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .edad(24)
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(178)
                .peso(70)
                .posiciones(List.of(PosicionJugador.EXTREMO_DERECHO))
                .dorsal(7)
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(85_000_000.0)
                .valorMercadoEnMillones(85.0)
                .disponible(true)
                .lesionesActivas(0)
                .build();

        crearRequest = CrearJugadorRequest.builder()
                .nombre("Bukayo")
                .apellido("Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(178)
                .peso(70)
                .dorsal(7)
                .posiciones(List.of(PosicionJugador.EXTREMO_DERECHO))
                .valorMercado(85_000_000.0)
                .build();

        actualizarRequest = ActualizarJugadorRequest.builder()
                .nombre("Nuevo")
                .apellido("Apellido")
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("buscarJugadores")
    class BuscarJugadores {

        @Test
        @DisplayName("devuelve página vacía si el texto es nulo o vacío")
        void textoVacio() {
            assertThat(jugadorService.buscarJugadores(null, pageable)).isEmpty();
            assertThat(jugadorService.buscarJugadores("   ", pageable)).isEmpty();
            verify(jugadorRepository, never()).buscarJugadorPorTexto(any(), any());
        }

        @Test
        @DisplayName("delega en el repositorio y mapea la página")
        void delega() {
            Page<Jugador> page = new PageImpl<>(List.of(jugador));
            when(jugadorRepository.buscarJugadorPorTexto(eq("Saka"), eq(pageable))).thenReturn(page);
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            Page<JugadorResponse> result = jugadorService.buscarJugadores("Saka", pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(jugadorRepository).buscarJugadorPorTexto("Saka", pageable);
        }
    }

    @Nested
    @DisplayName("crearJugador / crearVariosJugadores")
    class Crear {

        @Test
        @DisplayName("crearJugador: mapea, guarda y devuelve response")
        void crearUno() {
            when(jugadorMapper.toEntity(crearRequest)).thenReturn(jugador);
            when(jugadorRepository.save(jugador)).thenReturn(jugador);
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            JugadorResponse result = jugadorService.crearJugador(crearRequest);

            assertThat(result).isSameAs(response);
            verify(jugadorRepository).save(jugador);
        }

        @Test
        @DisplayName("crearVariosJugadores: guarda todos y mapea la lista")
        void crearVarios() {
            List<CrearJugadorRequest> requests = List.of(crearRequest, crearRequest);
            when(jugadorMapper.toEntity(any(CrearJugadorRequest.class))).thenReturn(jugador);
            when(jugadorRepository.saveAll(anyList())).thenReturn(List.of(jugador, jugador));
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            List<JugadorResponse> result = jugadorService.crearVariosJugadores(requests);

            assertThat(result).hasSize(2);
            verify(jugadorRepository).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("estadísticas")
    class Estadisticas {

        @Test
        @DisplayName("obtenerEstadisticasJugador: delega en mapper con eventos")
        void estadisticasOk() {
            EventosPartido evento = mock(EventosPartido.class);
            List<EventosPartido> eventos = List.of(evento);
            EstadisticasJugadorResponse estadisticas = mock(EstadisticasJugadorResponse.class);

            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
            when(eventosRepository.findByPersonalConCompeticion(ID_JUGADOR)).thenReturn(eventos);
            when(estadisticasMapper.toResponse(jugador, eventos)).thenReturn(estadisticas);

            assertThat(jugadorService.obtenerEstadisticasJugador(ID_JUGADOR))
                    .isSameAs(estadisticas);
        }

        @Test
        @DisplayName("obtenerEstadisticasJugador: lanza si el jugador no existe")
        void estadisticasJugadorNoExiste() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> jugadorService.obtenerEstadisticasJugador(ID_JUGADOR))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("obtenerPartidosConEstadisticas: agrupa eventos por partido y ordena")
        void partidosConEstadisticas() {
            Partido partido1 = Partido.builder()
                    .idPartido(ID_PARTIDO)
                    .fechaYHora(LocalDateTime.now().minusDays(1))
                    .build();

            EventosPartido evento1 = EventosPartido.builder()
                    .idEvento(UUID.randomUUID())
                    .partido(partido1)
                    .build();

            PartidoResponse partidoResponse = PartidoResponse.builder()
                    .fechaYHora(LocalDateTime.now().minusDays(1))
                    .build();

            EstadisticasPartidoJugadorResponse responsePartido =
                    EstadisticasPartidoJugadorResponse.builder()
                            .partido(partidoResponse)
                            .build();

            when(jugadorRepository.existsById(ID_JUGADOR)).thenReturn(true);
            when(partidoRepository.findPartidosByJugador(ID_JUGADOR)).thenReturn(List.of(partido1));
            when(eventosRepository.findByPersonalConCompeticion(ID_JUGADOR)).thenReturn(List.of(evento1));
            when(partidoConEstadisticasJugadorMapper.toResponse(eq(partido1), anyList()))
                    .thenReturn(responsePartido);

            List<EstadisticasPartidoJugadorResponse> result =
                    jugadorService.obtenerPartidosConEstadisticas(ID_JUGADOR);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("obtenerPartidosConEstadisticas: lanza si el jugador no existe")
        void partidosJugadorNoExiste() {
            when(jugadorRepository.existsById(ID_JUGADOR)).thenReturn(false);

            assertThatThrownBy(() -> jugadorService.obtenerPartidosConEstadisticas(ID_JUGADOR))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("consultas varias")
    class Consultas {

        @Test
        @DisplayName("obtenerJugadorPorId: retorna response")
        void porId() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            assertThat(jugadorService.obtenerJugadorPorId(ID_JUGADOR)).isSameAs(response);
        }

        @Test
        @DisplayName("obtenerJugadorPorId: lanza si no existe")
        void porIdNoExiste() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> jugadorService.obtenerJugadorPorId(ID_JUGADOR))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("obtenerTodosLosJugadores: mapea página")
        void todos() {
            Page<Jugador> page = new PageImpl<>(List.of(jugador));
            when(jugadorRepository.findAll(pageable)).thenReturn(page);
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            assertThat(jugadorService.obtenerTodosLosJugadores(pageable).getContent()).hasSize(1);
        }

        @Test
        @DisplayName("obtenerJugadoresPorEquipo: mapea lista")
        void porEquipo() {
            when(jugadorRepository.findByEquipo(ID_EQUIPO)).thenReturn(List.of(jugador));
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            assertThat(jugadorService.obtenerJugadoresPorEquipo(ID_EQUIPO)).hasSize(1);
        }

        @Test
        @DisplayName("obtenerJugadoresPorPosicion: mapea lista")
        void porPosicion() {
            when(jugadorRepository.findByPosicion(PosicionJugador.EXTREMO_DERECHO))
                    .thenReturn(List.of(jugador));
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            assertThat(jugadorService.obtenerJugadoresPorPosicion(PosicionJugador.EXTREMO_DERECHO))
                    .hasSize(1);
        }

        @Test
        @DisplayName("obtenerJugadoresDisponibles: mapea lista")
        void disponibles() {
            when(jugadorRepository.findDisponibles()).thenReturn(List.of(jugador));
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            assertThat(jugadorService.obtenerJugadoresDisponibles()).hasSize(1);
        }

        @Test
        @DisplayName("obtenerJugadoresLesionados: mapea lista")
        void lesionados() {
            when(jugadorRepository.findLesionados()).thenReturn(List.of(jugador));
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            assertThat(jugadorService.obtenerJugadoresLesionados()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("actualizarJugador")
    class Actualizar {

        @Test
        @DisplayName("actualiza solo los campos no nulos")
        void actualizaCamposNoNulos() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
            when(jugadorRepository.save(jugador)).thenReturn(jugador);
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            jugadorService.actualizarJugador(ID_JUGADOR, actualizarRequest);

            assertThat(jugador.getNombre()).isEqualTo("Nuevo");
            assertThat(jugador.getApellido()).isEqualTo("Apellido");
            assertThat(jugador.getPieHabil()).isEqualTo(JuegoPies.DERECHO);
            assertThat(jugador.getAltura()).isEqualTo(180);
            assertThat(jugador.getPeso()).isEqualTo(75);
        }

        @Test
        @DisplayName("lanza si el jugador no existe")
        void noExiste() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> jugadorService.actualizarJugador(ID_JUGADOR, actualizarRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("cambiarEstadoJugador / actualizarValorMercado")
    class Cambios {

        @Test
        @DisplayName("cambiarEstadoJugador: actualiza el estado")
        void cambiarEstado() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
            when(jugadorRepository.save(jugador)).thenReturn(jugador);
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            jugadorService.cambiarEstadoJugador(ID_JUGADOR, EstadoJugador.LESIONADO);

            assertThat(jugador.getDatosDeportivos().getEstadoJugador())
                    .isEqualTo(EstadoJugador.LESIONADO);
        }

        @Test
        @DisplayName("cambiarEstadoJugador: lanza si no tiene datos")
        void cambiarEstadoSinDatos() {
            jugador.setDatosDeportivos(null);
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));

            assertThatThrownBy(() ->
                    jugadorService.cambiarEstadoJugador(ID_JUGADOR, EstadoJugador.LESIONADO))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("actualizarValorMercado: actualiza el valor")
        void actualizarValor() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
            when(jugadorRepository.save(jugador)).thenReturn(jugador);
            when(jugadorMapper.toResponse(jugador)).thenReturn(response);

            jugadorService.actualizarValorMercado(ID_JUGADOR, 100_000_000.0);

            assertThat(jugador.getDatosDeportivos().getValorMercado()).isEqualTo(100_000_000.0);
        }

        @Test
        @DisplayName("actualizarValorMercado: lanza si no tiene datos")
        void actualizarValorSinDatos() {
            jugador.setDatosDeportivos(null);
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));

            assertThatThrownBy(() ->
                    jugadorService.actualizarValorMercado(ID_JUGADOR, 100_000_000.0))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("eliminarJugador")
    class Eliminar {

        @Test
        @DisplayName("elimina si existe")
        void elimina() {
            when(jugadorRepository.existsById(ID_JUGADOR)).thenReturn(true);

            jugadorService.eliminarJugador(ID_JUGADOR);

            verify(jugadorRepository).deleteById(ID_JUGADOR);
        }

        @Test
        @DisplayName("lanza si no existe")
        void noExiste() {
            when(jugadorRepository.existsById(ID_JUGADOR)).thenReturn(false);

            assertThatThrownBy(() -> jugadorService.eliminarJugador(ID_JUGADOR))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}