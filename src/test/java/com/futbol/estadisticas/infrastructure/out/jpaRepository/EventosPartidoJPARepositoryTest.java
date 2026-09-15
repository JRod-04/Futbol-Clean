package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.enums.*;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class EventosPartidoJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired private EventosPartidoJPARepository repository;
    @Autowired private PartidoJPARepository partidoRepository;
    @Autowired private EquipoJPARepository equipoRepository;
    @Autowired private JugadorJPARepository jugadorRepository;
    @Autowired private CompeticionJPARepository competicionRepository;

    private static final UUID ID_EQUIPO_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_EQUIPO_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_JUGADOR_1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID ID_JUGADOR_2 = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID ID_COMPETICION = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID ID_PARTIDO_1 = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final UUID ID_PARTIDO_2 = UUID.fromString("77777777-7777-7777-7777-777777777777");

    private static final UUID ID_EVENTO_GOL_1 = UUID.fromString("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ID_EVENTO_GOL_2 = UUID.fromString("aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ID_EVENTO_AUTOGOL = UUID.fromString("aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ID_EVENTO_PENALTI = UUID.fromString("aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ID_EVENTO_AMARILLA = UUID.fromString("aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ID_EVENTO_ROJA = UUID.fromString("aaaaaaa6-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ID_EVENTO_TIRO = UUID.fromString("aaaaaaa7-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    private static LocalTime minuto(int m) {
        return LocalTime.of(m / 60, m % 60, 0);
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        partidoRepository.deleteAll();
        jugadorRepository.deleteAll();
        equipoRepository.deleteAll();
        competicionRepository.deleteAll();
        repository.flush();
        partidoRepository.flush();
        jugadorRepository.flush();
        equipoRepository.flush();
        competicionRepository.flush();

        EquipoJPAEntity local = EquipoJPAEntity.builder()
                .idEquipo(ID_EQUIPO_1).nombre("Arsenal").nombreCorto("ARS")
                .paisEquipo(Nacion.INGLATERRA).tipo(TipoEquipo.CLUB_PROFESIONAL).build();
        EquipoJPAEntity visitante = EquipoJPAEntity.builder()
                .idEquipo(ID_EQUIPO_2).nombre("Chelsea").nombreCorto("CHE")
                .paisEquipo(Nacion.INGLATERRA).tipo(TipoEquipo.CLUB_PROFESIONAL).build();
        equipoRepository.saveAll(List.of(local, visitante));

        JugadorJPAEntity jugador1 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_1).nombre("Bukayo").apellido("Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .nacionalidad(Nacion.INGLATERRA).pieHabil(JuegoPies.ZURDO)
                .altura(178).peso(70).fechaActualizacion(LocalDate.now()).build();
        JugadorJPAEntity jugador2 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_2).nombre("Cole").apellido("Palmer")
                .fechaNacimiento(LocalDate.of(2002, 5, 6))
                .nacionalidad(Nacion.INGLATERRA).pieHabil(JuegoPies.DERECHO)
                .altura(185).peso(75).fechaActualizacion(LocalDate.now()).build();
        jugadorRepository.saveAll(List.of(jugador1, jugador2));

        CompeticionJPAEntity competicion = CompeticionJPAEntity.builder()
                .idCompeticion(ID_COMPETICION).nombre("Premier League")
                .temporada(Temporada.T2024_25).estado(EstadoCompeticion.EN_CURSO)
                .fechaInicio(NOW.minusMonths(2)).fechaFin(NOW.plusMonths(8)).build();
        competicionRepository.save(competicion);

        PartidoJPAEntity partido1 = PartidoJPAEntity.builder()
                .idPartido(ID_PARTIDO_1).fechaYHora(NOW.minusDays(2))
                .estado(EstadoPartido.FINALIZADO).golesLocal(2).golesVisitante(1)
                .fase(FaseTorneo.LIGA).jornadaTorneo(JornadaPartido.JORNADA_1)
                .equipoLocal(local).equipoVisitante(visitante).competicion(competicion).build();
        PartidoJPAEntity partido2 = PartidoJPAEntity.builder()
                .idPartido(ID_PARTIDO_2).fechaYHora(NOW.plusDays(3))
                .estado(EstadoPartido.PROGRAMADO).golesLocal(0).golesVisitante(0)
                .fase(FaseTorneo.LIGA).jornadaTorneo(JornadaPartido.JORNADA_2)
                .equipoLocal(visitante).equipoVisitante(local).competicion(competicion).build();
        partidoRepository.saveAll(List.of(partido1, partido2));
        partidoRepository.flush();

        EventosPartidoJPAEntity gol1 = evento(ID_EVENTO_GOL_1, partido1, jugador1, local,
                TipoEvento.GOL, EstadoPartido.PRIMER_TIEMPO, minuto(23), "Gol de Saka");
        EventosPartidoJPAEntity gol2 = evento(ID_EVENTO_GOL_2, partido1, jugador2, visitante,
                TipoEvento.GOL, EstadoPartido.SEGUNDO_TIEMPO, minuto(67), "Gol de Palmer");
        EventosPartidoJPAEntity autogol = evento(ID_EVENTO_AUTOGOL, partido1, jugador2, local,
                TipoEvento.AUTOGOL, EstadoPartido.SEGUNDO_TIEMPO, minuto(80), "Autogol");
        EventosPartidoJPAEntity penalti = evento(ID_EVENTO_PENALTI, partido2, jugador1, local,
                TipoEvento.PENALTI_ANOTADO, EstadoPartido.PROGRAMADO, minuto(15), "Penalti");
        EventosPartidoJPAEntity amarilla = evento(ID_EVENTO_AMARILLA, partido1, jugador2, visitante,
                TipoEvento.AMARILLA, EstadoPartido.SEGUNDO_TIEMPO, minuto(55), "Amarilla");
        EventosPartidoJPAEntity roja = evento(ID_EVENTO_ROJA, partido1, jugador1, local,
                TipoEvento.ROJA, EstadoPartido.SEGUNDO_TIEMPO, minuto(90), "Roja");
        EventosPartidoJPAEntity tiro = evento(ID_EVENTO_TIRO, partido2, jugador2, visitante,
                TipoEvento.TIRO_A_PUERTA, EstadoPartido.PROGRAMADO, minuto(12), "Tiro");

        repository.saveAll(List.of(gol1, gol2, autogol, penalti, amarilla, roja, tiro));
        repository.flush();
    }

    private EventosPartidoJPAEntity evento(UUID id, PartidoJPAEntity partido,
                                           PersonalDeportivoJPAEntity personal,
                                           EquipoJPAEntity equipoFavorecido,
                                           TipoEvento tipo, EstadoPartido estado,
                                           LocalTime minuto, String descripcion) {
        return EventosPartidoJPAEntity.builder()
                .idEvento(id)
                .partido(partido)
                .personal(personal)
                .equipoFavorecido(equipoFavorecido)
                .tipoEvento(tipo)
                .estadoEvento(estado)
                .minuto(minuto)
                .descripcion(descripcion)
                .build();
    }

    @Test
    @DisplayName("findByPartidoIdPartido: devuelve todos los eventos del partido")
    void findByPartidoIdPartido() {
        List<EventosPartidoJPAEntity> eventos = repository.findByPartidoIdPartido(ID_PARTIDO_1);

        assertThat(eventos).hasSize(5);
        assertThat(eventos)
                .extracting(EventosPartidoJPAEntity::getIdEvento)
                .containsExactlyInAnyOrder(
                        ID_EVENTO_GOL_1, ID_EVENTO_GOL_2, ID_EVENTO_AUTOGOL,
                        ID_EVENTO_AMARILLA, ID_EVENTO_ROJA
                );
    }

    @Test
    @DisplayName("findByPartidoIdPartido: devuelve vacío si no hay eventos")
    void findByPartidoIdPartidoVacio() {
        assertThat(repository.findByPartidoIdPartido(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("findByPartidoIdPartidoAndTipoEvento: filtra por tipo")
    void findByPartidoAndTipoEvento() {
        List<EventosPartidoJPAEntity> goles = repository
                .findByPartidoIdPartidoAndTipoEvento(ID_PARTIDO_1, TipoEvento.GOL);

        assertThat(goles).hasSize(2);
        assertThat(goles)
                .extracting(EventosPartidoJPAEntity::getIdEvento)
                .containsExactlyInAnyOrder(ID_EVENTO_GOL_1, ID_EVENTO_GOL_2);
    }

    @Test
    @DisplayName("findByPartidoIdPartidoAndTipoEvento: devuelve vacío si no coincide el tipo")
    void findByPartidoAndTipoEventoVacio() {
        List<EventosPartidoJPAEntity> resultado = repository
                .findByPartidoIdPartidoAndTipoEvento(ID_PARTIDO_1, TipoEvento.PENALTI_ANOTADO);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("findByPersonalIdPersonal: devuelve eventos del jugador en cualquier partido")
    void findByPersonalIdPersonal() {
        List<EventosPartidoJPAEntity> eventos = repository.findByPersonalIdPersonal(ID_JUGADOR_1);

        assertThat(eventos).hasSize(3);
        assertThat(eventos)
                .extracting(EventosPartidoJPAEntity::getIdEvento)
                .containsExactlyInAnyOrder(ID_EVENTO_GOL_1, ID_EVENTO_PENALTI, ID_EVENTO_ROJA);
    }

    @Test
    @DisplayName("findByPersonalIdPersonal: devuelve vacío si el jugador no tiene eventos")
    void findByPersonalIdPersonalVacio() {
        assertThat(repository.findByPersonalIdPersonal(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("findGolesByPartido: devuelve goles, autogoles y penaltis anotados")
    void findGolesByPartido() {
        List<EventosPartidoJPAEntity> goles = repository.findGolesByPartido(ID_PARTIDO_1);

        assertThat(goles).hasSize(3);
        assertThat(goles)
                .extracting(EventosPartidoJPAEntity::getTipoEvento)
                .containsExactlyInAnyOrder(
                        TipoEvento.GOL, TipoEvento.GOL, TipoEvento.AUTOGOL
                );
    }

    @Test
    @DisplayName("findGolesByPartido: devuelve vacío si no hay goles")
    void findGolesByPartidoVacio() {
        assertThat(repository.findGolesByPartido(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("findTarjetasByPartido: devuelve amarillas y rojas")
    void findTarjetasByPartido() {
        List<EventosPartidoJPAEntity> tarjetas = repository.findTarjetasByPartido(ID_PARTIDO_1);

        assertThat(tarjetas).hasSize(2);
        assertThat(tarjetas)
                .extracting(EventosPartidoJPAEntity::getTipoEvento)
                .containsExactlyInAnyOrder(TipoEvento.AMARILLA, TipoEvento.ROJA);
    }

    @Test
    @DisplayName("findTarjetasByPartido: devuelve vacío si no hay tarjetas")
    void findTarjetasByPartidoVacio() {
        assertThat(repository.findTarjetasByPartido(ID_PARTIDO_2)).isEmpty();
    }

    @Test
    @DisplayName("findByPersonalIdPersonalConCompeticion: trae eventos con competición cargada")
    void findByPersonalIdPersonalConCompeticion() {
        List<EventosPartidoJPAEntity> eventos =
                repository.findByPersonalIdPersonalConCompeticion(ID_JUGADOR_1);

        assertThat(eventos).hasSize(3);
        assertThat(eventos)
                .allMatch(e -> e.getPartido() != null)
                .allMatch(e -> e.getPartido().getCompeticion() != null)
                .allMatch(e -> e.getPartido().getCompeticion().getIdCompeticion()
                        .equals(ID_COMPETICION));
    }

    @Test
    @DisplayName("findById: devuelve el evento")
    void findById() {
        Optional<EventosPartidoJPAEntity> evento = repository.findById(ID_EVENTO_GOL_1);

        assertThat(evento).isPresent();
        assertThat(evento.get().getTipoEvento()).isEqualTo(TipoEvento.GOL);
        assertThat(evento.get().getDescripcion()).isEqualTo("Gol de Saka");
        assertThat(evento.get().getMinuto()).isEqualTo(minuto(23));
        assertThat(evento.get().getEstadoEvento()).isEqualTo(EstadoPartido.PRIMER_TIEMPO);
    }

    @Test
    @DisplayName("deleteEventoById: elimina el evento")
    void deleteEventoById() {
        assertThat(repository.existsById(ID_EVENTO_TIRO)).isTrue();

        repository.deleteEventoById(ID_EVENTO_TIRO);
        repository.flush();

        assertThat(repository.existsById(ID_EVENTO_TIRO)).isFalse();
        assertThat(repository.count()).isEqualTo(6);
    }

    @Test
    @DisplayName("save: guarda un nuevo evento")
    void save() {
        UUID nuevoId = UUID.randomUUID();

        PartidoJPAEntity partido = partidoRepository.findById(ID_PARTIDO_2).orElseThrow();
        JugadorJPAEntity jugador = jugadorRepository.findById(ID_JUGADOR_1).orElseThrow();
        EquipoJPAEntity equipo = equipoRepository.findById(ID_EQUIPO_1).orElseThrow();

        EventosPartidoJPAEntity nuevo = EventosPartidoJPAEntity.builder()
                .idEvento(nuevoId)
                .partido(partido)
                .personal(jugador)
                .equipoFavorecido(equipo)
                .tipoEvento(TipoEvento.ASISTENCIA)
                .estadoEvento(EstadoPartido.PRIMER_TIEMPO)
                .minuto(minuto(30))
                .descripcion("Asistencia de Saka")
                .build();

        EventosPartidoJPAEntity guardado = repository.save(nuevo);
        repository.flush();

        assertThat(guardado.getIdEvento()).isEqualTo(nuevoId);
        assertThat(repository.findById(nuevoId)).isPresent();
        assertThat(repository.count()).isEqualTo(8);
    }

    @Test
    @DisplayName("deleteById: elimina un evento")
    void deleteById() {
        assertThat(repository.existsById(ID_EVENTO_GOL_1)).isTrue();

        repository.deleteById(ID_EVENTO_GOL_1);
        repository.flush();

        assertThat(repository.existsById(ID_EVENTO_GOL_1)).isFalse();
    }
}