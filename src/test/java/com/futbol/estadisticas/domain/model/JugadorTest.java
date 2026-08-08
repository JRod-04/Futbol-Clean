package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JugadorTest {

    private Jugador jugador;
    private static final UUID ID_JUGADOR = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        jugador = Jugador.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(178)
                .peso(70)
                .build();

        DatosDeportivos datos = DatosDeportivos.builder()
                .estadoJugador(EstadoJugador.TITULAR)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.EXTREMO_DERECHO)))  
                .dorsal(7)  
                .valorMercado(85_000_000.0)
                .build();

        jugador.setDatosDeportivos(datos);

        Equipo club = Equipo.builder()
                .idEquipo(UUID.randomUUID())
                .nombre("Arsenal FC")
                .build();

        Contrato contrato = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .equipo(club)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .estado(EstadoContrato.ACTIVO)
                .build();

        jugador.agregarContrato(contrato);
    }

    @Test
    @DisplayName("getClubActual: debe retornar el club del contrato vigente")
    void testGetEquipoActual() {
        assertThat(jugador.getEquipoActual()).isNotNull();
        assertThat(jugador.getEquipoActual().getNombre()).isEqualTo("Arsenal FC");
    }

    @Test
    @DisplayName("getClubActual: debe retornar null cuando no hay contrato vigente")
    void testGetEquipoActual_SinContrato() {
        jugador.getContratos().clear();
        assertThat(jugador.getEquipoActual()).isNull();
    }

    @Test
    @DisplayName("registrarLesion: debe registrar una lesión y cambiar estado a LESIONADO")
    void testRegistrarLesion() {
        Lesion lesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Rotura de ligamento")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now())
                .curada(false)
                .build();

        jugador.registrarLesion(lesion);

        assertThat(jugador.getLesiones()).hasSize(1);
        assertThat(jugador.getDatosDeportivos().getEstadoJugador())
                .isEqualTo(EstadoJugador.LESIONADO);
    }

    @Test
    @DisplayName("registrarLesion: debe lanzar excepción cuando la lesión es nula")
    void testRegistrarLesion_Nula() {
        assertThatThrownBy(() -> jugador.registrarLesion(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La lesión no puede ser nula");
    }

    @Test
    @DisplayName("registrarLesion: debe lanzar excepción cuando la lesión ya está curada")
    void testRegistrarLesion_YaCurada() {
        Lesion lesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión curada")
                .gravedad(Gravedad.LEVE)
                .fechaInicio(LocalDate.now().minusMonths(1))
                .curada(true)
                .build();

        assertThatThrownBy(() -> jugador.registrarLesion(lesion))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No se puede registrar una lesión que ya está curada");
    }

    @Test
    @DisplayName("estaLesionado: debe retornar true cuando tiene lesiones activas")
    void testEstaLesionado() {
        Lesion lesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión activa")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(1))
                .curada(false)
                .build();

        jugador.registrarLesion(lesion);
        assertThat(jugador.estaLesionado()).isTrue();
    }

    @Test
    @DisplayName("estaDisponible: debe retornar true cuando no tiene lesiones activas")
    void testEstaDisponible() {
        assertThat(jugador.estaDisponible()).isTrue();

        Lesion lesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión activa")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(1))
                .curada(false)
                .build();

        jugador.registrarLesion(lesion);
        assertThat(jugador.estaDisponible()).isFalse();
    }

    @Test
    @DisplayName("estaDisponible: debe retornar false cuando está suspendido")
    void testEstaDisponible_Suspendido() {
        jugador.getDatosDeportivos().setEstadoJugador(EstadoJugador.SUSPENDIDO);
        assertThat(jugador.estaDisponible()).isFalse();
    }

    @Test
    @DisplayName("estaDisponible: debe retornar false cuando está retirado")
    void testEstaDisponible_Retirado() {
        jugador.getDatosDeportivos().setEstadoJugador(EstadoJugador.RETIRADO);
        assertThat(jugador.estaDisponible()).isFalse();
    }


    @Test
    @DisplayName("getDatosDeportivos: debe tener dorsal asignado")
    void testGetDorsal() {
        assertThat(jugador.getDatosDeportivos()).isNotNull();
        assertThat(jugador.getDatosDeportivos().getDorsal()).isEqualTo(7);
    }

    @Test
    @DisplayName("getDatosDeportivos: debe tener lista de posiciones")
    void testGetPosiciones() {
        assertThat(jugador.getDatosDeportivos()).isNotNull();
        assertThat(jugador.getDatosDeportivos().getPosiciones())
                .containsExactly(PosicionJugador.EXTREMO_DERECHO);
        assertThat(jugador.getDatosDeportivos().getPosicionActual())
                .isEqualTo(PosicionJugador.EXTREMO_DERECHO);
    }

    @Test
    @DisplayName("actualizarDorsal: debe actualizar el dorsal correctamente")
    void testActualizarDorsal() {
        DatosDeportivos datos = jugador.getDatosDeportivos();
        assertThat(datos.getDorsal()).isEqualTo(7);

        datos.actualizarDorsal(10);
        assertThat(datos.getDorsal()).isEqualTo(10);
        assertThat(datos.getFechaActualizacion()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("actualizarDorsal: debe lanzar excepción cuando el dorsal es null")
    void testActualizarDorsal_Nulo() {
        DatosDeportivos datos = jugador.getDatosDeportivos();

        assertThatThrownBy(() -> datos.actualizarDorsal(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El dorsal no puede ser nulo");
    }

    @Test
    @DisplayName("actualizarDorsal: debe lanzar excepción cuando el dorsal es negativo")
    void testActualizarDorsal_Negativo() {
        DatosDeportivos datos = jugador.getDatosDeportivos();

        assertThatThrownBy(() -> datos.actualizarDorsal(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El dorsal debe ser positivo");
    }

    @Test
    @DisplayName("actualizarDorsal: debe lanzar excepción cuando el dorsal es cero")
    void testActualizarDorsal_Cero() {
        DatosDeportivos datos = jugador.getDatosDeportivos();

        assertThatThrownBy(() -> datos.actualizarDorsal(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El dorsal debe ser positivo");
    }

    @Test
    @DisplayName("agregarPosicion: debe agregar una nueva posición a la lista")
    void testAgregarPosicion() {
        DatosDeportivos datos = jugador.getDatosDeportivos();
        assertThat(datos.getPosiciones()).hasSize(1);
        assertThat(datos.getPosicionActual()).isEqualTo(PosicionJugador.EXTREMO_DERECHO);

        datos.agregarPosicion(PosicionJugador.DELANTERO);

        assertThat(datos.getPosiciones()).hasSize(2);
        assertThat(datos.getPosiciones())
                .containsExactly(PosicionJugador.EXTREMO_DERECHO, PosicionJugador.DELANTERO);
        assertThat(datos.getPosicionActual()).isEqualTo(PosicionJugador.DELANTERO);
        assertThat(datos.getFechaActualizacion()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("agregarPosicion: no debe agregar posiciones duplicadas")
    void testAgregarPosicion_Duplicada() {
        DatosDeportivos datos = jugador.getDatosDeportivos();
        assertThat(datos.getPosiciones()).hasSize(1);

        datos.agregarPosicion(PosicionJugador.EXTREMO_DERECHO); 

        assertThat(datos.getPosiciones()).hasSize(1);
        assertThat(datos.getPosiciones())
                .containsExactly(PosicionJugador.EXTREMO_DERECHO);
    }

    @Test
    @DisplayName("agregarPosicion: debe lanzar excepción cuando la posición es nula")
    void testAgregarPosicion_Nula() {
        DatosDeportivos datos = jugador.getDatosDeportivos();

        assertThatThrownBy(() -> datos.agregarPosicion(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La posición no puede ser nula");
    }

    @Test
    @DisplayName("getPosicionActual: debe retornar la última posición de la lista")
    void testGetPosicionActual() {
        DatosDeportivos datos = jugador.getDatosDeportivos();
        
        assertThat(datos.getPosicionActual()).isEqualTo(PosicionJugador.EXTREMO_DERECHO);

        datos.agregarPosicion(PosicionJugador.DELANTERO);
        assertThat(datos.getPosicionActual()).isEqualTo(PosicionJugador.DELANTERO);

        datos.agregarPosicion(PosicionJugador.MEDIOCENTRO);
        assertThat(datos.getPosicionActual()).isEqualTo(PosicionJugador.MEDIOCENTRO);
    }

    @Test
    @DisplayName("getPosicionActual: debe retornar null cuando la lista está vacía")
    void testGetPosicionActual_ListaVacia() {
        DatosDeportivos datos = DatosDeportivos.builder()
                .posiciones(new ArrayList<>())
                .build();

        assertThat(datos.getPosicionActual()).isNull();
    }
}