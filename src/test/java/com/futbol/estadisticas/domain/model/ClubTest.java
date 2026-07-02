package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClubTest {

    private Club club;
    private Estadio estadio;
    private static final UUID ID_CLUB = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        estadio = Estadio.builder()
                .idEstadio(UUID.randomUUID())
                .nombre("Emirates Stadium")
                .capacidad(60704)
                .build();

        club = Club.builder()
                .idEquipo(ID_CLUB)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .fechaFundacion(LocalDate.of(1886, 12, 1))
                .estadio(estadio)
                .build();
    }

    @Test
    @DisplayName("agregarContrato: debe agregar un contrato y establecer relación bidireccional")
    void testAgregarContrato() {
        Contrato contrato = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .estado(EstadoContrato.ACTIVO)
                .build();

        club.agregarContrato(contrato);

        assertThat(club.getContratos()).hasSize(1);
        assertThat(contrato.getClub()).isEqualTo(club);
    }

    @Test
    @DisplayName("agregarContrato: debe lanzar excepción cuando el contrato es nulo")
    void testAgregarContrato_Nulo() {
        assertThatThrownBy(() -> club.agregarContrato(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El contrato no puede ser nulo");
    }

    @Test
    @DisplayName("getJugadoresActivos: debe retornar solo jugadores con contrato vigente")
    void testGetJugadoresActivos() {
        Jugador jugador1 = crearJugador("Bukayo", "Saka");
        Jugador jugador2 = crearJugador("Erling", "Haaland");

        Contrato contrato1 = crearContrato(jugador1, LocalDateTime.now().minusMonths(6), LocalDateTime.now().plusMonths(6));
        Contrato contrato2 = crearContrato(jugador2, LocalDateTime.now().minusMonths(12), LocalDateTime.now().minusMonths(1));

        club.agregarContrato(contrato1);
        club.agregarContrato(contrato2);

        assertThat(club.getJugadoresActivos()).hasSize(1);
        assertThat(club.getJugadoresActivos().get(0).getNombre()).isEqualTo("Bukayo");
    }

    @Test
    @DisplayName("getJugadoresTitulares: debe retornar solo jugadores titulares")
    void testGetJugadoresTitulares() {
        Jugador jugador1 = crearJugador("Bukayo", "Saka");
        Jugador jugador2 = crearJugador("Cole", "Palmer");

        DatosDeportivos datos1 = DatosDeportivos.builder()
                .estadoJugador(EstadoJugador.TITULAR)
                .build();
        jugador1.setDatosDeportivos(datos1);

        DatosDeportivos datos2 = DatosDeportivos.builder()
                .estadoJugador(EstadoJugador.SUPLENTE)
                .build();
        jugador2.setDatosDeportivos(datos2);

        Contrato contrato1 = crearContrato(jugador1, LocalDateTime.now().minusMonths(6), LocalDateTime.now().plusMonths(6));
        Contrato contrato2 = crearContrato(jugador2, LocalDateTime.now().minusMonths(6), LocalDateTime.now().plusMonths(6));

        club.agregarContrato(contrato1);
        club.agregarContrato(contrato2);

        assertThat(club.getJugadoresTitulares()).hasSize(1);
        assertThat(club.getJugadoresTitulares().get(0).getNombre()).isEqualTo("Bukayo");
    }

    @Test
    @DisplayName("getValorPlantillaTotal: debe calcular el valor total de la plantilla")
    void testGetValorPlantillaTotal() {
        Jugador jugador1 = crearJugador("Bukayo", "Saka");
        Jugador jugador2 = crearJugador("Erling", "Haaland");

        DatosDeportivos datos1 = DatosDeportivos.builder()
                .valorMercado(85_000_000.0)
                .estadoJugador(EstadoJugador.TITULAR)
                .build();
        jugador1.setDatosDeportivos(datos1);

        DatosDeportivos datos2 = DatosDeportivos.builder()
                .valorMercado(200_000_000.0)
                .estadoJugador(EstadoJugador.TITULAR)
                .build();
        jugador2.setDatosDeportivos(datos2);

        Contrato contrato1 = crearContrato(jugador1, LocalDateTime.now().minusMonths(6), LocalDateTime.now().plusMonths(6));
        Contrato contrato2 = crearContrato(jugador2, LocalDateTime.now().minusMonths(6), LocalDateTime.now().plusMonths(6));

        club.agregarContrato(contrato1);
        club.agregarContrato(contrato2);

        assertThat(club.getValorPlantillaTotal()).isEqualTo(285_000_000.0);
    }

    @Test
    @DisplayName("asignarTecnico: debe asignar un técnico y establecer relación bidireccional")
    void testAsignarTecnico() {
        Tecnico tecnico = Tecnico.builder()
                .idPersonal(UUID.randomUUID())
                .nombre("Mikel")
                .apellido("Arteta")
                .build();

        club.asignarTecnico(tecnico);

        assertThat(club.getTecnicoActual()).isEqualTo(tecnico);
        assertThat(club.getTecnicos()).hasSize(1);
        assertThat(tecnico.getClubActualAsignado()).isEqualTo(club);
    }

    @Test
    @DisplayName("asignarTecnico: debe lanzar excepción cuando el técnico es nulo")
    void testAsignarTecnico_Nulo() {
        assertThatThrownBy(() -> club.asignarTecnico(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El técnico no puede ser nulo");
    }

    @Test
    @DisplayName("desvincularTecnico: debe desvincular al técnico actual")
    void testDesvincularTecnico() {
        Tecnico tecnico = Tecnico.builder()
                .idPersonal(UUID.randomUUID())
                .nombre("Mikel")
                .apellido("Arteta")
                .build();

        club.asignarTecnico(tecnico);
        club.desvincularTecnico();

        assertThat(club.getTecnicoActual()).isNull();
        assertThat(tecnico.getClubActualAsignado()).isNull();
    }

    @Test
    @DisplayName("desvincularTecnico: debe lanzar excepción cuando no hay técnico")
    void testDesvincularTecnico_SinTecnico() {
        assertThatThrownBy(() -> club.desvincularTecnico())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El club no tiene técnico asignado actualmente");
    }

    // Helpers
    private Jugador crearJugador(String nombre, String apellido) {
        return Jugador.builder()
                .idPersonal(UUID.randomUUID())
                .nombre(nombre)
                .apellido(apellido)
                .nacionalidad(Nacion.INGLATERRA)
                .build();
    }

    private Contrato crearContrato(PersonalDeportivo personal, LocalDateTime inicio, LocalDateTime fin) {
        return Contrato.builder()
                .idContrato(UUID.randomUUID())
                .personal(personal)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .estado(EstadoContrato.ACTIVO)
                .build();
    }

    @Test
    void testAgregarContrato2() {
        
    }

    @Test
    void testAgregarPartidoLocal() {
        
    }

    @Test
    void testAgregarPartidoVisitante() {
        
    }

    @Test
    void testAsignarTecnico2() {
        
    }

    @Test
    void testBuilder() {
        
    }

    @Test
    void testCanEqual() {
        
    }

    @Test
    void testDesvincularTecnico2() {
        
    }

    @Test
    void testEquals() {
        
    }

    @Test
    void testGetContratos() {
        
    }

    @Test
    void testGetEstadio() {
        
    }

    @Test
    void testGetFechaFundacion() {
        
    }

    @Test
    void testGetIdEquipo() {
        
    }

    @Test
    void testGetJugadoresActivos2() {
        
    }

    @Test
    void testGetJugadoresDisponibles() {
        
    }

    @Test
    void testGetJugadoresLesionados() {
        
    }

    @Test
    void testGetJugadoresTitulares2() {
        
    }

    @Test
    void testGetNombre() {
        
    }

    @Test
    void testGetNombreCorto() {
        
    }

    @Test
    void testGetPartidosLocal() {
        
    }

    @Test
    void testGetPartidosVisitante() {
        
    }

    @Test
    void testGetTecnicoActual() {
        
    }

    @Test
    void testGetTecnicos() {
        
    }

    @Test
    void testGetTodosLosPartidos() {
        
    }

    @Test
    void testGetValorPlantillaTotal2() {
        
    }

    @Test
    void testHashCode() {
        
    }

    @Test
    void testSetContratos() {
        
    }

    @Test
    void testSetEstadio() {
        
    }

    @Test
    void testSetFechaFundacion() {
        
    }

    @Test
    void testSetIdEquipo() {
        
    }

    @Test
    void testSetNombre() {
        
    }

    @Test
    void testSetNombreCorto() {
        
    }

    @Test
    void testSetPartidosLocal() {
        
    }

    @Test
    void testSetPartidosVisitante() {
        
    }

    @Test
    void testSetTecnicoActual() {
        
    }

    @Test
    void testSetTecnicos() {
        
    }
}