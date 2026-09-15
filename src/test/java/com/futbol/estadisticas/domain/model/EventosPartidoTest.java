package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventosPartido")
class EventosPartidoTest {

    @Mock private Partido partido;
    @Mock private PersonalDeportivo personal;
    @Mock private Equipo equipo;

    private static final UUID ID_EVENTO = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private EventosPartido.EventosPartidoBuilder base() {
        return EventosPartido.builder().idEvento(ID_EVENTO);
    }

    private static LocalTime minuto(int m) {
        return LocalTime.of(m / 60, m % 60, 0);
    }

    @BeforeEach
    void setUp() {
        lenient().when(personal.getNombreCompleto()).thenReturn("Bukayo Saka");
        lenient().when(equipo.getNombreCorto()).thenReturn("ARS");
    }

    @Nested
    @DisplayName("getMinutoFormateado")
    class GetMinutoFormateado {

        @Test
        @DisplayName("devuelve 0' si minuto o estado son null")
        void nulls() {
            assertThat(base().build().getMinutoFormateado()).isEqualTo("0'");
            assertThat(base().minuto(minuto(23)).build().getMinutoFormateado()).isEqualTo("0'");
        }

        @Test
        @DisplayName("ENTRETIEMPO y ENTRETIEMPO_PRORROGA devuelven 'Descanso'")
        void descanso() {
            assertThat(base().minuto(minuto(45)).estadoEvento(EstadoPartido.ENTRETIEMPO)
                    .build().getMinutoFormateado()).isEqualTo("Descanso");
            assertThat(base().minuto(minuto(105)).estadoEvento(EstadoPartido.ENTRETIEMPO_PRORROGA)
                    .build().getMinutoFormateado()).isEqualTo("Descanso");
        }

        @Test
        @DisplayName("ESPERANDO_PRORROGA devuelve 'Esperando prórroga'")
        void esperandoProrroga() {
            assertThat(base().minuto(minuto(90)).estadoEvento(EstadoPartido.ESPERANDO_PRORROGA)
                    .build().getMinutoFormateado()).isEqualTo("Esperando prórroga");
        }

        @Test
        @DisplayName("PENALTIS devuelve 'Penaltis'")
        void penaltis() {
            assertThat(base().minuto(minuto(120)).estadoEvento(EstadoPartido.PENALTIS)
                    .build().getMinutoFormateado()).isEqualTo("Penaltis");
        }

        @Test
        @DisplayName("PROGRAMADO devuelve 'Programado'")
        void programado() {
            assertThat(base().minuto(minuto(0)).estadoEvento(EstadoPartido.PROGRAMADO)
                    .build().getMinutoFormateado()).isEqualTo("Programado");
        }

        @Test
        @DisplayName("FINALIZADO con minuto <= 90 devuelve 'N''")
        void finalizadoNormal() {
            assertThat(base().minuto(minuto(23)).estadoEvento(EstadoPartido.FINALIZADO)
                    .build().getMinutoFormateado()).isEqualTo("23'");
            assertThat(base().minuto(minuto(90)).estadoEvento(EstadoPartido.FINALIZADO)
                    .build().getMinutoFormateado()).isEqualTo("90'");
        }

        @Test
        @DisplayName("FINALIZADO con minuto > 90 devuelve '90+X''")
        void finalizadoConAgregado() {
            assertThat(base().minuto(minuto(93)).estadoEvento(EstadoPartido.FINALIZADO)
                    .build().getMinutoFormateado()).isEqualTo("90+3'");
            assertThat(base().minuto(minuto(95)).estadoEvento(EstadoPartido.CANCELADO)
                    .build().getMinutoFormateado()).isEqualTo("90+5'");
        }

        @Test
        @DisplayName("PRIMER_TIEMPO devuelve 'N''")
        void primerTiempo() {
            assertThat(base().minuto(minuto(23)).estadoEvento(EstadoPartido.PRIMER_TIEMPO)
                    .build().getMinutoFormateado()).isEqualTo("23'");
        }

        @Test
        @DisplayName("AGREGADO_PRIMER_TIEMPO devuelve '45+X''")
        void agregadoPrimerTiempo() {
            assertThat(base().minuto(minuto(47)).estadoEvento(EstadoPartido.AGREGADO_PRIMER_TIEMPO)
                    .build().getMinutoFormateado()).isEqualTo("45+2'");
        }

        @Test
        @DisplayName("SEGUNDO_TIEMPO devuelve 'N''")
        void segundoTiempo() {
            assertThat(base().minuto(minuto(67)).estadoEvento(EstadoPartido.SEGUNDO_TIEMPO)
                    .build().getMinutoFormateado()).isEqualTo("67'");
        }

        @Test
        @DisplayName("AGREGADO_SEGUNDO_TIEMPO devuelve '90+X''")
        void agregadoSegundoTiempo() {
            assertThat(base().minuto(minuto(92)).estadoEvento(EstadoPartido.AGREGADO_SEGUNDO_TIEMPO)
                    .build().getMinutoFormateado()).isEqualTo("90+2'");
        }

        @Test
        @DisplayName("PRIMER_TIEMPO_PRORROGA devuelve 'N''")
        void primeraProrroga() {
            assertThat(base().minuto(minuto(100)).estadoEvento(EstadoPartido.PRIMER_TIEMPO_PRORROGA)
                    .build().getMinutoFormateado()).isEqualTo("100'");
        }

        @Test
        @DisplayName("AGREGADO_PRORROGA_PRIMER devuelve '105+X''")
        void agregadoPrimerProrroga() {
            assertThat(base().minuto(minuto(107)).estadoEvento(EstadoPartido.AGREGADO_PRORROGA_PRIMER)
                    .build().getMinutoFormateado()).isEqualTo("105+2'");
        }

        @Test
        @DisplayName("SEGUNDO_TIEMPO_PRORROGA devuelve 'N''")
        void segundaProrroga() {
            assertThat(base().minuto(minuto(115)).estadoEvento(EstadoPartido.SEGUNDO_TIEMPO_PRORROGA)
                    .build().getMinutoFormateado()).isEqualTo("115'");
        }

        @Test
        @DisplayName("AGREGADO_PRORROGA_SEGUNDO devuelve '120+X''")
        void agregadoSegundaProrroga() {
            assertThat(base().minuto(minuto(122)).estadoEvento(EstadoPartido.AGREGADO_PRORROGA_SEGUNDO)
                    .build().getMinutoFormateado()).isEqualTo("120+2'");
        }

        @Test
        @DisplayName("suma 1 minuto si hay segundos > 0")
        void conSegundos() {
            assertThat(base().minuto(LocalTime.of(0, 23, 30)).estadoEvento(EstadoPartido.PRIMER_TIEMPO)
                    .build().getMinutoFormateado()).isEqualTo("24'");
        }
    }

    @Nested
    @DisplayName("es*()")
    class MetodosEsTipo {

        @Test
        @DisplayName("esGol: true solo para GOL")
        void esGol() {
            assertThat(base().tipoEvento(TipoEvento.GOL).build().esGol()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.AUTOGOL).build().esGol()).isFalse();
            assertThat(base().tipoEvento(TipoEvento.PENALTI_ANOTADO).build().esGol()).isFalse();
        }

        @Test
        @DisplayName("esAutoGol: true solo para AUTOGOL")
        void esAutoGol() {
            assertThat(base().tipoEvento(TipoEvento.AUTOGOL).build().esAutoGol()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.GOL).build().esAutoGol()).isFalse();
        }

        @Test
        @DisplayName("esGolDePenal: true solo para PENALTI_ANOTADO")
        void esGolDePenal() {
            assertThat(base().tipoEvento(TipoEvento.PENALTI_ANOTADO).build().esGolDePenal()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.PENALTI_FALLADO).build().esGolDePenal()).isFalse();
        }

        @Test
        @DisplayName("esPenalFallado: true solo para PENALTI_FALLADO")
        void esPenalFallado() {
            assertThat(base().tipoEvento(TipoEvento.PENALTI_FALLADO).build().esPenalFallado()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.PENALTI_ANOTADO).build().esPenalFallado()).isFalse();
        }

        @Test
        @DisplayName("esTarjeta: true para AMARILLA y ROJA")
        void esTarjeta() {
            assertThat(base().tipoEvento(TipoEvento.AMARILLA).build().esTarjeta()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.ROJA).build().esTarjeta()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.GOL).build().esTarjeta()).isFalse();
        }

        @Test
        @DisplayName("esSustitucion: true para SUB_IN y SUB_OUT")
        void esSustitucion() {
            assertThat(base().tipoEvento(TipoEvento.SUB_IN).build().esSustitucion()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.SUB_OUT).build().esSustitucion()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.GOL).build().esSustitucion()).isFalse();
        }

        @Test
        @DisplayName("esPenalti: true para los tres tipos de penalti")
        void esPenalti() {
            assertThat(base().tipoEvento(TipoEvento.PENALTI_CONCEDIDO).build().esPenalti()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.PENALTI_ANOTADO).build().esPenalti()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.PENALTI_FALLADO).build().esPenalti()).isTrue();
            assertThat(base().tipoEvento(TipoEvento.GOL).build().esPenalti()).isFalse();
        }

        @Test
        @DisplayName("tipoEvento null: todos los es*() devuelven false")
        void tipoNull() {
            EventosPartido e = base().build();

            assertThat(e.esGol()).isFalse();
            assertThat(e.esAutoGol()).isFalse();
            assertThat(e.esGolDePenal()).isFalse();
            assertThat(e.esPenalFallado()).isFalse();
            assertThat(e.esTarjeta()).isFalse();
            assertThat(e.esSustitucion()).isFalse();
            assertThat(e.esPenalti()).isFalse();
        }
    }

    @Nested
    @DisplayName("getColorTarjeta")
    class GetColorTarjeta {

        @Test
        @DisplayName("AMARILLA devuelve 'Amarilla'")
        void amarilla() {
            assertThat(base().tipoEvento(TipoEvento.AMARILLA).build().getColorTarjeta())
                    .isEqualTo("Amarilla");
        }

        @Test
        @DisplayName("ROJA devuelve 'Roja'")
        void roja() {
            assertThat(base().tipoEvento(TipoEvento.ROJA).build().getColorTarjeta())
                    .isEqualTo("Roja");
        }

        @Test
        @DisplayName("otros tipos devuelven null")
        void otros() {
            assertThat(base().tipoEvento(TipoEvento.GOL).build().getColorTarjeta()).isNull();
            assertThat(base().build().getColorTarjeta()).isNull();
        }
    }

    @Nested
    @DisplayName("getNombreJugador")
    class GetNombreJugador {

        @Test
        @DisplayName("devuelve el nombre completo del personal")
        void conPersonal() {
            assertThat(base().personal(personal).build().getNombreJugador())
                    .isEqualTo("Bukayo Saka");
        }

        @Test
        @DisplayName("devuelve 'Desconocido' si personal es null")
        void sinPersonal() {
            assertThat(base().build().getNombreJugador()).isEqualTo("Desconocido");
        }
    }

    @Nested
    @DisplayName("getNombreEquipoFavorecido")
    class GetNombreEquipoFavorecido {

        @Test
        @DisplayName("devuelve el nombre corto del equipo")
        void conEquipo() {
            assertThat(base().equipoFavorecido(equipo).build().getNombreEquipoFavorecido())
                    .isEqualTo("ARS");
        }

        @Test
        @DisplayName("devuelve 'Ninguno' si equipo es null")
        void sinEquipo() {
            assertThat(base().build().getNombreEquipoFavorecido()).isEqualTo("Ninguno");
        }
    }

    @Nested
    @DisplayName("getDescripcionCompleta")
    class GetDescripcionCompleta {

        @Test
        @DisplayName("devuelve la descripción si no está vacía")
        void descripcionExplicita() {
            assertThat(base().descripcion("Golazo de Saka").build().getDescripcionCompleta())
                    .isEqualTo("Golazo de Saka");
        }

        @Test
        @DisplayName("ignora descripción vacía y genera una")
        void descripcionVacia() {
            EventosPartido e = base()
                    .descripcion("")
                    .minuto(minuto(23))
                    .estadoEvento(EstadoPartido.PRIMER_TIEMPO)
                    .tipoEvento(TipoEvento.GOL)
                    .build();

            assertThat(e.getDescripcionCompleta()).isEqualTo("23' - GOL");
        }

        @Test
        @DisplayName("incluye el nombre del personal si existe")
        void conPersonal() {
            EventosPartido e = base()
                    .minuto(minuto(23))
                    .estadoEvento(EstadoPartido.PRIMER_TIEMPO)
                    .tipoEvento(TipoEvento.GOL)
                    .personal(personal)
                    .build();

            assertThat(e.getDescripcionCompleta()).isEqualTo("23' - GOL - Bukayo Saka");
        }

        @Test
        @DisplayName("incluye el equipo favorecido entre paréntesis si es gol")
        void conEquipoYGol() {
            EventosPartido e = base()
                    .minuto(minuto(23))
                    .estadoEvento(EstadoPartido.PRIMER_TIEMPO)
                    .tipoEvento(TipoEvento.GOL)
                    .personal(personal)
                    .equipoFavorecido(equipo)
                    .build();

            assertThat(e.getDescripcionCompleta())
                    .isEqualTo("23' - GOL - Bukayo Saka (ARS)");
        }

        @Test
        @DisplayName("no incluye el equipo si no es gol")
        void sinGol() {
            EventosPartido e = base()
                    .minuto(minuto(45))
                    .estadoEvento(EstadoPartido.PRIMER_TIEMPO)
                    .tipoEvento(TipoEvento.AMARILLA)
                    .equipoFavorecido(equipo)
                    .build();

            assertThat(e.getDescripcionCompleta())
                    .isEqualTo("45' - AMARILLA");
        }

        @Test
        @DisplayName("usa '0:00' si minuto es null y no hay descripción")
        void minutoNull() {
            EventosPartido e = base().tipoEvento(TipoEvento.GOL).build();

            assertThat(e.getDescripcionCompleta()).isEqualTo("0:00 - GOL");
        }

        @Test
        @DisplayName("usa 'Evento' si tipoEvento es null")
        void tipoNull() {
            EventosPartido e = base().descripcion("").build();

            assertThat(e.getDescripcionCompleta()).isEqualTo("0:00 - Evento");
        }
    }

    @Nested
    @DisplayName("validarMinutoCreate")
    class ValidarMinutoCreate {

        @Test
        @DisplayName("no hace nada si minuto o partido son null")
        void nulls() {
            base().estadoEvento(EstadoPartido.PRIMER_TIEMPO).build()
                    .validarMinutoCreate(null);

            base().minuto(null).estadoEvento(EstadoPartido.PRIMER_TIEMPO).build()
                    .validarMinutoCreate(partido);
        }

        @Test
        @DisplayName("ENTRETIEMPO lanza excepción")
        void entretiempo() {
            when(partido.getEstado()).thenReturn(EstadoPartido.ENTRETIEMPO);
            EventosPartido e = base().minuto(minuto(45)).build();

            assertThatThrownBy(() -> e.validarMinutoCreate(partido))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("No se pueden registrar eventos durante el");
        }

        @Test
        @DisplayName("ENTRETIEMPO_PRORROGA lanza excepción")
        void entretiempoProrroga() {
            when(partido.getEstado()).thenReturn(EstadoPartido.ENTRETIEMPO_PRORROGA);
            EventosPartido e = base().minuto(minuto(105)).build();

            assertThatThrownBy(() -> e.validarMinutoCreate(partido))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("ESPERANDO_PRORROGA lanza excepción")
        void esperandoProrroga() {
            when(partido.getEstado()).thenReturn(EstadoPartido.ESPERANDO_PRORROGA);
            EventosPartido e = base().minuto(minuto(90)).build();

            assertThatThrownBy(() -> e.validarMinutoCreate(partido))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("FINALIZADO, CANCELADO y SUSPENDIDO lanzan excepción")
        void finalizados() {
            EventosPartido e = base().minuto(minuto(90)).build();

            when(partido.getEstado()).thenReturn(EstadoPartido.FINALIZADO);
            assertThatThrownBy(() -> e.validarMinutoCreate(partido))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("finalizado");

            when(partido.getEstado()).thenReturn(EstadoPartido.CANCELADO);
            assertThatThrownBy(() -> e.validarMinutoCreate(partido))
                    .isInstanceOf(IllegalStateException.class);

            when(partido.getEstado()).thenReturn(EstadoPartido.SUSPENDIDO);
            assertThatThrownBy(() -> e.validarMinutoCreate(partido))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("PROGRAMADO lanza excepción")
        void programado() {
            when(partido.getEstado()).thenReturn(EstadoPartido.PROGRAMADO);
            EventosPartido e = base().minuto(minuto(0)).build();

            assertThatThrownBy(() -> e.validarMinutoCreate(partido))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("programado");
        }

        @Test
        @DisplayName("tiempo válido con minuto > 120 lanza excepción")
        void tiempoValidoConExceso() {
            when(partido.getEstado()).thenReturn(EstadoPartido.SEGUNDO_TIEMPO);
            EventosPartido e = base().minuto(minuto(121)).build();

            assertThatThrownBy(() -> e.validarMinutoCreate(partido))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("excede el límite máximo");
        }

        @Test
        @DisplayName("PRIMER_TIEMPO con minuto <= 45 es válido")
        void primerTiempoValido() {
            when(partido.getEstado()).thenReturn(EstadoPartido.PRIMER_TIEMPO);
            EventosPartido e = base().minuto(minuto(23)).build();

            e.validarMinutoCreate(partido);
        }

        @Test
        @DisplayName("PRIMER_TIEMPO con minuto > 45 lanza excepción")
        void primerTiempoExcedido() {
            when(partido.getEstado()).thenReturn(EstadoPartido.PRIMER_TIEMPO);
            EventosPartido e = base().minuto(minuto(50)).build();

            assertThatThrownBy(() -> e.validarMinutoCreate(partido))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("sin tiempo agregado");
        }

        @Test
        @DisplayName("AGREGADO_PRIMER_TIEMPO con minuto <= 120 es válido")
        void agregadoPrimerTiempoValido() {
            when(partido.getEstado()).thenReturn(EstadoPartido.AGREGADO_PRIMER_TIEMPO);
            EventosPartido e = base().minuto(minuto(47)).build();

            e.validarMinutoCreate(partido);
        }
    }

    @Nested
    @DisplayName("esEstadisticable")
    class EsEstadisticable {

        @ParameterizedTest
        @EnumSource(value = TipoEvento.class, names = {"GOL", "AUTOGOL", "PENALTI_ANOTADO"})
        @DisplayName("es true para eventos de gol")
        void goles(TipoEvento tipo) {
            assertThat(base().tipoEvento(tipo).build().esEstadisticable()).isTrue();
        }

        @Test
        @DisplayName("es true para ASISTENCIA")
        void asistencia() {
            assertThat(base().tipoEvento(TipoEvento.ASISTENCIA).build().esEstadisticable()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(value = TipoEvento.class, names = {"AMARILLA", "ROJA"})
        @DisplayName("es true para tarjetas")
        void tarjetas(TipoEvento tipo) {
            assertThat(base().tipoEvento(tipo).build().esEstadisticable()).isTrue();
        }

        @Test
        @DisplayName("es false para CORNER y otros eventos no estadísticos")
        void noEstadisticos() {
            assertThat(base().tipoEvento(TipoEvento.CORNER).build().esEstadisticable()).isFalse();
            assertThat(base().tipoEvento(TipoEvento.SUB_IN).build().esEstadisticable()).isFalse();
            assertThat(base().build().esEstadisticable()).isFalse();
        }
    }

    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("mismo ID son iguales aunque el resto difiera")
        void mismoId() {
            EventosPartido e1 = base().descripcion("A").build();
            EventosPartido e2 = base().descripcion("B").build();

            assertThat(e1).isEqualTo(e2);
            assertThat(e1).hasSameHashCodeAs(e2);
        }

        @Test
        @DisplayName("distinto ID no son iguales")
        void distintoId() {
            EventosPartido e1 = base().idEvento(UUID.randomUUID()).build();
            EventosPartido e2 = base().idEvento(UUID.randomUUID()).build();

            assertThat(e1).isNotEqualTo(e2);
        }
    }
}