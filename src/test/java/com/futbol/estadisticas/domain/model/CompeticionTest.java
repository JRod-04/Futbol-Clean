package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoCompeticion;
import com.futbol.estadisticas.domain.model.enums.Temporada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompeticionTest {

    private static final UUID ID_COMPETICION =
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private static final LocalDateTime INICIO = LocalDateTime.of(2024, 8, 16, 0, 0);
    private static final LocalDateTime FIN = LocalDateTime.of(2025, 5, 25, 23, 59);

    private Competicion competicionBase(EstadoCompeticion estado) {
        return Competicion.builder()
                .idCompeticion(ID_COMPETICION)
                .nombre("Premier League")
                .temporada(Temporada.T2024_25)
                .fechaInicio(INICIO)
                .fechaFin(FIN)
                .estado(estado)
                .build();
    }


    @Nested
    @DisplayName("Construcción")
    class Construccion {

        @Test
        @DisplayName("builder crea competición con todos los campos")
        void builderCompleto() {
            Competicion c = competicionBase(EstadoCompeticion.POR_INICIAR);

            assertThat(c.getIdCompeticion()).isEqualTo(ID_COMPETICION);
            assertThat(c.getNombre()).isEqualTo("Premier League");
            assertThat(c.getTemporada()).isEqualTo(Temporada.T2024_25);
            assertThat(c.getFechaInicio()).isEqualTo(INICIO);
            assertThat(c.getFechaFin()).isEqualTo(FIN);
            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.POR_INICIAR);
            assertThat(c.getEquipoGanador()).isNull();
        }

        @Test
        @DisplayName("partidos se inicializa vacío por defecto")
        void partidosVacioPorDefecto() {
            Competicion c = competicionBase(EstadoCompeticion.POR_INICIAR);

            assertThat(c.getPartidos()).isNotNull();
            assertThat(c.getPartidos()).isEmpty();
        }

        @Test
        @DisplayName("constructor vacío también deja partidos vacío (@Builder.Default)")
        void constructorVacio() {
            Competicion c = new Competicion();

            assertThat(c.getPartidos()).isNotNull();
            assertThat(c.getPartidos()).isEmpty();
        }
    }


    @Nested
    @DisplayName("Métodos de estado")
    class MetodosDeEstado {

        @Test
        @DisplayName("estaActiva: true solo si EN_CURSO")
        void estaActiva() {
            assertThat(competicionBase(EstadoCompeticion.EN_CURSO).estaActiva()).isTrue();
            assertThat(competicionBase(EstadoCompeticion.POR_INICIAR).estaActiva()).isFalse();
            assertThat(competicionBase(EstadoCompeticion.FINALIZADA).estaActiva()).isFalse();
            assertThat(competicionBase(EstadoCompeticion.SUSPENDIDA).estaActiva()).isFalse();
            assertThat(competicionBase(EstadoCompeticion.CANCELADA).estaActiva()).isFalse();
        }

        @Test
        @DisplayName("haFinalizado: true solo si FINALIZADA")
        void haFinalizado() {
            assertThat(competicionBase(EstadoCompeticion.FINALIZADA).haFinalizado()).isTrue();
            assertThat(competicionBase(EstadoCompeticion.EN_CURSO).haFinalizado()).isFalse();
            assertThat(competicionBase(EstadoCompeticion.POR_INICIAR).haFinalizado()).isFalse();
            assertThat(competicionBase(EstadoCompeticion.SUSPENDIDA).haFinalizado()).isFalse();
            assertThat(competicionBase(EstadoCompeticion.CANCELADA).haFinalizado()).isFalse();
        }

        @Test
        @DisplayName("noHaComenzado: true solo si POR_INICIAR")
        void noHaComenzado() {
            assertThat(competicionBase(EstadoCompeticion.POR_INICIAR).noHaComenzado()).isTrue();
            assertThat(competicionBase(EstadoCompeticion.EN_CURSO).noHaComenzado()).isFalse();
            assertThat(competicionBase(EstadoCompeticion.FINALIZADA).noHaComenzado()).isFalse();
            assertThat(competicionBase(EstadoCompeticion.SUSPENDIDA).noHaComenzado()).isFalse();
            assertThat(competicionBase(EstadoCompeticion.CANCELADA).noHaComenzado()).isFalse();
        }

        @Test
        @DisplayName("estado null: todos los métodos devuelven false")
        void estadoNull() {
            Competicion c = competicionBase(null);

            assertThat(c.estaActiva()).isFalse();
            assertThat(c.haFinalizado()).isFalse();
            assertThat(c.noHaComenzado()).isFalse();
        }
    }


    @Nested
    @DisplayName("agregarPartido")
    class AgregarPartido {

        @Test
        @DisplayName("agrega partido y establece la competición en el partido")
        void agregaPartidoYEstableceCompeticion() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);
            Partido partido = new Partido();

            c.agregarPartido(partido);

            assertThat(c.getPartidos()).hasSize(1);
            assertThat(c.getPartidos()).contains(partido);
            assertThat(partido.getCompeticion()).isSameAs(c);
        }

        @Test
        @DisplayName("ignora partido null")
        void ignoraNull() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            c.agregarPartido(null);

            assertThat(c.getPartidos()).isEmpty();
        }

        @Test
        @DisplayName("permite agregar varios partidos")
        void agregaVarios() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);
            Partido p1 = new Partido();
            Partido p2 = new Partido();

            c.agregarPartido(p1);
            c.agregarPartido(p2);

            assertThat(c.getPartidos()).containsExactly(p1, p2);
            assertThat(p1.getCompeticion()).isSameAs(c);
            assertThat(p2.getCompeticion()).isSameAs(c);
        }
    }


    @Nested
    @DisplayName("getPartidosJugados y porcentaje")
    class PartidosJugados {

        @Test
        @DisplayName("sin partidos devuelve lista vacía y porcentaje 0")
        void sinPartidos() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            assertThat(c.getPartidosJugados()).isEmpty();
            assertThat(c.getPorcentajePartidosJugados()).isZero();
        }

        @Test
        @DisplayName("solo cuenta partidos finalizados")
        void soloFinalizados() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            Partido finalizado = partidoFinalizado();
            Partido enCurso = partidoConEstado(false);
            Partido programado = partidoConEstado(false);

            c.agregarPartido(finalizado);
            c.agregarPartido(enCurso);
            c.agregarPartido(programado);

            assertThat(c.getPartidosJugados()).containsExactly(finalizado);
        }

        @Test
        @DisplayName("porcentaje correcto con 2 de 4 finalizados = 50%")
        void porcentajeMitad() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            c.agregarPartido(partidoFinalizado());
            c.agregarPartido(partidoFinalizado());
            c.agregarPartido(partidoConEstado(false));
            c.agregarPartido(partidoConEstado(false));

            assertThat(c.getPorcentajePartidosJugados()).isEqualTo(50.0);
        }

        @Test
        @DisplayName("porcentaje 100% si todos finalizados")
        void porcentajeTotal() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);
            c.agregarPartido(partidoFinalizado());
            c.agregarPartido(partidoFinalizado());

            assertThat(c.getPorcentajePartidosJugados()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("porcentaje 0% si ninguno finalizado")
        void porcentajeCero() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);
            c.agregarPartido(partidoConEstado(false));
            c.agregarPartido(partidoConEstado(false));

            assertThat(c.getPorcentajePartidosJugados()).isZero();
        }
    }


    @Nested
    @DisplayName("getClubesParticipantes")
    class ClubesParticipantes {

        @Test
        @DisplayName("sin partidos devuelve lista vacía")
        void sinPartidos() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);
            assertThat(c.getClubesParticipantes()).isEmpty();
        }

        @Test
        @DisplayName("extrae local y visitante de todos los partidos, sin duplicados")
        void extraeUnicos() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            Equipo a = equipo("A");
            Equipo b = equipo("B");
            Equipo d = equipo("D");

            Partido p1 = partidoConEquipos(a, b);
            Partido p2 = partidoConEquipos(a, d);
            Partido p3 = partidoConEquipos(b, d);

            c.agregarPartido(p1);
            c.agregarPartido(p2);
            c.agregarPartido(p3);

            assertThat(c.getClubesParticipantes())
                    .containsExactlyInAnyOrder(a, b, d)
                    .hasSize(3);
        }

        @Test
        @DisplayName("ignora equipos null")
        void ignoraNulls() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            Equipo a = equipo("A");
            Partido p = partidoConEquipos(a, null);
            c.agregarPartido(p);

            assertThat(c.getClubesParticipantes()).containsExactly(a);
        }

        @Test
        @DisplayName("mismo equipo como local y visitante no se duplica")
        void mismoEquipoNoDuplica() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            Equipo a = equipo("A");
            Partido p = partidoConEquipos(a, a);
            c.agregarPartido(p);

            assertThat(c.getClubesParticipantes()).containsExactly(a);
        }
    }


    @Nested
    @DisplayName("iniciarCompeticion")
    class IniciarCompeticion {

        @Test
        @DisplayName("desde POR_INICIAR pasa a EN_CURSO")
        void desdePorIniciar() {
            Competicion c = competicionBase(EstadoCompeticion.POR_INICIAR);

            c.iniciarCompeticion();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.EN_CURSO);
        }

        @Test
        @DisplayName("desde EN_CURSO se mantiene EN_CURSO")
        void desdeEnCurso() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            c.iniciarCompeticion();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.EN_CURSO);
        }

        @Test
        @DisplayName("desde SUSPENDIDA pasa a EN_CURSO")
        void desdeSuspendida() {
            Competicion c = competicionBase(EstadoCompeticion.SUSPENDIDA);

            c.iniciarCompeticion();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.EN_CURSO);
        }

        @Test
        @DisplayName("desde FINALIZADA lanza IllegalStateException")
        void desdeFinalizadaLanza() {
            Competicion c = competicionBase(EstadoCompeticion.FINALIZADA);

            assertThatThrownBy(c::iniciarCompeticion)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("finalizada");
        }
    }

    @Nested
    @DisplayName("finalizarCompeticion")
    class FinalizarCompeticion {

        @Test
        @DisplayName("desde EN_CURSO pasa a FINALIZADA")
        void desdeEnCurso() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            c.finalizarCompeticion();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.FINALIZADA);
        }

        @Test
        @DisplayName("desde POR_INICIAR pasa a FINALIZADA")
        void desdePorIniciar() {
            Competicion c = competicionBase(EstadoCompeticion.POR_INICIAR);

            c.finalizarCompeticion();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.FINALIZADA);
        }

        @Test
        @DisplayName("desde SUSPENDIDA pasa a FINALIZADA")
        void desdeSuspendida() {
            Competicion c = competicionBase(EstadoCompeticion.SUSPENDIDA);

            c.finalizarCompeticion();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.FINALIZADA);
        }

        @Test
        @DisplayName("desde FINALIZADA lanza IllegalStateException")
        void desdeFinalizadaLanza() {
            Competicion c = competicionBase(EstadoCompeticion.FINALIZADA);

            assertThatThrownBy(c::finalizarCompeticion)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("finalizada");
        }
    }

    @Nested
    @DisplayName("suspender")
    class Suspender {

        @Test
        @DisplayName("desde EN_CURSO pasa a SUSPENDIDA")
        void desdeEnCurso() {
            Competicion c = competicionBase(EstadoCompeticion.EN_CURSO);

            c.suspender();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.SUSPENDIDA);
        }

        @Test
        @DisplayName("desde POR_INICIAR pasa a SUSPENDIDA")
        void desdePorIniciar() {
            Competicion c = competicionBase(EstadoCompeticion.POR_INICIAR);

            c.suspender();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.SUSPENDIDA);
        }

        @Test
        @DisplayName("desde FINALIZADA lanza IllegalStateException")
        void desdeFinalizadaLanza() {
            Competicion c = competicionBase(EstadoCompeticion.FINALIZADA);

            assertThatThrownBy(c::suspender)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("finalizada");
        }
    }

    @Nested
    @DisplayName("reanudar")
    class Reanudar {

        @Test
        @DisplayName("desde SUSPENDIDA pasa a EN_CURSO")
        void desdeSuspendida() {
            Competicion c = competicionBase(EstadoCompeticion.SUSPENDIDA);

            c.reanudar();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.EN_CURSO);
        }

        @Test
        @DisplayName("desde POR_INICIAR pasa a EN_CURSO")
        void desdePorIniciar() {
            Competicion c = competicionBase(EstadoCompeticion.POR_INICIAR);

            c.reanudar();

            assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.EN_CURSO);
        }

        @Test
        @DisplayName("desde FINALIZADA lanza IllegalStateException")
        void desdeFinalizadaLanza() {
            Competicion c = competicionBase(EstadoCompeticion.FINALIZADA);

            assertThatThrownBy(c::reanudar)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("finalizada");
        }
    }


    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("mismo ID son iguales")
        void mismoId() {
            Competicion c1 = Competicion.builder().idCompeticion(ID_COMPETICION).nombre("A").build();
            Competicion c2 = Competicion.builder().idCompeticion(ID_COMPETICION).nombre("B").build();

            assertThat(c1).isEqualTo(c2);
            assertThat(c1).hasSameHashCodeAs(c2);
        }

        @Test
        @DisplayName("distinto ID no son iguales")
        void distintoId() {
            Competicion c1 = Competicion.builder().idCompeticion(ID_COMPETICION).build();
            Competicion c2 = Competicion.builder().idCompeticion(UUID.randomUUID()).build();

            assertThat(c1).isNotEqualTo(c2);
        }
    }


    private Partido partidoFinalizado() {
        Partido p = new Partido();
        p.setEstado(com.futbol.estadisticas.domain.model.enums.EstadoPartido.FINALIZADO);
        return p;
    }

    private Partido partidoConEstado(boolean finalizado) {
        Partido p = new Partido();
        p.setEstado(finalizado
                ? com.futbol.estadisticas.domain.model.enums.EstadoPartido.FINALIZADO
                : com.futbol.estadisticas.domain.model.enums.EstadoPartido.PROGRAMADO);
        return p;
    }

    private Partido partidoConEquipos(Equipo local, Equipo visitante) {
        Partido p = new Partido();
        p.setEquipoLocal(local);
        p.setEquipoVisitante(visitante);
        return p;
    }

    private Equipo equipo(String nombre) {
        return Equipo.builder()
                .idEquipo(UUID.randomUUID())
                .nombre(nombre)
                .nombreCorto(nombre)
                .build();
    }
}