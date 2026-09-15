package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.application.port.dto.response.JugadorPosicionNotificacionDTO;
import com.futbol.estadisticas.domain.model.enums.Alineacion;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.domain.model.enums.TipoContrato;
import com.futbol.estadisticas.domain.model.enums.TipoEquipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Equipo")
class EquipoTest {

    private Equipo equipo;

    @Mock
    private Tecnico tecnico1;

    @Mock
    private Tecnico tecnico2;

    @BeforeEach
    void setUp() {
        equipo = Equipo.builder()
                .idEquipo(UUID.randomUUID())
                .nombre("Boca Juniors")
                .tipo(TipoEquipo.CLUB_PROFESIONAL)
                .pais(Nacion.ARGENTINA)
                .build();
    }

    private Jugador jugadorCon(EstadoJugador estado) {
        Jugador jugador = new Jugador(UUID.randomUUID(), "Nombre", "Apellido",
                LocalDate.now().minusYears(24), Nacion.ARGENTINA, JuegoPies.DERECHO, 180, 75);
        DatosDeportivos datos = DatosDeportivos.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .estadoJugador(estado)
                .build();
        jugador.setDatosDeportivos(datos);
        return jugador;
    }

    private Contrato contratoVigente(PersonalDeportivo personal, TipoContrato tipo) {
        Contrato contrato = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .tipoContrato(tipo)
                .equipo(equipo)
                .personal(personal)
                .fechaInicio(LocalDateTime.now().minusDays(1))
                .fechaFin(LocalDateTime.now().plusDays(1))
                .build();
        equipo.getContratos().add(contrato);
        return contrato;
    }

    private Contrato contratoVencido(PersonalDeportivo personal, TipoContrato tipo) {
        Contrato contrato = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .tipoContrato(tipo)
                .equipo(equipo)
                .personal(personal)
                .fechaInicio(LocalDateTime.now().minusDays(30))
                .fechaFin(LocalDateTime.now().minusDays(1))
                .build();
        equipo.getContratos().add(contrato);
        return contrato;
    }

    @Nested
    @DisplayName("getJugadoresActivos")
    class GetJugadoresActivos {

        @Test
        @DisplayName("solo incluye jugadores con contrato vigente")
        void soloIncluyeContratoVigente() {
            Jugador activo = jugadorCon(EstadoJugador.TITULAR);
            Jugador inactivo = jugadorCon(EstadoJugador.LIBRE);
            contratoVigente(activo, TipoContrato.PROFESIONAL);
            contratoVencido(inactivo, TipoContrato.PROFESIONAL);

            assertThat(equipo.getJugadoresActivos()).containsExactly(activo);
        }

        @Test
        @DisplayName("excluye personal que no sea Jugador (por ejemplo, un Tecnico)")
        void excluyeTecnicos() {
            Tecnico tecnico = new Tecnico(UUID.randomUUID(), "Diego", "Simeone",
                    LocalDate.now().minusYears(50), Nacion.ARGENTINA, "Defensivo", "4-4-2");
            contratoVigente(tecnico, TipoContrato.PROFESIONAL);

            assertThat(equipo.getJugadoresActivos()).isEmpty();
        }
    }

    @Test
    @DisplayName("getJugadoresTitulares filtra por estado TITULAR")
    void getJugadoresTitulares() {
        Jugador titular = jugadorCon(EstadoJugador.TITULAR);
        Jugador suplente = jugadorCon(EstadoJugador.SUPLENTE);
        contratoVigente(titular, TipoContrato.PROFESIONAL);
        contratoVigente(suplente, TipoContrato.PROFESIONAL);

        assertThat(equipo.getJugadoresTitulares()).containsExactly(titular);
    }

    @Test
    @DisplayName("getJugadoresLesionados filtra jugadores con lesión activa")
    void getJugadoresLesionados() {
        Jugador lesionado = jugadorCon(EstadoJugador.LESIONADO);
        lesionado.getLesiones().add(Lesion.builder()
                .idLesion(UUID.randomUUID())
                .fechaInicio(LocalDate.now().minusDays(2))
                .fechaFin(LocalDate.now().plusDays(5))
                .build());
        Jugador sano = jugadorCon(EstadoJugador.TITULAR);
        contratoVigente(lesionado, TipoContrato.PROFESIONAL);
        contratoVigente(sano, TipoContrato.PROFESIONAL);

        assertThat(equipo.getJugadoresLesionados()).containsExactly(lesionado);
    }

    @Test
    @DisplayName("getJugadoresDisponibles excluye lesionados, suspendidos, retirados y apartados")
    void getJugadoresDisponibles() {
        Jugador disponible = jugadorCon(EstadoJugador.TITULAR);
        Jugador suspendido = jugadorCon(EstadoJugador.SUSPENDIDO);
        contratoVigente(disponible, TipoContrato.PROFESIONAL);
        contratoVigente(suspendido, TipoContrato.PROFESIONAL);

        assertThat(equipo.getJugadoresDisponibles()).containsExactly(disponible);
    }

    @Nested
    @DisplayName("getValorPlantillaTotal")
    class GetValorPlantillaTotal {

        @Test
        @DisplayName("suma el valor de mercado de los jugadores activos")
        void sumaValoresDeMercado() {
            Jugador j1 = jugadorCon(EstadoJugador.TITULAR);
            j1.getDatosDeportivos().setValorMercado(10_000_000.0);
            Jugador j2 = jugadorCon(EstadoJugador.SUPLENTE);
            j2.getDatosDeportivos().setValorMercado(5_000_000.0);
            contratoVigente(j1, TipoContrato.PROFESIONAL);
            contratoVigente(j2, TipoContrato.PROFESIONAL);

            assertThat(equipo.getValorPlantillaTotal()).isEqualTo(15_000_000.0);
        }

        @Test
        @DisplayName("trata como 0 a los jugadores sin valor de mercado")
        void tratanComoCeroSinValor() {
            Jugador sinValor = jugadorCon(EstadoJugador.TITULAR);
            contratoVigente(sinValor, TipoContrato.PROFESIONAL);

            assertThat(equipo.getValorPlantillaTotal()).isZero();
        }

        @Test
        @DisplayName("devuelve 0 si no hay jugadores activos")
        void devuelveCeroSinJugadores() {
            assertThat(equipo.getValorPlantillaTotal()).isZero();
        }
    }

    @Nested
    @DisplayName("agregarContrato")
    class AgregarContrato {

        @Test
        @DisplayName("lanza excepción si el contrato es nulo")
        void lanzaExcepcionSiEsNulo() {
            assertThatThrownBy(() -> equipo.agregarContrato(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("agrega el contrato y establece el equipo en el contrato")
        void agregaContratoYEstableceEquipo() {
            Contrato contrato = Contrato.builder()
                    .idContrato(UUID.randomUUID())
                    .tipoContrato(TipoContrato.PROFESIONAL)
                    .build();

            equipo.agregarContrato(contrato);

            assertThat(equipo.getContratos()).contains(contrato);
            assertThat(contrato.getEquipo()).isEqualTo(equipo);
        }

        @Test
        @DisplayName("propaga la excepción si el tipo de contrato no es compatible")
        void propagaExcepcionSiIncompatible() {
            Contrato contratoAmateur = Contrato.builder()
                    .idContrato(UUID.randomUUID())
                    .tipoContrato(TipoContrato.AMATEUR)
                    .build();

            assertThatThrownBy(() -> equipo.agregarContrato(contratoAmateur))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("puedeTenerContratoDeTipo")
    class PuedeTenerContratoDeTipo {

        @Test
        @DisplayName("devuelve false si el tipo de contrato o el tipo de equipo son nulos")
        void falseSiHayNulos() {
            assertThat(equipo.puedeTenerContratoDeTipo(null)).isFalse();

            Equipo sinTipo = Equipo.builder().idEquipo(UUID.randomUUID()).build();
            assertThat(sinTipo.puedeTenerContratoDeTipo(TipoContrato.PROFESIONAL)).isFalse();
        }

        @Test
        @DisplayName("CLUB_PROFESIONAL acepta PROFESIONAL y CESION")
        void clubProfesionalAceptaProfesionalYCesion() {
            assertThat(equipo.puedeTenerContratoDeTipo(TipoContrato.PROFESIONAL)).isTrue();
            assertThat(equipo.puedeTenerContratoDeTipo(TipoContrato.CESION)).isTrue();
            assertThat(equipo.puedeTenerContratoDeTipo(TipoContrato.AMATEUR)).isFalse();
        }

        @Test
        @DisplayName("SELECCION_ABSOLUTA solo acepta CONVOCATORIA")
        void seleccionSoloAceptaConvocatoria() {
            Equipo seleccion = Equipo.builder()
                    .idEquipo(UUID.randomUUID())
                    .tipo(TipoEquipo.SELECCION_ABSOLUTA)
                    .build();

            assertThat(seleccion.puedeTenerContratoDeTipo(TipoContrato.CONVOCATORIA)).isTrue();
            assertThat(seleccion.puedeTenerContratoDeTipo(TipoContrato.PROFESIONAL)).isFalse();
        }
    }

    @Test
    @DisplayName("getContratosPorTipo filtra por el tipo indicado")
    void getContratosPorTipo() {
        Jugador jugador = jugadorCon(EstadoJugador.TITULAR);
        Contrato profesional = contratoVigente(jugador, TipoContrato.PROFESIONAL);

        assertThat(equipo.getContratosPorTipo(TipoContrato.PROFESIONAL)).containsExactly(profesional);
        assertThat(equipo.getContratosPorTipo(TipoContrato.AMATEUR)).isEmpty();
    }

    @Test
    @DisplayName("getJugadoresProfesionales filtra jugadores vigentes con contrato PROFESIONAL")
    void getJugadoresProfesionales() {
        Jugador profesional = jugadorCon(EstadoJugador.TITULAR);
        Jugador juvenil = jugadorCon(EstadoJugador.SUPLENTE);
        contratoVigente(profesional, TipoContrato.PROFESIONAL);
        contratoVigente(juvenil, TipoContrato.JUVENIL);

        assertThat(equipo.getJugadoresProfesionales()).containsExactly(profesional);
    }

    @Test
    @DisplayName("getJugadoresJuveniles filtra jugadores vigentes con contrato JUVENIL")
    void getJugadoresJuveniles() {
        Jugador profesional = jugadorCon(EstadoJugador.TITULAR);
        Jugador juvenil = jugadorCon(EstadoJugador.SUPLENTE);
        contratoVigente(profesional, TipoContrato.PROFESIONAL);
        contratoVigente(juvenil, TipoContrato.JUVENIL);

        assertThat(equipo.getJugadoresJuveniles()).containsExactly(juvenil);
    }

    @Nested
    @DisplayName("gestión del técnico")
    class GestionDelTecnico {

        @Test
        @DisplayName("asignarTecnico lanza excepción si el técnico es nulo")
        void asignarTecnicoLanzaExcepcionSiEsNulo() {
            assertThatThrownBy(() -> equipo.asignarTecnico(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("asignarTecnico vincula al nuevo técnico con el club")
        void asignarTecnicoVinculaAlClub() {
            equipo.asignarTecnico(tecnico1);

            assertThat(equipo.getTecnicoActual()).isEqualTo(tecnico1);
            assertThat(equipo.getTecnicos()).contains(tecnico1);
            verify(tecnico1).asignarClub(equipo);
        }

        @Test
        @DisplayName("asignarTecnico desvincula al técnico anterior si es distinto")
        void asignarTecnicoDesvinculaAlAnterior() {
            equipo.asignarTecnico(tecnico1);

            equipo.asignarTecnico(tecnico2);

            verify(tecnico1).desvincularClub();
            verify(tecnico2).asignarClub(equipo);
            assertThat(equipo.getTecnicoActual()).isEqualTo(tecnico2);
        }

        @Test
        @DisplayName("asignarTecnico no desvincula si se reasigna el mismo técnico")
        void asignarTecnicoNoDesvinculaSiEsElMismo() {
            equipo.asignarTecnico(tecnico1);

            equipo.asignarTecnico(tecnico1);

            verify(tecnico1, never()).desvincularClub();
            verify(tecnico1, times(2)).asignarClub(equipo);
        }

        @Test
        @DisplayName("asignarTecnico no duplica al técnico en la lista histórica")
        void asignarTecnicoNoDuplicaEnLista() {
            equipo.asignarTecnico(tecnico1);
            equipo.asignarTecnico(tecnico2);
            equipo.asignarTecnico(tecnico1);

            assertThat(equipo.getTecnicos()).containsExactly(tecnico1, tecnico2);
        }

        @Test
        @DisplayName("desvincularTecnico lanza excepción si no hay técnico asignado")
        void desvincularTecnicoLanzaExcepcionSiNoHayNinguno() {
            assertThatThrownBy(() -> equipo.desvincularTecnico())
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("desvincularTecnico limpia el técnico actual y notifica al técnico")
        void desvincularTecnicoLimpiaYNotifica() {
            equipo.asignarTecnico(tecnico1);

            equipo.desvincularTecnico();

            verify(tecnico1, times(1)).desvincularClub();
            assertThat(equipo.getTecnicoActual()).isNull();
        }
    }

    @Nested
    @DisplayName("asignarPosiciones")
    class AsignarPosiciones {

        private Jugador jugadorEnPosicion(PosicionJugador posicion, Map<String, UUID> mapaCampos) {
            Jugador jugador = jugadorCon(EstadoJugador.TITULAR);
            contratoVigente(jugador, TipoContrato.PROFESIONAL);
            mapaCampos.put(posicion.getNombreCampo(), jugador.getIdPersonal());
            return jugador;
        }

        @Test
        @DisplayName("lanza excepción si falta el jugador para alguna posición requerida")
        void lanzaExcepcionSiFaltaJugadorParaPosicion() {
            Map<String, UUID> mapaCampos = new HashMap<>();

            assertThatThrownBy(() -> equipo.asignarPosiciones(Alineacion.ALINEACION_433, mapaCampos))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Falta el jugador");
        }

        @Test
        @DisplayName("lanza excepción si el jugador indicado no pertenece al equipo (no está activo)")
        void lanzaExcepcionSiJugadorNoPerteneceAlEquipo() {
            Map<String, UUID> mapaCampos = new HashMap<>();
            for (PosicionJugador posicion : Alineacion.ALINEACION_433.getPosicionesAplanadas()) {
                mapaCampos.put(posicion.getNombreCampo(), UUID.randomUUID());
            }

            assertThatThrownBy(() -> equipo.asignarPosiciones(Alineacion.ALINEACION_433, mapaCampos))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no pertenece al equipo");
        }

        @Test
        @DisplayName("lanza excepción si el jugador activo no tiene DatosDeportivos")
        void lanzaExcepcionSiJugadorSinDatosDeportivos() {
            Jugador sinDatos = new Jugador(UUID.randomUUID(), "Sin", "Datos",
                    LocalDate.now().minusYears(22), Nacion.ARGENTINA, JuegoPies.DERECHO, 178, 74);
            // Se agrega manualmente el contrato sin pasar por jugadorCon(),
            // ya que este jugador no debe tener DatosDeportivos.
            Contrato contrato = Contrato.builder()
                    .idContrato(UUID.randomUUID())
                    .tipoContrato(TipoContrato.PROFESIONAL)
                    .equipo(equipo)
                    .personal(sinDatos)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            equipo.getContratos().add(contrato);

            Map<String, UUID> mapaCampos = new HashMap<>();
            for (PosicionJugador posicion : Alineacion.ALINEACION_433.getPosicionesAplanadas()) {
                mapaCampos.put(posicion.getNombreCampo(), sinDatos.getIdPersonal());
            }

            assertThatThrownBy(() -> equipo.asignarPosiciones(Alineacion.ALINEACION_433, mapaCampos))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no tiene DatosDeportivos");
        }

        @Test
        @DisplayName("asigna cada jugador a su posición e informa si hubo cambio de posición")
        void asignaJugadoresYDetectaCambioDePosicion() {
            Map<String, UUID> mapaCampos = new HashMap<>();
            List<PosicionJugador> posiciones = Alineacion.ALINEACION_433.getPosicionesAplanadas();

            // El primer puesto de la 4-3-3 es siempre PORTERO; simulamos que ese
            // jugador venía jugando de delantero, para verificar la bandera
            // "posicionCambiada" al reasignarlo a su nueva posición.
            Jugador conCambioDePosicion = jugadorEnPosicion(posiciones.get(0), mapaCampos);
            conCambioDePosicion.getDatosDeportivos().agregarPosicion(PosicionJugador.DELANTERO_CENTRO);

            for (int i = 1; i < posiciones.size(); i++) {
                jugadorEnPosicion(posiciones.get(i), mapaCampos);
            }

            List<JugadorPosicionNotificacionDTO> resultado =
                    equipo.asignarPosiciones(Alineacion.ALINEACION_433, mapaCampos);

            assertThat(resultado).hasSize(posiciones.size());
            assertThat(resultado.get(0).posicionCambiada()).isTrue();
            assertThat(resultado.get(0).posicionNueva()).isEqualTo(posiciones.get(0));
            assertThat(conCambioDePosicion.getDatosDeportivos().getPosicionActual())
                    .isEqualTo(posiciones.get(0));
        }

        @Test
        @DisplayName("no marca cambio de posición si el jugador ya estaba en esa posición")
        void noMarcaCambioSiYaEstabaEnLaPosicion() {
            Map<String, UUID> mapaCampos = new HashMap<>();
            List<PosicionJugador> posiciones = Alineacion.ALINEACION_433.getPosicionesAplanadas();

            for (PosicionJugador posicion : posiciones) {
                Jugador jugador = jugadorEnPosicion(posicion, mapaCampos);
                jugador.getDatosDeportivos().agregarPosicion(posicion);
            }

            List<JugadorPosicionNotificacionDTO> resultado =
                    equipo.asignarPosiciones(Alineacion.ALINEACION_433, mapaCampos);

            assertThat(resultado).allSatisfy(dto -> assertThat(dto.posicionCambiada()).isFalse());
        }
    }
}
