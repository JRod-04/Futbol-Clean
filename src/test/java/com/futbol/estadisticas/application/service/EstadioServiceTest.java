package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.ActualizarEstadioRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearEstadioRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadioResponse;
import com.futbol.estadisticas.application.port.mapper.EstadioMapper;
import com.futbol.estadisticas.application.port.out.ClubRepositoryPort;
import com.futbol.estadisticas.application.port.out.EstadioRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Estadio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadioServiceTest {

    @Mock
    private EstadioRepositoryPort estadioRepository;

    @Mock
    private ClubRepositoryPort clubRepository;

    @Mock
    private EstadioMapper estadioMapper;

    @InjectMocks
    private EstadioService estadioService;

    private static final UUID ID_ESTADIO = UUID.randomUUID();
    private static final UUID ID_ESTADIO_2 = UUID.randomUUID();
    private static final UUID ID_CLUB = UUID.randomUUID();
    private static final UUID ID_CLUB_2 = UUID.randomUUID();

    private Estadio estadio;
    private Estadio estadio2;
    private Club club;
    private Club club2;
    private EstadioResponse response;
    private EstadioResponse response2;
    private CrearEstadioRequest crearRequest;
    private ActualizarEstadioRequest actualizarRequest;

    @BeforeEach
    void setUp() {
        // Crear clubes
        club = Club.builder()
                .idEquipo(ID_CLUB)
                .nombre("FC Barcelona")
                .nombreCorto("Barça")
                .fechaFundacion(LocalDate.of(1899, 11, 29))
                .build();

        club2 = Club.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("Real Madrid")
                .nombreCorto("Madrid")
                .fechaFundacion(LocalDate.of(1902, 3, 6))
                .build();

        // Crear estadios
        estadio = Estadio.builder()
                .idEstadio(ID_ESTADIO)
                .nombre("Camp Nou")
                .direccion("C/ Arístides Maillol, 12, 08028 Barcelona")
                .capacidad(99354)
                .fechaFundacion(LocalDate.of(1957, 9, 24))
                .clubPrincipal(club)
                .build();

        estadio2 = Estadio.builder()
                .idEstadio(ID_ESTADIO_2)
                .nombre("Santiago Bernabéu")
                .direccion("Av. de Concha Espina, 1, 28036 Madrid")
                .capacidad(81044)
                .fechaFundacion(LocalDate.of(1947, 12, 14))
                .build();

        // Crear responses
        response = EstadioResponse.builder()
                .idEstadio(ID_ESTADIO)
                .nombre("Camp Nou")
                .direccion("C/ Arístides Maillol, 12, 08028 Barcelona")
                .capacidad(99354)
                .fechaFundacion(LocalDate.of(1957, 9, 24))
                .descripcionCompleta("Camp Nou - Capacidad: 99354 - Fundado: 1957")
                .clubPrincipal("FC Barcelona")
                .idClubPrincipal(ID_CLUB)
                .build();

        response2 = EstadioResponse.builder()
                .idEstadio(ID_ESTADIO_2)
                .nombre("Santiago Bernabéu")
                .direccion("Av. de Concha Espina, 1, 28036 Madrid")
                .capacidad(81044)
                .fechaFundacion(LocalDate.of(1947, 12, 14))
                .descripcionCompleta("Santiago Bernabéu - Capacidad: 81044 - Fundado: 1947")
                .build();

        // Crear requests
        crearRequest = new CrearEstadioRequest(
                "Estadio Nuevo",
                "Calle Nueva 123",
                50000,
                LocalDate.of(2000, 1, 1)
        );

        actualizarRequest = new ActualizarEstadioRequest(
                "Camp Nou Actualizado",
                "Calle Nueva 456",
                105000
        );
    }

    // ── TESTS: CREAR ──

    @Test
    @DisplayName("crearEstadio: debe crear un nuevo estadio correctamente")
    void testCrearEstadio() {
        Estadio nuevoEstadio = Estadio.builder()
                .idEstadio(UUID.randomUUID())
                .nombre("Estadio Nuevo")
                .direccion("Calle Nueva 123")
                .capacidad(50000)
                .fechaFundacion(LocalDate.of(2000, 1, 1))
                .build();

        when(estadioMapper.toEntity(crearRequest)).thenReturn(nuevoEstadio);
        when(estadioRepository.save(nuevoEstadio)).thenReturn(nuevoEstadio);
        when(estadioMapper.toResponse(nuevoEstadio)).thenReturn(response);

        EstadioResponse result = estadioService.crearEstadio(crearRequest);

        assertThat(result).isNotNull();
        assertThat(result.idEstadio()).isEqualTo(ID_ESTADIO);
        assertThat(result.nombre()).isEqualTo("Camp Nou");
        assertThat(result.capacidad()).isEqualTo(99354);

        verify(estadioMapper).toEntity(crearRequest);
        verify(estadioRepository).save(nuevoEstadio);
        verify(estadioMapper).toResponse(nuevoEstadio);
    }

    // ── TESTS: OBTENER POR ID ──

    @Test
    @DisplayName("obtenerEstadioPorId: debe retornar el estadio cuando existe")
    void testObtenerEstadioPorId_Existe() {
        when(estadioRepository.findById(ID_ESTADIO)).thenReturn(Optional.of(estadio));
        when(estadioMapper.toResponse(estadio)).thenReturn(response);

        EstadioResponse result = estadioService.obtenerEstadioPorId(ID_ESTADIO);

        assertThat(result).isNotNull();
        assertThat(result.idEstadio()).isEqualTo(ID_ESTADIO);
        assertThat(result.nombre()).isEqualTo("Camp Nou");
        assertThat(result.capacidad()).isEqualTo(99354);
        assertThat(result.clubPrincipal()).isEqualTo("FC Barcelona");
        assertThat(result.idClubPrincipal()).isEqualTo(ID_CLUB);

        verify(estadioRepository).findById(ID_ESTADIO);
        verify(estadioMapper).toResponse(estadio);
    }

    @Test
    @DisplayName("obtenerEstadioPorId: debe lanzar excepción cuando el estadio no existe")
    void testObtenerEstadioPorId_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(estadioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estadioService.obtenerEstadioPorId(idInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estadio no encontrado con id: " + idInexistente);

        verify(estadioRepository).findById(idInexistente);
        verify(estadioMapper, never()).toResponse(any());
    }

    // ── TESTS: OBTENER TODOS ──

    @Test
    @DisplayName("obtenerTodosLosEstadios: debe retornar todos los estadios")
    void testObtenerTodosLosEstadios() {
        List<Estadio> estadios = List.of(estadio, estadio2);
        List<EstadioResponse> responses = List.of(response, response2);

        when(estadioRepository.findAll()).thenReturn(estadios);
        when(estadioMapper.toResponse(estadio)).thenReturn(response);
        when(estadioMapper.toResponse(estadio2)).thenReturn(response2);

        List<EstadioResponse> result = estadioService.obtenerTodosLosEstadios();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(EstadioResponse::nombre)
                .containsExactlyInAnyOrder("Camp Nou", "Santiago Bernabéu");
        assertThat(result)
                .extracting(EstadioResponse::capacidad)
                .containsExactlyInAnyOrder(99354, 81044);

        verify(estadioRepository).findAll();
        verify(estadioMapper, times(2)).toResponse(any(Estadio.class));
    }

    @Test
    @DisplayName("obtenerTodosLosEstadios: debe retornar lista vacía cuando no hay estadios")
    void testObtenerTodosLosEstadios_Vacio() {
        when(estadioRepository.findAll()).thenReturn(List.of());

        List<EstadioResponse> result = estadioService.obtenerTodosLosEstadios();

        assertThat(result).isEmpty();
        verify(estadioRepository).findAll();
        verify(estadioMapper, never()).toResponse(any());
    }

    // ── TESTS: ACTUALIZAR ──

    @Test
    @DisplayName("actualizarEstadio: debe actualizar un estadio existente")
    void testActualizarEstadio() {
        when(estadioRepository.findById(ID_ESTADIO)).thenReturn(Optional.of(estadio));
        when(estadioRepository.save(estadio)).thenReturn(estadio);
        when(estadioMapper.toResponse(estadio)).thenReturn(response);

        EstadioResponse result = estadioService.actualizarEstadio(ID_ESTADIO, actualizarRequest);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Camp Nou");
        assertThat(estadio.getNombre()).isEqualTo("Camp Nou Actualizado");
        assertThat(estadio.getDireccion()).isEqualTo("Calle Nueva 456");
        assertThat(estadio.getCapacidad()).isEqualTo(105000);

        verify(estadioRepository).findById(ID_ESTADIO);
        verify(estadioRepository).save(estadio);
        verify(estadioMapper).toResponse(estadio);
    }

    @Test
    @DisplayName("actualizarEstadio: debe lanzar excepción cuando el estadio no existe")
    void testActualizarEstadio_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(estadioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estadioService.actualizarEstadio(idInexistente, actualizarRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estadio no encontrado con id: " + idInexistente);

        verify(estadioRepository).findById(idInexistente);
        verify(estadioRepository, never()).save(any());
        verify(estadioMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("actualizarEstadio: debe actualizar solo los campos proporcionados")
    void testActualizarEstadio_Parcial() {
        // Actualizar solo nombre
        ActualizarEstadioRequest requestSoloNombre = new ActualizarEstadioRequest(
                "Nuevo Nombre",
                null,
                null
        );

        when(estadioRepository.findById(ID_ESTADIO_2)).thenReturn(Optional.of(estadio2));
        when(estadioRepository.save(estadio2)).thenReturn(estadio2);

        EstadioResponse responseSoloNombre = EstadioResponse.builder()
                .idEstadio(ID_ESTADIO_2)
                .nombre("Nuevo Nombre")
                .direccion("Av. de Concha Espina, 1, 28036 Madrid")
                .capacidad(81044)
                .fechaFundacion(LocalDate.of(1947, 12, 14))
                .descripcionCompleta("Nuevo Nombre - Capacidad: 81044 - Fundado: 1947")
                .build();

        when(estadioMapper.toResponse(estadio2)).thenReturn(responseSoloNombre);

        EstadioResponse result = estadioService.actualizarEstadio(ID_ESTADIO_2, requestSoloNombre);

        assertThat(result.nombre()).isEqualTo("Nuevo Nombre");
        assertThat(result.direccion()).isEqualTo("Av. de Concha Espina, 1, 28036 Madrid");
        assertThat(result.capacidad()).isEqualTo(81044);

        verify(estadioRepository).findById(ID_ESTADIO_2);
        verify(estadioRepository).save(estadio2);
    }

    // ── TESTS: ASIGNAR ESTADIO A CLUB ──

    @Test
    @DisplayName("asignarEstadioAClub: debe asignar un estadio a un club correctamente")
    void testAsignarEstadioAClub() {
        Estadio estadioSinClub = Estadio.builder()
                .idEstadio(ID_ESTADIO_2)
                .nombre("Estadio Olímpico")
                .direccion("Av. del Estadio, 1")
                .capacidad(75000)
                .fechaFundacion(LocalDate.of(1992, 7, 25))
                .build();

        EstadioResponse responseConClub = EstadioResponse.builder()
                .idEstadio(ID_ESTADIO_2)
                .nombre("Estadio Olímpico")
                .direccion("Av. del Estadio, 1")
                .capacidad(75000)
                .fechaFundacion(LocalDate.of(1992, 7, 25))
                .descripcionCompleta("Estadio Olímpico - Capacidad: 75000 - Fundado: 1992")
                .clubPrincipal("Real Madrid")
                .idClubPrincipal(ID_CLUB_2)
                .build();

        when(estadioRepository.findById(ID_ESTADIO_2)).thenReturn(Optional.of(estadioSinClub));
        when(clubRepository.findById(ID_CLUB_2)).thenReturn(Optional.of(club2));
        when(clubRepository.save(club2)).thenReturn(club2);
        when(estadioRepository.save(estadioSinClub)).thenReturn(estadioSinClub);
        when(estadioMapper.toResponse(estadioSinClub)).thenReturn(responseConClub);

        EstadioResponse result = estadioService.asignarEstadioAClub(ID_ESTADIO_2, ID_CLUB_2);

        assertThat(result).isNotNull();
        assertThat(result.idEstadio()).isEqualTo(ID_ESTADIO_2);
        assertThat(result.clubPrincipal()).isEqualTo("Real Madrid");
        assertThat(result.idClubPrincipal()).isEqualTo(ID_CLUB_2);

        assertThat(estadioSinClub.getClubPrincipal()).isNotNull();
        assertThat(estadioSinClub.getClubPrincipal().getIdEquipo()).isEqualTo(ID_CLUB_2);
        assertThat(club2.getEstadio()).isEqualTo(estadioSinClub);

        verify(estadioRepository).findById(ID_ESTADIO_2);
        verify(clubRepository).findById(ID_CLUB_2);
        verify(clubRepository).save(club2);
        verify(estadioRepository).save(estadioSinClub);
        verify(estadioMapper).toResponse(estadioSinClub);
    }

    @Test
    @DisplayName("asignarEstadioAClub: debe lanzar excepción cuando el estadio no existe")
    void testAsignarEstadioAClub_EstadioNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(estadioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estadioService.asignarEstadioAClub(idInexistente, ID_CLUB))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estadio no encontrado con id: " + idInexistente);

        verify(estadioRepository).findById(idInexistente);
        verify(clubRepository, never()).findById(any());
        verify(clubRepository, never()).save(any());
        verify(estadioRepository, never()).save(any());
    }

    @Test
    @DisplayName("asignarEstadioAClub: debe lanzar excepción cuando el club no existe")
    void testAsignarEstadioAClub_ClubNoExiste() {
        UUID idClubInexistente = UUID.randomUUID();
        when(estadioRepository.findById(ID_ESTADIO)).thenReturn(Optional.of(estadio));
        when(clubRepository.findById(idClubInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estadioService.asignarEstadioAClub(ID_ESTADIO, idClubInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Club no encontrado con id: " + idClubInexistente);

        verify(estadioRepository).findById(ID_ESTADIO);
        verify(clubRepository).findById(idClubInexistente);
        verify(clubRepository, never()).save(any());
        verify(estadioRepository, never()).save(any());
    }

    // ── TESTS: CALCULAR PORCENTAJE OCUPACIÓN ──

    @Test
    @DisplayName("calcularPorcentajeOcupacion: debe calcular correctamente el porcentaje de ocupación")
    void testCalcularPorcentajeOcupacion() {
        when(estadioRepository.findById(ID_ESTADIO)).thenReturn(Optional.of(estadio));

        double porcentaje50 = estadioService.calcularPorcentajeOcupacion(ID_ESTADIO, 50000);
        assertThat(porcentaje50).isEqualTo(50.325100146949296);

        double porcentajeCompleto = estadioService.calcularPorcentajeOcupacion(ID_ESTADIO, 99354);
        assertThat(porcentajeCompleto).isEqualTo(100.0);

        double porcentajeVacio = estadioService.calcularPorcentajeOcupacion(ID_ESTADIO, 0);
        assertThat(porcentajeVacio).isEqualTo(0.0);

        double porcentajeExcedido = estadioService.calcularPorcentajeOcupacion(ID_ESTADIO, 150000);
        assertThat(porcentajeExcedido).isGreaterThan(100.0);

        verify(estadioRepository, times(4)).findById(ID_ESTADIO);
    }

    @Test
    @DisplayName("calcularPorcentajeOcupacion: debe lanzar excepción cuando el estadio no existe")
    void testCalcularPorcentajeOcupacion_EstadioNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(estadioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estadioService.calcularPorcentajeOcupacion(idInexistente, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estadio no encontrado con id: " + idInexistente);

        verify(estadioRepository).findById(idInexistente);
    }

    // ── TESTS: ELIMINAR ──

    @Test
    @DisplayName("eliminarEstadio: debe eliminar un estadio existente")
    void testEliminarEstadio() {
        when(estadioRepository.existsById(ID_ESTADIO)).thenReturn(true);
        doNothing().when(estadioRepository).deleteById(ID_ESTADIO);

        estadioService.eliminarEstadio(ID_ESTADIO);

        verify(estadioRepository).existsById(ID_ESTADIO);
        verify(estadioRepository).deleteById(ID_ESTADIO);
    }

    @Test
    @DisplayName("eliminarEstadio: debe lanzar excepción cuando el estadio no existe")
    void testEliminarEstadio_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(estadioRepository.existsById(idInexistente)).thenReturn(false);

        assertThatThrownBy(() -> estadioService.eliminarEstadio(idInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estadio no encontrado con id: " + idInexistente);

        verify(estadioRepository).existsById(idInexistente);
        verify(estadioRepository, never()).deleteById(any());
    }

    // ── TESTS: CASOS ESPECIALES ──

    @Test
    @DisplayName("crearEstadio: debe crear estadio sin club asignado")
    void testCrearEstadio_SinClub() {
        CrearEstadioRequest request = new CrearEstadioRequest(
                "Estadio Sin Club",
                "Calle Sola 1",
                30000,
                LocalDate.of(2010, 5, 15)
        );

        Estadio nuevoEstadio = Estadio.builder()
                .idEstadio(UUID.randomUUID())
                .nombre("Estadio Sin Club")
                .direccion("Calle Sola 1")
                .capacidad(30000)
                .fechaFundacion(LocalDate.of(2010, 5, 15))
                .build();

        EstadioResponse responseSinClub = EstadioResponse.builder()
                .idEstadio(nuevoEstadio.getIdEstadio())
                .nombre("Estadio Sin Club")
                .direccion("Calle Sola 1")
                .capacidad(30000)
                .fechaFundacion(LocalDate.of(2010, 5, 15))
                .descripcionCompleta("Estadio Sin Club - Capacidad: 30000 - Fundado: 2010")
                .build();

        when(estadioMapper.toEntity(request)).thenReturn(nuevoEstadio);
        when(estadioRepository.save(nuevoEstadio)).thenReturn(nuevoEstadio);
        when(estadioMapper.toResponse(nuevoEstadio)).thenReturn(responseSinClub);

        EstadioResponse result = estadioService.crearEstadio(request);

        assertThat(result).isNotNull();
        assertThat(result.idClubPrincipal()).isNull();
        assertThat(result.clubPrincipal()).isNull();
        assertThat(result.descripcionCompleta()).contains("Estadio Sin Club");
        assertThat(result.descripcionCompleta()).contains("30000");

        verify(estadioMapper).toEntity(request);
        verify(estadioRepository).save(nuevoEstadio);
    }

    @Test
    @DisplayName("asignarEstadioAClub: debe mantener la información actualizada en ambos lados")
    void testAsignarEstadioAClub_Bidireccional() {
        Estadio estadioSinClub = Estadio.builder()
                .idEstadio(ID_ESTADIO_2)
                .nombre("Estadio Olímpico")
                .direccion("Av. del Estadio, 1")
                .capacidad(75000)
                .fechaFundacion(LocalDate.of(1992, 7, 25))
                .build();

        EstadioResponse responseConClub = EstadioResponse.builder()
                .idEstadio(ID_ESTADIO_2)
                .nombre("Estadio Olímpico")
                .direccion("Av. del Estadio, 1")
                .capacidad(75000)
                .fechaFundacion(LocalDate.of(1992, 7, 25))
                .descripcionCompleta("Estadio Olímpico - Capacidad: 75000 - Fundado: 1992")
                .clubPrincipal("FC Barcelona")
                .idClubPrincipal(ID_CLUB)
                .build();

        when(estadioRepository.findById(ID_ESTADIO_2)).thenReturn(Optional.of(estadioSinClub));
        when(clubRepository.findById(ID_CLUB)).thenReturn(Optional.of(club));
        when(clubRepository.save(club)).thenReturn(club);
        when(estadioRepository.save(estadioSinClub)).thenReturn(estadioSinClub);
        when(estadioMapper.toResponse(estadioSinClub)).thenReturn(responseConClub);

        EstadioResponse result = estadioService.asignarEstadioAClub(ID_ESTADIO_2, ID_CLUB);

        assertThat(result).isNotNull();
        assertThat(result.idEstadio()).isEqualTo(ID_ESTADIO_2);
        assertThat(result.clubPrincipal()).isEqualTo("FC Barcelona");
        assertThat(result.idClubPrincipal()).isEqualTo(ID_CLUB);

        assertThat(estadioSinClub.getClubPrincipal()).isNotNull();
        assertThat(estadioSinClub.getClubPrincipal().getIdEquipo()).isEqualTo(ID_CLUB);
        assertThat(club.getEstadio()).isNotNull();
        assertThat(club.getEstadio().getIdEstadio()).isEqualTo(ID_ESTADIO_2);

        verify(estadioRepository).findById(ID_ESTADIO_2);
        verify(clubRepository).findById(ID_CLUB);
        verify(clubRepository).save(club);
        verify(estadioRepository).save(estadioSinClub);
    }

    @Test
    @DisplayName("obtenerEstadioPorId: debe retornar descripción completa formateada")
    void testObtenerEstadioPorId_DescripcionCompleta() {
        when(estadioRepository.findById(ID_ESTADIO)).thenReturn(Optional.of(estadio));
        when(estadioMapper.toResponse(estadio)).thenReturn(response);

        EstadioResponse result = estadioService.obtenerEstadioPorId(ID_ESTADIO);

        assertThat(result.descripcionCompleta()).isEqualTo(
                "Camp Nou - Capacidad: 99354 - Fundado: 1957"
        );
    }

    @Test
    @DisplayName("obtenerEstadioPorId: debe retornar descripción con N/A cuando no tiene fecha")
    void testObtenerEstadioPorId_SinFecha() {
        Estadio estadioSinFecha = Estadio.builder()
                .idEstadio(UUID.randomUUID())
                .nombre("Estadio Sin Fecha")
                .direccion("Dirección")
                .capacidad(10000)
                .build();

        EstadioResponse responseSinFecha = EstadioResponse.builder()
                .idEstadio(estadioSinFecha.getIdEstadio())
                .nombre("Estadio Sin Fecha")
                .direccion("Dirección")
                .capacidad(10000)
                .descripcionCompleta("Estadio Sin Fecha - Capacidad: 10000 - Fundado: N/A")
                .build();

        when(estadioRepository.findById(estadioSinFecha.getIdEstadio())).thenReturn(Optional.of(estadioSinFecha));
        when(estadioMapper.toResponse(estadioSinFecha)).thenReturn(responseSinFecha);

        EstadioResponse result = estadioService.obtenerEstadioPorId(estadioSinFecha.getIdEstadio());

        assertThat(result.descripcionCompleta()).isEqualTo(
                "Estadio Sin Fecha - Capacidad: 10000 - Fundado: N/A"
        );
    }
}