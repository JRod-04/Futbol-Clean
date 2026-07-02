package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.ActualizarTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import com.futbol.estadisticas.application.port.mapper.TecnicoMapper;
import com.futbol.estadisticas.application.port.out.ClubRepositoryPort;
import com.futbol.estadisticas.application.port.out.TecnicoRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.Tecnico;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.Nacion;
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
class TecnicoServiceTest {

    @Mock
    private TecnicoRepositoryPort tecnicoRepository;

    @Mock
    private ClubRepositoryPort clubRepository;

    @Mock
    private TecnicoMapper tecnicoMapper;

    @InjectMocks
    private TecnicoService tecnicoService;

    private static final UUID ID_TECNICO = UUID.randomUUID();
    private static final UUID ID_TECNICO_2 = UUID.randomUUID();
    private static final UUID ID_CLUB = UUID.randomUUID();
    private static final UUID ID_CLUB_2 = UUID.randomUUID();

    private Tecnico tecnico;
    private Tecnico tecnico2;
    private Club club;
    private Club club2;
    private TecnicoResponse response;
    private TecnicoResponse response2;
    private CrearTecnicoRequest crearRequest;
    private ActualizarTecnicoRequest actualizarRequest;

    @BeforeEach
    void setUp() {
        club = Club.builder()
                .idEquipo(ID_CLUB)
                .nombre("FC Barcelona")
                .nombreCorto("Barça")
                .fechaFundacion(LocalDate.of(1899, 11, 29))
                .contratos(new ArrayList<>())
                .build();

        club2 = Club.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("Real Madrid")
                .nombreCorto("Madrid")
                .fechaFundacion(LocalDate.of(1902, 3, 6))
                .contratos(new ArrayList<>())
                .build();

        tecnico = Tecnico.builder()
                .idPersonal(ID_TECNICO)
                .nombre("Pep")
                .apellido("Guardiola")
                .fechaNacimiento(LocalDate.of(1971, 1, 18))
                .nacionalidad(Nacion.ESPAÑA)
                .tipoPersonal(TipoPersonal.TECNICO)
                .estiloJuego("Tiki-taka")
                .alineacionFavorita("4-3-3")
                .contratos(new ArrayList<>())
                .build();

        Contrato contrato = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .club(club)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .sueldo(10_000_000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();
        tecnico.agregarContrato(contrato);
        club.asignarTecnico(tecnico);

        tecnico2 = Tecnico.builder()
                .idPersonal(ID_TECNICO_2)
                .nombre("Carlo")
                .apellido("Ancelotti")
                .fechaNacimiento(LocalDate.of(1959, 6, 10))
                .nacionalidad(Nacion.ITALIA)
                .tipoPersonal(TipoPersonal.TECNICO)
                .estiloJuego("Contragolpe")
                .alineacionFavorita("4-4-2")
                .contratos(new ArrayList<>())
                .build();

        response = TecnicoResponse.builder()
                .idPersonal(ID_TECNICO)
                .nombre("Pep")
                .apellido("Guardiola")
                .nombreCompleto("Pep Guardiola")
                .fechaNacimiento(LocalDate.of(1971, 1, 18))
                .nacionalidad(Nacion.ESPAÑA)
                .estiloJuego("Tiki-taka")
                .alineacionFavorita("4-3-3")
                .clubActual("FC Barcelona")
                .idClubActual(ID_CLUB)
                .build();

        response2 = TecnicoResponse.builder()
                .idPersonal(ID_TECNICO_2)
                .nombre("Carlo")
                .apellido("Ancelotti")
                .nombreCompleto("Carlo Ancelotti")
                .fechaNacimiento(LocalDate.of(1959, 6, 10))
                .nacionalidad(Nacion.ITALIA)
                .estiloJuego("Contragolpe")
                .alineacionFavorita("4-4-2")
                .build();

        crearRequest = CrearTecnicoRequest.builder()
                .nombre("Nuevo")
                .apellido("Tecnico")
                .fechaNacimiento(LocalDate.of(1980, 1, 1))
                .nacionalidad(Nacion.ARGENTINA)
                .estiloJuego("Presión alta")
                .alineacionFavorita("4-2-3-1")
                .build();

        actualizarRequest = ActualizarTecnicoRequest.builder()
                .nombre("Pep Actualizado")
                .apellido("Guardiola Actualizado")
                .estiloJuego("Tiki-taka moderno")
                .alineacionFavorita("3-4-3")
                .build();
    }

    @Test
    @DisplayName("crearTecnico: debe crear un nuevo técnico correctamente")
    void testCrearTecnico() {
        Tecnico nuevoTecnico = Tecnico.builder()
                .idPersonal(UUID.randomUUID())
                .nombre("Nuevo")
                .apellido("Tecnico")
                .fechaNacimiento(LocalDate.of(1980, 1, 1))
                .nacionalidad(Nacion.ARGENTINA)
                .tipoPersonal(TipoPersonal.TECNICO)
                .estiloJuego("Presión alta")
                .alineacionFavorita("4-2-3-1")
                .contratos(new ArrayList<>())
                .build();

        when(tecnicoMapper.toEntity(crearRequest)).thenReturn(nuevoTecnico);
        when(tecnicoRepository.save(nuevoTecnico)).thenReturn(nuevoTecnico);
        when(tecnicoMapper.toResponse(nuevoTecnico)).thenReturn(response2);

        TecnicoResponse result = tecnicoService.crearTecnico(crearRequest);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Carlo");
        assertThat(result.apellido()).isEqualTo("Ancelotti");

        verify(tecnicoMapper).toEntity(crearRequest);
        verify(tecnicoRepository).save(nuevoTecnico);
        verify(tecnicoMapper).toResponse(nuevoTecnico);
    }

    @Test
    @DisplayName("obtenerTecnicoPorId: debe retornar el técnico cuando existe")
    void testObtenerTecnicoPorId_Existe() {
        when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.of(tecnico));
        when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

        TecnicoResponse result = tecnicoService.obtenerTecnicoPorId(ID_TECNICO);

        assertThat(result).isNotNull();
        assertThat(result.idPersonal()).isEqualTo(ID_TECNICO);
        assertThat(result.nombre()).isEqualTo("Pep");
        assertThat(result.apellido()).isEqualTo("Guardiola");
        assertThat(result.nombreCompleto()).isEqualTo("Pep Guardiola");
        assertThat(result.estiloJuego()).isEqualTo("Tiki-taka");
        assertThat(result.alineacionFavorita()).isEqualTo("4-3-3");
        assertThat(result.clubActual()).isEqualTo("FC Barcelona");
        assertThat(result.idClubActual()).isEqualTo(ID_CLUB);

        verify(tecnicoRepository).findById(ID_TECNICO);
        verify(tecnicoMapper).toResponse(tecnico);
    }

    @Test
    @DisplayName("obtenerTecnicoPorId: debe lanzar excepción cuando el técnico no existe")
    void testObtenerTecnicoPorId_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(tecnicoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tecnicoService.obtenerTecnicoPorId(idInexistente))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Técnico no encontrado con id: " + idInexistente);

        verify(tecnicoRepository).findById(idInexistente);
        verify(tecnicoMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("obtenerTodosTecnicos: debe retornar todos los técnicos")
    void testObtenerTodosTecnicos() {
        List<Tecnico> tecnicos = List.of(tecnico);
        List<TecnicoResponse> responses = List.of(response);

        when(tecnicoRepository.findAll()).thenReturn(tecnicos);
        when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

        List<TecnicoResponse> result = tecnicoService.obtenerTodosTecnicos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Pep");
        assertThat(result.get(0).apellido()).isEqualTo("Guardiola");

        verify(tecnicoRepository).findAll();
        verify(tecnicoMapper).toResponse(tecnico);
    }

    @Test
    @DisplayName("obtenerTodosTecnicos: debe retornar lista vacía cuando no hay técnicos")
    void testObtenerTodosTecnicos_Vacio() {
        when(tecnicoRepository.findAll()).thenReturn(List.of());

        List<TecnicoResponse> result = tecnicoService.obtenerTodosTecnicos();

        assertThat(result).isEmpty();
        verify(tecnicoRepository).findAll();
        verify(tecnicoMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("obtenerTecnicoActualDeClub: debe retornar el técnico actual del club")
    void testObtenerTecnicoActualDeClub() {
        when(tecnicoRepository.findTecnicoActualByClub(ID_CLUB)).thenReturn(Optional.of(tecnico));
        when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

        TecnicoResponse result = tecnicoService.obtenerTecnicoActualDeClub(ID_CLUB);

        assertThat(result).isNotNull();
        assertThat(result.idPersonal()).isEqualTo(ID_TECNICO);
        assertThat(result.clubActual()).isEqualTo("FC Barcelona");
        assertThat(result.idClubActual()).isEqualTo(ID_CLUB);

        verify(tecnicoRepository).findTecnicoActualByClub(ID_CLUB);
        verify(tecnicoMapper).toResponse(tecnico);
    }

    @Test
    @DisplayName("obtenerTecnicoActualDeClub: debe lanzar excepción cuando el club no tiene técnico")
    void testObtenerTecnicoActualDeClub_SinTecnico() {
        when(tecnicoRepository.findTecnicoActualByClub(ID_CLUB_2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tecnicoService.obtenerTecnicoActualDeClub(ID_CLUB_2))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("No hay técnico asignado al club con id: " + ID_CLUB_2);

        verify(tecnicoRepository).findTecnicoActualByClub(ID_CLUB_2);
        verify(tecnicoMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("actualizarTecnico: debe actualizar un técnico existente")
    void testActualizarTecnico() {
        when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.of(tecnico));
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

        TecnicoResponse result = tecnicoService.actualizarTecnico(ID_TECNICO, actualizarRequest);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Pep");

        assertThat(tecnico.getNombre()).isEqualTo("Pep Actualizado");
        assertThat(tecnico.getApellido()).isEqualTo("Guardiola Actualizado");
        assertThat(tecnico.getEstiloJuego()).isEqualTo("Tiki-taka moderno");
        assertThat(tecnico.getAlineacionFavorita()).isEqualTo("3-4-3");

        verify(tecnicoRepository).findById(ID_TECNICO);
        verify(tecnicoRepository).save(tecnico);
        verify(tecnicoMapper).toResponse(tecnico);
    }

    @Test
    @DisplayName("actualizarTecnico: debe lanzar excepción cuando el técnico no existe")
    void testActualizarTecnico_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(tecnicoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tecnicoService.actualizarTecnico(idInexistente, actualizarRequest))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Técnico no encontrado con id: " + idInexistente);

        verify(tecnicoRepository).findById(idInexistente);
        verify(tecnicoRepository, never()).save(any());
        verify(tecnicoMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("actualizarTecnico: debe actualizar solo los campos proporcionados")
    void testActualizarTecnico_Parcial() {
        ActualizarTecnicoRequest requestSoloEstilo = ActualizarTecnicoRequest.builder()
                .estiloJuego("Nuevo estilo")
                .build();

        when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.of(tecnico));
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        when(tecnicoMapper.toResponse(tecnico)).thenReturn(response);

        tecnicoService.actualizarTecnico(ID_TECNICO, requestSoloEstilo);

        assertThat(tecnico.getEstiloJuego()).isEqualTo("Nuevo estilo");
        assertThat(tecnico.getNombre()).isEqualTo("Pep");
        assertThat(tecnico.getApellido()).isEqualTo("Guardiola");
        assertThat(tecnico.getAlineacionFavorita()).isEqualTo("4-3-3");

        verify(tecnicoRepository).findById(ID_TECNICO);
        verify(tecnicoRepository).save(tecnico);
    }


    @Test
    @DisplayName("asignarTecnicoAClub: debe asignar un técnico a un club correctamente")
    void testAsignarTecnicoAClub() {
    TecnicoResponse responseConClub = TecnicoResponse.builder()
            .idPersonal(ID_TECNICO_2)
            .nombre("Carlo")
            .apellido("Ancelotti")
            .nombreCompleto("Carlo Ancelotti")
            .fechaNacimiento(LocalDate.of(1959, 6, 10))
            .nacionalidad(Nacion.ITALIA)
            .estiloJuego("Contragolpe")
            .alineacionFavorita("4-4-2")
            .clubActual("Real Madrid")
            .idClubActual(ID_CLUB_2)
            .build();

    when(tecnicoRepository.findById(ID_TECNICO_2)).thenReturn(Optional.of(tecnico2));
    when(clubRepository.findById(ID_CLUB_2)).thenReturn(Optional.of(club2));
    when(clubRepository.save(club2)).thenReturn(club2);
    when(tecnicoRepository.save(tecnico2)).thenReturn(tecnico2);
    when(tecnicoMapper.toResponse(tecnico2)).thenReturn(responseConClub);

    TecnicoResponse result = tecnicoService.asignarTecnicoAClub(ID_TECNICO_2, ID_CLUB_2);

    assertThat(result).isNotNull();
    assertThat(result.idPersonal()).isEqualTo(ID_TECNICO_2);
    assertThat(result.clubActual()).isEqualTo("Real Madrid");
    assertThat(result.idClubActual()).isEqualTo(ID_CLUB_2);

    assertThat(club2.getTecnicoActual()).isNotNull();
    assertThat(club2.getTecnicoActual().getIdPersonal()).isEqualTo(ID_TECNICO_2);

    verify(tecnicoRepository).findById(ID_TECNICO_2);
    verify(clubRepository).findById(ID_CLUB_2);
    verify(clubRepository).save(club2);
    verify(tecnicoRepository).save(tecnico2);
    verify(tecnicoMapper).toResponse(tecnico2);
}

    @Test
    @DisplayName("asignarTecnicoAClub: debe lanzar excepción cuando el técnico no existe")
    void testAsignarTecnicoAClub_TecnicoNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(tecnicoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tecnicoService.asignarTecnicoAClub(idInexistente, ID_CLUB))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Técnico no encontrado con id: " + idInexistente);

        verify(tecnicoRepository).findById(idInexistente);
        verify(clubRepository, never()).findById(any());
        verify(clubRepository, never()).save(any());
        verify(tecnicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("asignarTecnicoAClub: debe lanzar excepción cuando el club no existe")
    void testAsignarTecnicoAClub_ClubNoExiste() {
        UUID idClubInexistente = UUID.randomUUID();
        when(tecnicoRepository.findById(ID_TECNICO)).thenReturn(Optional.of(tecnico));
        when(clubRepository.findById(idClubInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tecnicoService.asignarTecnicoAClub(ID_TECNICO, idClubInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Club no encontrado con id: " + idClubInexistente);

        verify(tecnicoRepository).findById(ID_TECNICO);
        verify(clubRepository).findById(idClubInexistente);
        verify(clubRepository, never()).save(any());
        verify(tecnicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("desvincularTecnicoDeClub: debe desvincular el técnico del club correctamente")
    void testDesvincularTecnicoDeClub() {
        when(clubRepository.findById(ID_CLUB)).thenReturn(Optional.of(club));
        when(clubRepository.save(club)).thenReturn(club);

        tecnicoService.desvincularTecnicoDeClub(ID_CLUB);

        assertThat(club.getTecnicoActual()).isNull();
        verify(clubRepository).findById(ID_CLUB);
        verify(clubRepository).save(club);
    }

    @Test
    @DisplayName("desvincularTecnicoDeClub: debe lanzar excepción cuando el club no existe")
    void testDesvincularTecnicoDeClub_ClubNoExiste() {
        UUID idClubInexistente = UUID.randomUUID();
        when(clubRepository.findById(idClubInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tecnicoService.desvincularTecnicoDeClub(idClubInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Club no encontrado con id: " + idClubInexistente);

        verify(clubRepository).findById(idClubInexistente);
        verify(clubRepository, never()).save(any());
    }

    @Test
    @DisplayName("desvincularTecnicoDeClub: debe lanzar excepción cuando el club no tiene técnico")
    void testDesvincularTecnicoDeClub_SinTecnico() {
        Club clubSinTecnico = Club.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("Real Madrid")
                .tecnicoActual(null)
                .build();

        when(clubRepository.findById(ID_CLUB_2)).thenReturn(Optional.of(clubSinTecnico));

        assertThatThrownBy(() -> tecnicoService.desvincularTecnicoDeClub(ID_CLUB_2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El club no tiene técnico asignado");

        verify(clubRepository).findById(ID_CLUB_2);
        verify(clubRepository, never()).save(any());
    }

    @Test
    @DisplayName("eliminarTecnico: debe eliminar un técnico existente")
    void testEliminarTecnico() {
        when(tecnicoRepository.existsById(ID_TECNICO)).thenReturn(true);
        doNothing().when(tecnicoRepository).deleteById(ID_TECNICO);

        tecnicoService.eliminarTecnico(ID_TECNICO);

        verify(tecnicoRepository).existsById(ID_TECNICO);
        verify(tecnicoRepository).deleteById(ID_TECNICO);
    }

    @Test
    @DisplayName("eliminarTecnico: debe lanzar excepción cuando el técnico no existe")
    void testEliminarTecnico_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(tecnicoRepository.existsById(idInexistente)).thenReturn(false);

        assertThatThrownBy(() -> tecnicoService.eliminarTecnico(idInexistente))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Técnico no encontrado con id: " + idInexistente);

        verify(tecnicoRepository).existsById(idInexistente);
        verify(tecnicoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("asignarTecnicoAClub: debe desvincular al técnico anterior del club si existe")
    void testAsignarTecnicoAClub_ReemplazarTecnico() {
        Tecnico tecnicoAnterior = Tecnico.builder()
                .idPersonal(UUID.randomUUID())
                .nombre("Anterior")
                .apellido("Tecnico")
                .build();

        Club clubConTecnico = Club.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("Real Madrid")
                .tecnicoActual(tecnicoAnterior)
                .build();

        when(tecnicoRepository.findById(ID_TECNICO_2)).thenReturn(Optional.of(tecnico2));
        when(clubRepository.findById(ID_CLUB_2)).thenReturn(Optional.of(clubConTecnico));
        when(clubRepository.save(clubConTecnico)).thenReturn(clubConTecnico);
        when(tecnicoRepository.save(tecnico2)).thenReturn(tecnico2);
        when(tecnicoMapper.toResponse(tecnico2)).thenReturn(response2);

        tecnicoService.asignarTecnicoAClub(ID_TECNICO_2, ID_CLUB_2);

        assertThat(clubConTecnico.getTecnicoActual()).isEqualTo(tecnico2);
        verify(clubRepository).save(clubConTecnico);
        verify(tecnicoRepository).save(tecnico2);
    }
}