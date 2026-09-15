package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.TipoContrato;
import com.futbol.estadisticas.domain.model.enums.TipoEquipo;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Contrato")
class ContratoTest {

    @Mock private Equipo clubProfesional;
    @Mock private Equipo clubAmateur;
    @Mock private Equipo filial;
    @Mock private Equipo seleccionAbsoluta;
    @Mock private Equipo seleccionJuvenil;
    @Mock private Jugador jugadorAdulto;
    @Mock private Jugador jugadorMenor;

    @BeforeEach
    void setUp() {
        lenient().when(clubProfesional.getTipo()).thenReturn(TipoEquipo.CLUB_PROFESIONAL);
        lenient().when(clubProfesional.getNombre()).thenReturn("River Plate");
        lenient().when(clubProfesional.getPais()).thenReturn(Nacion.ARGENTINA);

        lenient().when(clubAmateur.getTipo()).thenReturn(TipoEquipo.CLUB_AMATEUR);
        lenient().when(clubAmateur.getNombre()).thenReturn("Club Barrial");
        lenient().when(clubAmateur.getPais()).thenReturn(Nacion.ARGENTINA);

        lenient().when(filial.getTipo()).thenReturn(TipoEquipo.FILIAL);
        lenient().when(filial.getNombre()).thenReturn("River Reserva");
        lenient().when(filial.getPais()).thenReturn(Nacion.ARGENTINA);

        lenient().when(seleccionAbsoluta.getTipo()).thenReturn(TipoEquipo.SELECCION_ABSOLUTA);
        lenient().when(seleccionAbsoluta.getNombre()).thenReturn("Selección Argentina");
        lenient().when(seleccionAbsoluta.getPais()).thenReturn(Nacion.ARGENTINA);

        lenient().when(seleccionJuvenil.getTipo()).thenReturn(TipoEquipo.SELECCION_JUVENIL);
        lenient().when(seleccionJuvenil.getNombre()).thenReturn("Selección Argentina Sub-20");
        lenient().when(seleccionJuvenil.getPais()).thenReturn(Nacion.ARGENTINA);

        lenient().when(jugadorAdulto.getEdad()).thenReturn(25);
        lenient().when(jugadorAdulto.getNacionalidad()).thenReturn(Nacion.ARGENTINA);
        lenient().when(jugadorAdulto.getContratos()).thenReturn(new ArrayList<>());

        lenient().when(jugadorMenor.getEdad()).thenReturn(16);
        lenient().when(jugadorMenor.getNacionalidad()).thenReturn(Nacion.ARGENTINA);
        lenient().when(jugadorMenor.getContratos()).thenReturn(new ArrayList<>());
    }

    private Contrato.ContratoBuilder base() {
        return Contrato.builder().idContrato(UUID.randomUUID());
    }

    @Nested
    @DisplayName("estaVigente")
    class EstaVigente {

        @Test
        @DisplayName("true con estado ACTIVO y fecha actual dentro del rango")
        void trueActivoEnRango() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();

            assertThat(c.estaVigente()).isTrue();
        }

        @Test
        @DisplayName("false si el estado no es ACTIVO")
        void falseEstadoNoActivo() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .estado(EstadoContrato.FINALIZADO)
                    .build();

            assertThat(c.estaVigente()).isFalse();
        }

        @Test
        @DisplayName("false si todavía no comenzó")
        void falseNoComenzo() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().plusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .build();

            assertThat(c.estaVigente()).isFalse();
        }

        @Test
        @DisplayName("false si ya finalizó por fecha")
        void falseYaFinalizo() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().minusDays(1))
                    .build();

            assertThat(c.estaVigente()).isFalse();
        }

        @Test
        @DisplayName("false si falta fechaInicio o fechaFin")
        void falseFaltanFechas() {
            Contrato sinInicio = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaFin(LocalDateTime.now().plusDays(1)).build();
            Contrato sinFin = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(1)).build();

            assertThat(sinInicio.estaVigente()).isFalse();
            assertThat(sinFin.estaVigente()).isFalse();
        }
    }

    @Nested
    @DisplayName("esCompatibleConTipoEquipo")
    class EsCompatibleConTipoEquipo {

        @Test
        @DisplayName("PROFESIONAL y CESION compatibles con CLUB_PROFESIONAL")
        void profesionalYCesionConClubProfesional() {
            assertThat(base().tipoContrato(TipoContrato.PROFESIONAL).build()
                    .esCompatibleConTipoEquipo(TipoEquipo.CLUB_PROFESIONAL)).isTrue();
            assertThat(base().tipoContrato(TipoContrato.CESION).build()
                    .esCompatibleConTipoEquipo(TipoEquipo.CLUB_PROFESIONAL)).isTrue();
        }

        @Test
        @DisplayName("AMATEUR solo compatible con CLUB_AMATEUR")
        void amateurSoloClubAmateur() {
            Contrato c = base().tipoContrato(TipoContrato.AMATEUR).build();

            assertThat(c.esCompatibleConTipoEquipo(TipoEquipo.CLUB_AMATEUR)).isTrue();
            assertThat(c.esCompatibleConTipoEquipo(TipoEquipo.CLUB_PROFESIONAL)).isFalse();
        }

        @Test
        @DisplayName("CONVOCATORIA solo compatible con selecciones")
        void convocatoriaSoloSelecciones() {
            Contrato c = base().tipoContrato(TipoContrato.CONVOCATORIA).build();

            assertThat(c.esCompatibleConTipoEquipo(TipoEquipo.SELECCION_ABSOLUTA)).isTrue();
            assertThat(c.esCompatibleConTipoEquipo(TipoEquipo.SELECCION_JUVENIL)).isTrue();
            assertThat(c.esCompatibleConTipoEquipo(TipoEquipo.CLUB_PROFESIONAL)).isFalse();
        }

        @Test
        @DisplayName("false si tipo de equipo o de contrato es nulo")
        void falseNulos() {
            assertThat(base().build().esCompatibleConTipoEquipo(TipoEquipo.CLUB_PROFESIONAL)).isFalse();
            assertThat(base().tipoContrato(TipoContrato.PROFESIONAL).build()
                    .esCompatibleConTipoEquipo(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("validarContratoConEquipo")
    class ValidarContratoConEquipo {

        @Test
        @DisplayName("equipo nulo lanza IllegalArgumentException")
        void equipoNulo() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL).build();

            assertThatThrownBy(() -> c.validarContratoConEquipo(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("tipos incompatibles lanzan IllegalStateException")
        void tiposIncompatibles() {
            Contrato c = base().tipoContrato(TipoContrato.AMATEUR).build();

            assertThatThrownBy(() -> c.validarContratoConEquipo(clubProfesional))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no es compatible");
        }

        @Test
        @DisplayName("JUVENIL en club profesional lanza por incompatibilidad de tipo")
        void juvenilEnClubProfesional() {
            Contrato c = base().tipoContrato(TipoContrato.JUVENIL)
                    .personal(jugadorMenor).build();

            assertThatThrownBy(() -> c.validarContratoConEquipo(clubProfesional))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no es compatible");
        }

        @Test
        @DisplayName("JUVENIL con jugador mayor de 18 en filial lanza")
        void juvenilMayorDeEdad() {
            Contrato c = base().tipoContrato(TipoContrato.JUVENIL)
                    .personal(jugadorAdulto).build();

            assertThatThrownBy(() -> c.validarContratoConEquipo(filial))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("menores de 18 años");
        }

        @Test
        @DisplayName("JUVENIL con menor de edad en filial es válido")
        void juvenilValido() {
            Contrato c = base().tipoContrato(TipoContrato.JUVENIL)
                    .personal(jugadorMenor).build();

            c.validarContratoConEquipo(filial);
        }

        @Test
        @DisplayName("CONVOCATORIA con nacionalidad distinta lanza")
        void convocatoriaNacionalidadDistinta() {
            when(jugadorAdulto.getNacionalidad()).thenReturn(Nacion.BRASIL);

            Contrato c = base().tipoContrato(TipoContrato.CONVOCATORIA)
                    .personal(jugadorAdulto)
                    .equipo(seleccionAbsoluta)
                    .build();

            assertThatThrownBy(() -> c.validarContratoConEquipo(seleccionAbsoluta))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("nacionalidad");
        }

        @Test
        @DisplayName("CONVOCATORIA con nacionalidad coincidente es válida")
        void convocatoriaValida() {
            Contrato c = base().tipoContrato(TipoContrato.CONVOCATORIA)
                    .personal(jugadorAdulto)
                    .equipo(seleccionAbsoluta)
                    .build();

            c.validarContratoConEquipo(seleccionAbsoluta);

            c.setEquipo(seleccionJuvenil);
            c.validarContratoConEquipo(seleccionJuvenil);
        }

        @Test
        @DisplayName("AMATEUR con sueldo mayor a 0 lanza")
        void amateurConSueldo() {
            Contrato c = base().tipoContrato(TipoContrato.AMATEUR)
                    .sueldo(1000.0).build();

            assertThatThrownBy(() -> c.validarContratoConEquipo(clubAmateur))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no pueden tener sueldo");
        }

        @Test
        @DisplayName("AMATEUR sin sueldo es válido en club amateur")
        void amateurValido() {
            Contrato c = base().tipoContrato(TipoContrato.AMATEUR).build();
            c.validarContratoConEquipo(clubAmateur);
        }

        @Test
        @DisplayName("CESION sin contrato profesional previo lanza")
        void cesionSinProfesional() {
            Contrato c = base().tipoContrato(TipoContrato.CESION)
                    .personal(jugadorAdulto).build();

            assertThatThrownBy(() -> c.validarContratoConEquipo(clubProfesional))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no tiene un contrato PROFESIONAL previo");
        }

        @Test
        @DisplayName("CESION con contrato profesional previo es válida")
        void cesionValida() {
            Contrato profesional = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .personal(jugadorAdulto).build();
            jugadorAdulto.getContratos().add(profesional);

            Contrato cesion = base().tipoContrato(TipoContrato.CESION)
                    .personal(jugadorAdulto).build();

            cesion.validarContratoConEquipo(clubProfesional);
        }

        @Test
        @DisplayName("PROFESIONAL solo válido en club profesional, filial o reserva")
        void profesionalSoloValidoEn() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL).build();

            c.validarContratoConEquipo(clubProfesional);
            c.validarContratoConEquipo(filial);

            assertThatThrownBy(() -> c.validarContratoConEquipo(clubAmateur))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("validarContratoConPersonal")
    class ValidarContratoConPersonal {

        @Test
        @DisplayName("personal nulo lanza IllegalArgumentException")
        void personalNulo() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL).build();

            assertThatThrownBy(() -> c.validarContratoConPersonal(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("rechaza segunda CONVOCATORIA vigente")
        void rechazaSegundaConvocatoria() {
            Contrato convocatoriaExistente = base()
                    .tipoContrato(TipoContrato.CONVOCATORIA)
                    .equipo(seleccionAbsoluta)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            jugadorAdulto.getContratos().add(convocatoriaExistente);

            Contrato nueva = base().tipoContrato(TipoContrato.CONVOCATORIA)
                    .equipo(seleccionJuvenil).build();

            assertThatThrownBy(() -> nueva.validarContratoConPersonal(jugadorAdulto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ya tiene un contrato de CONVOCATORIA vigente");
        }

        @Test
        @DisplayName("rechaza CESION si ya tiene CESION vigente")
        void rechazaCesionVigente() {
            Contrato cesionExistente = base()
                    .tipoContrato(TipoContrato.CESION)
                    .equipo(clubProfesional)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            jugadorAdulto.getContratos().add(cesionExistente);

            Contrato nueva = base().tipoContrato(TipoContrato.CESION)
                    .equipo(clubProfesional).build();

            assertThatThrownBy(() -> nueva.validarContratoConPersonal(jugadorAdulto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ya tiene un contrato de CESION vigente");
        }

        @Test
        @DisplayName("rechaza dos clubes distintos")
        void rechazaDosClubes() {
            Contrato conClub = base()
                    .tipoContrato(TipoContrato.PROFESIONAL)
                    .equipo(clubProfesional)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            jugadorAdulto.getContratos().add(conClub);

            Contrato conOtroClub = base()
                    .tipoContrato(TipoContrato.AMATEUR)
                    .equipo(clubAmateur).build();

            assertThatThrownBy(() -> conOtroClub.validarContratoConPersonal(jugadorAdulto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("dos clubes diferentes");
        }
        @Test
        @DisplayName("rechaza dos selecciones distintas")
        void rechazaDosSelecciones() {
            Contrato seleccionExistente = base()
                    .tipoContrato(TipoContrato.CESION)
                    .equipo(seleccionAbsoluta)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            jugadorAdulto.getContratos().add(seleccionExistente);

            Contrato nueva = base().tipoContrato(TipoContrato.CONVOCATORIA)
                    .equipo(seleccionJuvenil).build();

            assertThatThrownBy(() -> nueva.validarContratoConPersonal(jugadorAdulto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("No puede tener contratos con dos selecciones diferentes");
        }

        @Test
        @DisplayName("permite club profesional y selección simultáneos")
        void permiteClubYSeleccion() {
            Contrato conClub = base()
                    .tipoContrato(TipoContrato.PROFESIONAL)
                    .equipo(clubProfesional)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            jugadorAdulto.getContratos().add(conClub);

            Contrato convocatoria = base()
                    .tipoContrato(TipoContrato.CONVOCATORIA)
                    .equipo(seleccionAbsoluta).build();

            convocatoria.validarContratoConPersonal(jugadorAdulto);
        }

        @Test
        @DisplayName("ignora el propio contrato al comparar por id")
        void ignoraPropioContrato() {
            UUID id = UUID.randomUUID();
            Contrato c = base()
                    .idContrato(id)
                    .tipoContrato(TipoContrato.PROFESIONAL)
                    .equipo(clubProfesional)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            jugadorAdulto.getContratos().add(c);

            c.validarContratoConPersonal(jugadorAdulto);
        }
    }

    @ParameterizedTest
    @EnumSource(value = TipoContrato.class, names = {"PROFESIONAL", "CONVOCATORIA", "CESION", "JUVENIL", "AMATEUR"})
    @DisplayName("es*() identifica correctamente el tipo")
    void metodosEsTipo(TipoContrato tipo) {
        Contrato c = base().tipoContrato(tipo).build();

        assertThat(c.esProfesional()).isEqualTo(tipo == TipoContrato.PROFESIONAL);
        assertThat(c.esConvocatoria()).isEqualTo(tipo == TipoContrato.CONVOCATORIA);
        assertThat(c.esCesion()).isEqualTo(tipo == TipoContrato.CESION);
        assertThat(c.esJuvenil()).isEqualTo(tipo == TipoContrato.JUVENIL);
        assertThat(c.esAmateur()).isEqualTo(tipo == TipoContrato.AMATEUR);
    }

    @Nested
    @DisplayName("finalizar")
    class Finalizar {

        @Test
        @DisplayName("lanza si ya está finalizado")
        void yaFinalizado() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .estado(EstadoContrato.FINALIZADO)
                    .build();

            assertThatThrownBy(() -> c.finalizar(LocalDateTime.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ya está finalizado");
        }

        @Test
        @DisplayName("marca FINALIZADO y actualiza fechaFin")
        void marcaFinalizado() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .build();
            LocalDateTime fecha = LocalDateTime.now();

            c.finalizar(fecha);

            assertThat(c.getEstado()).isEqualTo(EstadoContrato.FINALIZADO);
            assertThat(c.getFechaFin()).isEqualTo(fecha);
        }

        @Test
        @DisplayName("si el personal es Jugador con DatosDeportivos, lo deja LIBRE")
        void dejaJugadorLibre() {
            DatosDeportivos datos = mock(DatosDeportivos.class);
            when(jugadorAdulto.getDatosDeportivos()).thenReturn(datos);

            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .personal(jugadorAdulto)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .build();

            c.finalizar(LocalDateTime.now());

            org.mockito.Mockito.verify(datos).setEstadoJugador(EstadoJugador.LIBRE);
            org.mockito.Mockito.verify(datos).setFechaActualizacion(org.mockito.ArgumentMatchers.any());
        }
    }

    @Nested
    @DisplayName("renovar")
    class Renovar {

        @Test
        @DisplayName("lanza si nueva fecha es anterior a inicio")
        void nuevaFechaAnteriorAInicio() {
            LocalDateTime inicio = LocalDateTime.now().minusDays(10);
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(inicio)
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .build();

            assertThatThrownBy(() -> c.renovar(inicio.minusDays(1)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("lanza si nueva fecha está en el pasado")
        void nuevaFechaEnPasado() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .build();

            assertThatThrownBy(() -> c.renovar(LocalDateTime.now().minusHours(1)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no debe ser en el pasado");
        }

        @Test
        @DisplayName("lanza si el contrato no está ACTIVO")
        void contratoNoActivo() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .estado(EstadoContrato.RESCINDIDO)
                    .build();

            assertThatThrownBy(() -> c.renovar(LocalDateTime.now().plusDays(30)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no activo");
        }

        @Test
        @DisplayName("extiende fechaFin cuando todo es válido")
        void extiendeFechaFin() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .build();
            LocalDateTime nueva = LocalDateTime.now().plusYears(1);

            c.renovar(nueva);

            assertThat(c.getFechaFin()).isEqualTo(nueva);
        }
    }

    @Nested
    @DisplayName("rescindir")
    class Rescindir {

        @Test
        @DisplayName("lanza si el contrato ya está finalizado")
        void yaFinalizado() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .estado(EstadoContrato.FINALIZADO)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().minusDays(1))
                    .build();

            assertThatThrownBy(() -> c.rescindir(LocalDateTime.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Finalizado");
        }

        @Test
        @DisplayName("marca RESCINDIDO y actualiza fechaFin")
        void marcaRescindido() {
            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .build();
            LocalDateTime fecha = LocalDateTime.now();

            c.rescindir(fecha);

            assertThat(c.getEstado()).isEqualTo(EstadoContrato.RESCINDIDO);
            assertThat(c.getFechaFin()).isEqualTo(fecha);
        }

        @Test
        @DisplayName("deja al jugador LIBRE si no le quedan contratos")
        void dejaLibre() {
            DatosDeportivos datos = mock(DatosDeportivos.class);
            when(jugadorAdulto.getDatosDeportivos()).thenReturn(datos);

            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .personal(jugadorAdulto)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .build();

            c.rescindir(LocalDateTime.now());

            org.mockito.Mockito.verify(datos).setEstadoJugador(EstadoJugador.LIBRE);
        }

        @Test
        @DisplayName("no cambia el estado si quedan otros contratos")
        void noCambiaSiQuedanContratos() {
            DatosDeportivos datos = mock(DatosDeportivos.class);
            when(jugadorAdulto.getDatosDeportivos()).thenReturn(datos);

            Contrato c = base().tipoContrato(TipoContrato.PROFESIONAL)
                    .personal(jugadorAdulto)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .fechaFin(LocalDateTime.now().plusDays(10))
                    .build();

            Contrato otro = base()
                    .tipoContrato(TipoContrato.CONVOCATORIA)
                    .equipo(seleccionAbsoluta)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            jugadorAdulto.getContratos().add(otro);

            c.rescindir(LocalDateTime.now());

            org.mockito.Mockito.verify(datos, org.mockito.Mockito.never())
                    .setEstadoJugador(EstadoJugador.LIBRE);
        }
    }

    @Test
    @DisplayName("equals/hashCode solo por idContrato")
    void equalsPorId() {
        UUID id = UUID.randomUUID();
        Contrato c1 = base().idContrato(id).tipoContrato(TipoContrato.PROFESIONAL).build();
        Contrato c2 = base().idContrato(id).tipoContrato(TipoContrato.AMATEUR).build();

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).hasSameHashCodeAs(c2);
    }
}