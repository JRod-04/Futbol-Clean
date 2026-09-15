package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.RegistrarLesionRequest;
import com.futbol.estadisticas.application.port.dto.response.LesionResponse;
import com.futbol.estadisticas.application.port.mapper.LesionMapper;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.application.port.out.LesionRepositoryPort;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Lesion;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.Gravedad;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
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
class LesionServiceTest {

    @Mock private LesionRepositoryPort lesionRepository;
    @Mock private JugadorRepositoryPort jugadorRepository;
    @Mock private LesionMapper lesionMapper;

    @InjectMocks
    private LesionService lesionService;

    private static final UUID ID_JUGADOR = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_LESION = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private Jugador jugador;
    private Lesion lesion;
    private LesionResponse response;
    private RegistrarLesionRequest request;

    @BeforeEach
    void setUp() {
        jugador = Jugador.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .datosDeportivos(DatosDeportivos.builder()
                        .idHistorialDeportivo(UUID.randomUUID())
                        .estadoJugador(EstadoJugador.LESIONADO)
                        .build())
                .build();

        lesion = Lesion.builder()
                .idLesion(ID_LESION)
                .jugadorLesionado(jugador)
                .nombreLesion("Rotura de ligamento")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFin(LocalDate.now().plusDays(20))
                .curada(false)
                .build();

        response = mock(LesionResponse.class);

        request = RegistrarLesionRequest.builder()
                .idJugador(ID_JUGADOR)
                .nombreLesion("Rotura de ligamento")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFinEstimada(LocalDate.now().plusDays(20))
                .build();
    }

    @Nested
    @DisplayName("registrarLesion")
    class RegistrarLesion {



            @Test
            @DisplayName("registra la lesión y guarda jugador")
            void registrarOk() {
                when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
                when(lesionMapper.toEntity(request, jugador)).thenReturn(lesion);
                when(lesionRepository.save(lesion)).thenReturn(lesion);
                when(jugadorRepository.save(jugador)).thenReturn(jugador);
                when(lesionMapper.toResponse(lesion, jugador)).thenReturn(response);

                LesionResponse result = lesionService.registrarLesion(request);

                assertThat(result).isSameAs(response);
                assertThat(jugador.getLesiones()).contains(lesion);
                verify(lesionRepository).save(lesion);
                verify(jugadorRepository).save(jugador);
            }

            @Test
            @DisplayName("lanza PersonalNotFoundException si el jugador no existe")
            void jugadorNoExiste() {
                when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> lesionService.registrarLesion(request))
                        .isInstanceOf(ResourceNotFoundException.class);
            }
        }
        @Test
        @DisplayName("lanza PersonalNotFoundException si el jugador no existe")
        void jugadorNoExiste() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lesionService.registrarLesion(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }


    @Nested
    @DisplayName("registrarVariasLesiones")
    class RegistrarVarias {

        @Test
        @DisplayName("guarda todas las lesiones y sus jugadores")
        void registrarVariasOk() {
            List<RegistrarLesionRequest> requests = List.of(request, request);

            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
            when(lesionMapper.toEntity(any(RegistrarLesionRequest.class), eq(jugador)))
                    .thenReturn(lesion);
            when(lesionRepository.saveAll(anyList())).thenReturn(List.of(lesion, lesion));
            when(lesionMapper.toResponse(eq(lesion), eq(jugador))).thenReturn(response);

            List<LesionResponse> result = lesionService.registrarVariasLesiones(requests);

            assertThat(result).hasSize(2);
            verify(lesionRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("lanza si algún jugador no existe")
        void jugadorNoExiste() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lesionService.registrarVariasLesiones(List.of(request)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("obtenerLesionPorId: retorna response")
        void porId() {
            when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.of(lesion));
            when(lesionMapper.toResponse(lesion, null)).thenReturn(response);

            assertThat(lesionService.obtenerLesionPorId(ID_LESION)).isSameAs(response);
        }

        @Test
        @DisplayName("obtenerLesionPorId: lanza si no existe")
        void porIdNoExiste() {
            when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lesionService.obtenerLesionPorId(ID_LESION))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("obtenerLesionesPorJugador: mapea lista")
        void porJugador() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
            when(lesionRepository.findByJugador(ID_JUGADOR)).thenReturn(List.of(lesion));
            when(lesionMapper.toResponse(lesion, jugador)).thenReturn(response);

            assertThat(lesionService.obtenerLesionesPorJugador(ID_JUGADOR)).hasSize(1);
        }

        @Test
        @DisplayName("obtenerLesionesActivasPorJugador: mapea lista")
        void activasPorJugador() {
            when(jugadorRepository.findById(ID_JUGADOR)).thenReturn(Optional.of(jugador));
            when(lesionRepository.findActivasByJugador(ID_JUGADOR)).thenReturn(List.of(lesion));
            when(lesionMapper.toResponse(lesion, jugador)).thenReturn(response);

            assertThat(lesionService.obtenerLesionesActivasPorJugador(ID_JUGADOR)).hasSize(1);
        }

        @Test
        @DisplayName("obtenerLesionesActivasEnSistema: mapea lista")
        void activasSistema() {
            when(lesionRepository.findActivas()).thenReturn(List.of(lesion));
            when(lesionMapper.toResponse(lesion, null)).thenReturn(response);

            assertThat(lesionService.obtenerLesionesActivasEnSistema()).hasSize(1);
        }

        @Test
        @DisplayName("obtenerLesionesPorGravedad: mapea lista")
        void porGravedad() {
            when(lesionRepository.findByGravedad(Gravedad.GRAVE)).thenReturn(List.of(lesion));
            when(lesionMapper.toResponse(lesion, null)).thenReturn(response);

            assertThat(lesionService.obtenerLesionesPorGravedad(Gravedad.GRAVE)).hasSize(1);
        }
    }

    @Nested
    @DisplayName("curarLesion")
    class CurarLesion {

        @Test
        @DisplayName("cura la lesión y no cambia estado si aún hay otras activas")
        void curaConOtrasActivas() {
            when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.of(lesion));
            when(lesionRepository.save(lesion)).thenReturn(lesion);
            when(lesionRepository.findActivasByJugador(ID_LESION))
                    .thenReturn(List.of(mock(Lesion.class)));
            when(lesionMapper.toResponse(lesion, null)).thenReturn(response);

            LesionResponse result = lesionService.curarLesion(ID_LESION);

            assertThat(result).isSameAs(response);
            verify(jugadorRepository, never()).save(any(Jugador.class));
        }

        @Test
        @DisplayName("cura la lesión y actualiza jugador si no quedan activas")
        void curaSinOtrasActivas() {
            jugador.setLesiones(new ArrayList<>());
            jugador.getLesiones().add(lesion);
            jugador.setDatosDeportivos(DatosDeportivos.builder()
                    .idHistorialDeportivo(UUID.randomUUID())
                    .estadoJugador(EstadoJugador.LESIONADO)
                    .build());

            when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.of(lesion));
            when(lesionRepository.save(lesion)).thenReturn(lesion);
            when(lesionRepository.findActivasByJugador(ID_LESION))
                    .thenReturn(new ArrayList<>());
            when(jugadorRepository.findAll(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(jugador)));
            when(lesionMapper.toResponse(lesion, null)).thenReturn(response);

            lesionService.curarLesion(ID_LESION);

            assertThat(jugador.getDatosDeportivos().getEstadoJugador())
                    .isEqualTo(EstadoJugador.SUPLENTE);
            verify(jugadorRepository).save(jugador);
        }

        @Test
        @DisplayName("lanza si la lesión no existe")
        void lesionNoExiste() {
            when(lesionRepository.findById(ID_LESION)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lesionService.curarLesion(ID_LESION))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
    }