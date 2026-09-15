package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.enums.*;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class PartidoJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    private PartidoJPARepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EquipoJPARepository equipoRepository;

    @Autowired
    private EstadioJPARepository estadioRepository;

    @Autowired
    private ArbitroJPARepository arbitroRepository;

    @Autowired
    private CompeticionJPARepository competicionRepository;


    private static final UUID ID_EQUIPO_1   = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_EQUIPO_2   = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_EQUIPO_3   = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID ID_ESTADIO_1  = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID ID_ARBITRO_1  = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID ID_COMPETICION_1 = UUID.fromString("66666666-6666-6666-6666-666666666666");

    private static final UUID ID_PARTIDO_1 = UUID.fromString("77777777-7777-7777-7777-777777777777");
    private static final UUID ID_PARTIDO_2 = UUID.fromString("88888888-8888-8888-8888-888888888888");
    private static final UUID ID_PARTIDO_3 = UUID.fromString("99999999-9999-9999-9999-999999999999");
    private static final UUID ID_PARTIDO_4 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private static final LocalDateTime NOW =
            LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        equipoRepository.deleteAll();
        estadioRepository.deleteAll();
        arbitroRepository.deleteAll();
        competicionRepository.deleteAll();
        repository.flush();
        equipoRepository.flush();
        estadioRepository.flush();
        arbitroRepository.flush();
        competicionRepository.flush();

        EquipoJPAEntity equipo1 = EquipoJPAEntity.builder()
                .idEquipo(ID_EQUIPO_1)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .paisEquipo(Nacion.INGLATERRA)
                .tipo(TipoEquipo.CLUB_PROFESIONAL)
                .build();

        EquipoJPAEntity equipo2 = EquipoJPAEntity.builder()
                .idEquipo(ID_EQUIPO_2)
                .nombre("Manchester City")
                .nombreCorto("MCI")
                .paisEquipo(Nacion.INGLATERRA)
                .tipo(TipoEquipo.CLUB_PROFESIONAL)
                .build();

        EquipoJPAEntity equipo3 = EquipoJPAEntity.builder()
                .idEquipo(ID_EQUIPO_3)
                .nombre("Chelsea FC")
                .nombreCorto("CHE")
                .paisEquipo(Nacion.INGLATERRA)
                .tipo(TipoEquipo.CLUB_PROFESIONAL)
                .build();

        equipoRepository.saveAll(List.of(equipo1, equipo2, equipo3));

        EstadioJPAEntity estadio = EstadioJPAEntity.builder()
                .idEstadio(ID_ESTADIO_1)
                .nombre("Emirates Stadium")
                .direccion("Highbury, Londres")
                .capacidad(60704)
                .fechaFundacion(LocalDate.of(2006, 7, 22))
                .build();
        estadioRepository.save(estadio);

        ArbitroJPAEntity arbitro = ArbitroJPAEntity.builder()
                .idArbitro(ID_ARBITRO_1)
                .nombre("Michael")
                .apellido("Oliver")
                .fechaNacimiento(LocalDate.of(1985, 2, 20))
                .build();
        arbitroRepository.save(arbitro);

        CompeticionJPAEntity competicion = CompeticionJPAEntity.builder()
                .idCompeticion(ID_COMPETICION_1)
                .nombre("Premier League")
                .temporada(Temporada.T2024_25)
                .estado(EstadoCompeticion.EN_CURSO)
                .fechaInicio(NOW.minusMonths(2))
                .fechaFin(NOW.plusMonths(8))
                .build();
        competicionRepository.save(competicion);


        PartidoJPAEntity partido1 = PartidoJPAEntity.builder()
                .idPartido(ID_PARTIDO_1)
                .fechaYHora(NOW.minusDays(10))
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(3)
                .golesVisitante(1)
                .fase(FaseTorneo.LIGA)
                .jornadaTorneo(JornadaPartido.JORNADA_1)
                .equipoLocal(equipo1)
                .equipoVisitante(equipo2)
                .estadio(estadio)
                .arbitro(arbitro)
                .competicion(competicion)
                .alineacionLocal(Alineacion.ALINEACION_433)
                .alineacionVisitante(Alineacion.ALINEACION_4231)
                .build();

        PartidoJPAEntity partido2 = PartidoJPAEntity.builder()
                .idPartido(ID_PARTIDO_2)
                .fechaYHora(NOW.plusDays(5))
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .fase(FaseTorneo.LIGA)
                .jornadaTorneo(JornadaPartido.JORNADA_2)
                .equipoLocal(equipo3)
                .equipoVisitante(equipo1)
                .estadio(estadio)
                .arbitro(arbitro)
                .competicion(competicion)
                .build();

        PartidoJPAEntity partido3 = PartidoJPAEntity.builder()
                .idPartido(ID_PARTIDO_3)
                .fechaYHora(NOW.minusMinutes(30))
                .estado(EstadoPartido.PRIMER_TIEMPO)
                .golesLocal(1)
                .golesVisitante(0)
                .fase(FaseTorneo.LIGA)
                .jornadaTorneo(JornadaPartido.JORNADA_3)
                .equipoLocal(equipo2)
                .equipoVisitante(equipo3)
                .estadio(estadio)
                .arbitro(arbitro)
                .competicion(competicion)
                .build();

        PartidoJPAEntity partido4 = PartidoJPAEntity.builder()
                .idPartido(ID_PARTIDO_4)
                .fechaYHora(NOW.minusDays(2))
                .estado(EstadoPartido.CANCELADO)
                .golesLocal(0)
                .golesVisitante(0)
                .fase(FaseTorneo.LIGA)
                .jornadaTorneo(JornadaPartido.JORNADA_4)
                .equipoLocal(equipo1)
                .equipoVisitante(equipo3)
                .estadio(estadio)
                .arbitro(arbitro)
                .competicion(competicion)
                .build();

        repository.saveAll(List.of(partido1, partido2, partido3, partido4));
        repository.flush();
    }


    @Test
    @DisplayName("findById: debe encontrar un partido por ID")
    void testFindById() {
        Optional<PartidoJPAEntity> partido = repository.findById(ID_PARTIDO_1);

        assertThat(partido).isPresent();
        assertThat(partido.get().getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
        assertThat(partido.get().getGolesLocal()).isEqualTo(3);
        assertThat(partido.get().getGolesVisitante()).isEqualTo(1);
        assertThat(partido.get().getEquipoLocal().getIdEquipo()).isEqualTo(ID_EQUIPO_1);
        assertThat(partido.get().getEquipoVisitante().getIdEquipo()).isEqualTo(ID_EQUIPO_2);
    }

    @Test
    @DisplayName("findById: debe devolver vacío si no existe")
    void testFindByIdNoExiste() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }


    @Test
    @DisplayName("findAll: debe retornar los 4 partidos del setup")
    void testFindAll() {
        List<PartidoJPAEntity> partidos = repository.findAll();
        assertThat(partidos).hasSize(4);
    }


    @Test
    @DisplayName("findByEquipo: debe encontrar partidos donde el equipo es local o visitante")
    void testFindByEquipo() {
        List<PartidoJPAEntity> partidosEquipo1 = repository.findByEquipo(ID_EQUIPO_1);
        assertThat(partidosEquipo1).hasSize(3);
        assertThat(partidosEquipo1)
                .extracting(PartidoJPAEntity::getIdPartido)
                .containsExactlyInAnyOrder(ID_PARTIDO_1, ID_PARTIDO_2, ID_PARTIDO_4);

        List<PartidoJPAEntity> partidosEquipo2 = repository.findByEquipo(ID_EQUIPO_2);
        assertThat(partidosEquipo2).hasSize(2);

        List<PartidoJPAEntity> partidosEquipo3 = repository.findByEquipo(ID_EQUIPO_3);
        assertThat(partidosEquipo3).hasSize(3);

        assertThat(repository.findByEquipo(UUID.randomUUID())).isEmpty();
    }


    @Test
    @DisplayName("findByCompeticionIdCompeticion: debe encontrar partidos de una competición")
    void testFindByCompeticion() {
        List<PartidoJPAEntity> partidos = repository.findByCompeticionIdCompeticion(ID_COMPETICION_1);
        assertThat(partidos).hasSize(4);

        assertThat(repository.findByCompeticionIdCompeticion(UUID.randomUUID())).isEmpty();
    }


    @Test
    @DisplayName("findClasificacion: debe excluir partidos CANCELADOS y SUSPENDIDOS")
    void testFindClasificacion() {
        List<PartidoJPAEntity> clasificacion = repository.findClasificacion(ID_COMPETICION_1);

        assertThat(clasificacion).hasSize(3);
        assertThat(clasificacion)
                .extracting(PartidoJPAEntity::getIdPartido)
                .containsExactlyInAnyOrder(ID_PARTIDO_1, ID_PARTIDO_2, ID_PARTIDO_3)
                .doesNotContain(ID_PARTIDO_4);
    }


    @Test
    @DisplayName("updateGoles: debe actualizar los goles de un partido")
    void testUpdateGoles() {
        repository.updateGoles(ID_PARTIDO_2, 2, 1);
        entityManager.flush();
        entityManager.clear();


        PartidoJPAEntity actualizado = repository.findById(ID_PARTIDO_2).orElseThrow();
        assertThat(actualizado.getGolesLocal()).isEqualTo(2);
        assertThat(actualizado.getGolesVisitante()).isEqualTo(1);
    }


    @Test
    @DisplayName("findByFecha: debe encontrar partidos de un día concreto")
    void testFindByFecha() {
        LocalDate fechaPartido1 = NOW.minusDays(10).toLocalDate();
        Pageable pageable = PageRequest.of(0, 10);

        Page<PartidoJPAEntity> page = repository.findByFecha(fechaPartido1, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getIdPartido()).isEqualTo(ID_PARTIDO_1);
    }

    @Test
    @DisplayName("findByFecha: debe retornar página vacía si no hay partidos ese día")
    void testFindByFecha_SinPartidos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PartidoJPAEntity> page = repository.findByFecha(LocalDate.of(1900, 1, 1), pageable);

        assertThat(page).isEmpty();
    }


    @Test
    @DisplayName("deleteById: debe eliminar un partido")
    void testDeleteById() {
        assertThat(repository.existsById(ID_PARTIDO_4)).isTrue();

        repository.deleteById(ID_PARTIDO_4);
        repository.flush();

        assertThat(repository.existsById(ID_PARTIDO_4)).isFalse();
        assertThat(repository.findAll()).hasSize(3);
    }


    @Test
    @DisplayName("save: debe guardar un nuevo partido")
    void testSave() {
        UUID nuevoId = UUID.randomUUID();

        EquipoJPAEntity local = equipoRepository.findById(ID_EQUIPO_2).orElseThrow();
        EquipoJPAEntity visitante = equipoRepository.findById(ID_EQUIPO_1).orElseThrow();
        CompeticionJPAEntity competicion =
                competicionRepository.findById(ID_COMPETICION_1).orElseThrow();

        PartidoJPAEntity nuevo = PartidoJPAEntity.builder()
                .idPartido(nuevoId)
                .fechaYHora(NOW.plusDays(20))
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .fase(FaseTorneo.LIGA)
                .jornadaTorneo(JornadaPartido.JORNADA_5)
                .equipoLocal(local)
                .equipoVisitante(visitante)
                .competicion(competicion)
                .build();

        PartidoJPAEntity guardado = repository.save(nuevo);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdPartido()).isEqualTo(nuevoId);
        assertThat(guardado.getEstado()).isEqualTo(EstadoPartido.PROGRAMADO);
        assertThat(guardado.getEquipoLocal().getIdEquipo()).isEqualTo(ID_EQUIPO_2);
        assertThat(guardado.getEquipoVisitante().getIdEquipo()).isEqualTo(ID_EQUIPO_1);

        assertThat(repository.findById(nuevoId)).isPresent();
        assertThat(repository.findAll()).hasSize(5);
    }
}