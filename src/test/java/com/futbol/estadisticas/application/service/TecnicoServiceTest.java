package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.ActualizarTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponseEstadisticas;
import com.futbol.estadisticas.application.port.mapper.PartidoMapper;
import com.futbol.estadisticas.application.port.mapper.TecnicoMapper;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.application.port.out.TecnicoRepositoryPort;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.Tecnico;
import com.futbol.estadisticas.domain.model.enums.Alineacion;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.Nacion;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TecnicoServiceTest {

    @Mock private TecnicoRepositoryPort tecnicoRepository;
    @Mock private TecnicoMapper tecnicoMapper;
    @Mock private PartidoMapper partidoMapper;
    @Mock private PartidoRepositoryPort partidoRepository;

    @InjectMocks
    private TecnicoService tecnicoService;

    private static final UUID ID_TECNICO = UUID.randomUUID();
    private static final UUID ID_EQUIPO = UUID.randomUUID();

    private Tecnico tecnico;
    private TecnicoResponse response;
    private CrearTecnicoRequest crearRequest;
    private ActualizarTecnicoRequest actualizarRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        tecnico = Tecnico.builder()
                .idPersonal(ID_TECNICO)
                .nombre("Mikel")
                .apellido("Arteta")
                .fechaNacimiento(LocalDate.of(1982, 3, 26))
                .nacionalidad(Nacion.ESPAÑA)
                .estiloJuego("Posicional")
                .alineacionFavorita("ALINEACION_433")
                .build();

        response = TecnicoResponse.builder()
                .idPersonal(ID_TECNICO)
                .nombre("Mikel")
                .apellido("Arteta")
                .build();

        crearRequest = CrearTecnicoRequest.builder()
                .nombre("Mikel")
                .apellido("Arteta")
                .fechaNacimiento(LocalDate.of(1982, 3, 26))
                .nacionalidad(Nacion.ESPAÑA)
                .estiloJuego("Posicional")
                .alineacionFavorita("ALINEACION_433")
                .build();

        actualizarRequest = ActualizarTecnicoRequest.builder()
                .nombre("Nuevo")
                .apellido("Apellido")
                .estiloJuego("Ofensivo")
                .alineacionFavorita("ALINEACION_4231")
                .build();

        pageable = PageRequest.of(0, 10);
    }

    // ──────────────────────────── BUSCAR ────────────────────────────

    @Nested
    @DisplayName("buscarTecnicos")
    class BuscarTecnicos {

        @Test
        @DisplayName("devuelve página vacía si el texto es nulo o vacío")
        void textoVacio() {
            assertThat(tecnicoService.buscarTecnicos(null, pageable)).isEmpty();
            assertThat(tecnicoService.buscarTecnicos("   ", pageable)).isEmpty();
            verify(tecnicoRepository, never()).buscarTecnicoPorNombre(any(), any());
        }

        @Test
        @DisplayName("delega en el repositorio y mapea")
        void delega() {
            Page<Tecnico> page = new PageImpl<>(List.of(tecnico));
            when(tecnicoRepository.buscarTecnicoPorNombre(eq("Arteta"), eq(pageable))).thenReturn(page);
            when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

            Page<TecnicoResponse> result = tecnicoService.buscarTecnicos("Arteta", pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(tecnicoRepository).buscarTecnicoPorNombre("Arteta", pageable);
        }
    }

    // ──────────────────────────── CREAR ────────────────────────────

    @Nested
    @DisplayName("crearTecnico")
    class Crear {

        @Test
        @DisplayName("mapea, guarda y devuelve response")
        void ok() {
            when(tecnicoMapper.toEntity(crearRequest)).thenReturn(tecnico);
            when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
            when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

            TecnicoResponse result = tecnicoService.crearTecnico(crearRequest);

            assertThat(result).isSameAs(response);
            verify(tecnicoRepository).save(tecnico);
        }
    }

    // ──────────────────────────── CONSULTAS ────────────────────────────

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("obtenerTecnicoPorId: retorna response")
        void porId() {
            when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.of(tecnico));
            when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

            assertThat(tecnicoService.obtenerTecnicoPorId(ID_TECNICO)).isSameAs(response);
        }

        @Test
        @DisplayName("obtenerTecnicoPorId: lanza si no existe")
        void porIdNoExiste() {
            when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tecnicoService.obtenerTecnicoPorId(ID_TECNICO))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("obtenerTodosTecnicos: mapea lista")
        void todos() {
            when(tecnicoRepository.findAll()).thenReturn(List.of(tecnico));
            when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

            assertThat(tecnicoService.obtenerTodosTecnicos()).hasSize(1);
        }

        @Test
        @DisplayName("obtenerTecnicoActualDeEquipo: retorna response")
        void actualDeEquipo() {
            when(tecnicoRepository.findTecnicoActualByEquipo(ID_EQUIPO)).thenReturn(Optional.of(tecnico));
            when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

            assertThat(tecnicoService.obtenerTecnicoActualDeEquipo(ID_EQUIPO)).isSameAs(response);
        }

        @Test
        @DisplayName("obtenerTecnicoActualDeEquipo: lanza si no hay técnico")
        void actualDeEquipoNoExiste() {
            when(tecnicoRepository.findTecnicoActualByEquipo(ID_EQUIPO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tecnicoService.obtenerTecnicoActualDeEquipo(ID_EQUIPO))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ──────────────────────────── PARTIDOS ────────────────────────────

    @Nested
    @DisplayName("obtenerPartidosPorTecnico")
    class Partidos {

        @Test
        @DisplayName("lanza si el técnico no existe")
        void noExiste() {
            when(tecnicoRepository.existsById(ID_TECNICO)).thenReturn(false);

            assertThatThrownBy(() -> tecnicoService.obtenerPartidosPorTecnico(ID_TECNICO))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("mapea los partidos del técnico")
        void ok() {
            Partido partido = Partido.builder()
                    .idPartido(UUID.randomUUID())
                    .estado(EstadoPartido.FINALIZADO)
                    .fechaYHora(LocalDateTime.now().minusDays(2))
                    .build();
            PartidoResponse responsePartido = PartidoResponse.builder()
                    .idPartido(partido.getIdPartido())
                    .estado(EstadoPartido.FINALIZADO)
                    .build();

            when(tecnicoRepository.existsById(ID_TECNICO)).thenReturn(true);
            when(partidoRepository.findPartidosBytecnico(ID_TECNICO)).thenReturn(List.of(partido));
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            List<PartidoResponse> result = tecnicoService.obtenerPartidosPorTecnico(ID_TECNICO);

            assertThat(result).hasSize(1);
        }
    }

    // ──────────────────────────── ESTADÍSTICAS ────────────────────────────

    @Nested
    @DisplayName("obtenerTecnicoConEstadisticas")
    class Estadisticas {

        @Test
        @DisplayName("lanza si el técnico no existe")
        void noExiste() {
            when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tecnicoService.obtenerTecnicoConEstadisticas(ID_TECNICO))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("delega en el mapper con partidos")
        void ok() {
            Partido partido = Partido.builder().idPartido(UUID.randomUUID()).build();
            TecnicoResponseEstadisticas responseEst =
                    TecnicoResponseEstadisticas.builder().build();

            when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.of(tecnico));
            when(partidoRepository.findPartidosBytecnico(ID_TECNICO)).thenReturn(List.of(partido));
            when(tecnicoMapper.toResponseConEstadisticas(tecnico, List.of(partido)))
                    .thenReturn(responseEst);

            assertThat(tecnicoService.obtenerTecnicoConEstadisticas(ID_TECNICO))
                    .isSameAs(responseEst);
        }
    }

    // ──────────────────────────── ACTUALIZAR ────────────────────────────

    @Nested
    @DisplayName("actualizarTecnico")
    class Actualizar {

        @Test
        @DisplayName("actualiza solo los campos no nulos")
        void actualizaCampos() {
            when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.of(tecnico));
            when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
            when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

            tecnicoService.actualizarTecnico(ID_TECNICO, actualizarRequest);

            assertThat(tecnico.getNombre()).isEqualTo("Nuevo");
            assertThat(tecnico.getApellido()).isEqualTo("Apellido");
            assertThat(tecnico.getEstiloJuego()).isEqualTo("Ofensivo");
            assertThat(tecnico.getAlineacionFavorita()).isEqualTo("ALINEACION_4231");
        }

        @Test
        @DisplayName("lanza si el técnico no existe")
        void noExiste() {
            when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tecnicoService.actualizarTecnico(ID_TECNICO, actualizarRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ──────────────────────────── ELIMINAR ────────────────────────────

    @Nested
    @DisplayName("eliminarTecnico")
    class Eliminar {

        @Test
        @DisplayName("elimina si existe")
        void ok() {
            when(tecnicoRepository.existsById(ID_TECNICO)).thenReturn(true);

            tecnicoService.eliminarTecnico(ID_TECNICO);

            verify(tecnicoRepository).deleteById(ID_TECNICO);
        }

        @Test
        @DisplayName("lanza si no existe")
        void noExiste() {
            when(tecnicoRepository.existsById(ID_TECNICO)).thenReturn(false);

            assertThatThrownBy(() -> tecnicoService.eliminarTecnico(ID_TECNICO))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}