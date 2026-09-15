package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class ContratoJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    private ContratoJPARepository contratoRepository;

    @Autowired
    private JugadorJPARepository jugadorRepository;

    @Autowired
    private EquipoJPARepository clubRepository;

    @Autowired
    private EstadioJPARepository estadioRepository;

    private static final UUID ID_JUGADOR_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_JUGADOR_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_CLUB_1    = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID ID_CLUB_2    = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID ID_ESTADIO_1 = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID ID_ESTADIO_2 = UUID.fromString("66666666-6666-6666-6666-666666666666");

    private static final UUID ID_CONTRATO_ACTIVO     = UUID.fromString("77777777-7777-7777-7777-777777777777");
    private static final UUID ID_CONTRATO_FINALIZADO = UUID.fromString("88888888-8888-8888-8888-888888888888");
    private static final UUID ID_CONTRATO_FUTURO     = UUID.fromString("99999999-9999-9999-9999-999999999999");

    private static final LocalDateTime NOW =
            LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    private UUID idContratoActivo;
    private UUID idContratoFinalizado;
    private UUID idContratoFuturo;

    @BeforeEach
    void setUp() {
        // Orden correcto por FKs: contratos -> jugadores -> equipos -> estadios
        contratoRepository.deleteAll();
        jugadorRepository.deleteAll();
        clubRepository.deleteAll();
        estadioRepository.deleteAll();

        // 1. Estadios
        EstadioJPAEntity estadio1 = EstadioJPAEntity.builder()
                .idEstadio(ID_ESTADIO_1)
                .nombre("Estadio 1")
                .direccion("Direccion 1")
                .capacidad(50000)
                .fechaFundacion(LocalDate.of(2000, 1, 1))
                .build();

        EstadioJPAEntity estadio2 = EstadioJPAEntity.builder()
                .idEstadio(ID_ESTADIO_2)
                .nombre("Estadio 2")
                .direccion("Direccion 2")
                .capacidad(60000)
                .fechaFundacion(LocalDate.of(2001, 1, 1))
                .build();

        estadioRepository.saveAll(List.of(estadio1, estadio2));

        // 2. Clubes
        EquipoJPAEntity club1 = EquipoJPAEntity.builder()
                .idEquipo(ID_CLUB_1)
                .nombre("Club 1")
                .nombreCorto("CL1")
                .fechaFundacion(LocalDate.of(1990, 1, 1))
                .estadio(estadio1)
                .build();

        EquipoJPAEntity club2 = EquipoJPAEntity.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("Club 2")
                .nombreCorto("CL2")
                .fechaFundacion(LocalDate.of(1991, 1, 1))
                .estadio(estadio2)
                .build();

        clubRepository.saveAll(List.of(club1, club2));

        // 3. Jugadores
        JugadorJPAEntity jugador1 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_1)
                .nombre("Jugador")
                .apellido("Uno")
                .fechaNacimiento(LocalDate.of(1995, 1, 1))
                .nacionalidad(Nacion.ESPAÑA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .fechaActualizacion(LocalDate.now())
                .build();

        JugadorJPAEntity jugador2 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_2)
                .nombre("Jugador")
                .apellido("Dos")
                .fechaNacimiento(LocalDate.of(1996, 1, 1))
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(175)
                .peso(70)
                .fechaActualizacion(LocalDate.now())
                .build();

        jugadorRepository.saveAll(List.of(jugador1, jugador2));

        // 4. Contratos
        ContratoJPAEntity contratoActivo = ContratoJPAEntity.builder()
                .idContrato(ID_CONTRATO_ACTIVO)
                .personal(jugador1)
                .equipo(club1)
                .fechaInicio(NOW.minusMonths(6))
                .fechaFin(NOW.plusMonths(6))
                .sueldo(100000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();

        ContratoJPAEntity contratoFinalizado = ContratoJPAEntity.builder()
                .idContrato(ID_CONTRATO_FINALIZADO)
                .personal(jugador1)
                .equipo(club2)
                .fechaInicio(NOW.minusMonths(12))
                .fechaFin(NOW.minusMonths(1))
                .sueldo(80000.0)
                .estado(EstadoContrato.FINALIZADO)
                .build();

        ContratoJPAEntity contratoFuturo = ContratoJPAEntity.builder()
                .idContrato(ID_CONTRATO_FUTURO)
                .personal(jugador2)
                .equipo(club1)
                .fechaInicio(NOW.plusMonths(1))
                .fechaFin(NOW.plusMonths(13))
                .sueldo(120000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();

        contratoRepository.saveAll(List.of(contratoActivo, contratoFinalizado, contratoFuturo));
        contratoRepository.flush();

        this.idContratoActivo     = ID_CONTRATO_ACTIVO;
        this.idContratoFinalizado = ID_CONTRATO_FINALIZADO;
        this.idContratoFuturo     = ID_CONTRATO_FUTURO;
    }

    @Test
    @DisplayName("findById: debe encontrar el contrato activo")
    void testFindById() {
        Optional<ContratoJPAEntity> contrato = contratoRepository.findById(idContratoActivo);
        assertThat(contrato).isPresent();
        assertThat(contrato.get().getEstado()).isEqualTo(EstadoContrato.ACTIVO);
        assertThat(contrato.get().getSueldo()).isEqualTo(100000.0);
    }

    @Test
    @DisplayName("findByIdWithRelations: debe traer personal y equipo en una sola query")
    void testFindByIdWithRelations() {
        Optional<ContratoJPAEntity> contrato =
                contratoRepository.findByIdWithRelations(idContratoActivo);

        assertThat(contrato).isPresent();
        ContratoJPAEntity c = contrato.get();

        // Al estar @Transactional, la sesión sigue abierta y el LAZY se resuelve,
        // pero aquí verificamos que los datos son correctos.
        assertThat(c.getPersonal().getIdPersonal()).isEqualTo(ID_JUGADOR_1);
        assertThat(c.getEquipo().getIdEquipo()).isEqualTo(ID_CLUB_1);
    }

    @Test
    @DisplayName("findAll: debe retornar los 3 contratos del setup")
    void testFindAll() {
        List<ContratoJPAEntity> todos = contratoRepository.findAll();
        assertThat(todos).hasSize(3);
    }

    @Test
    @DisplayName("findByPersonalIdPersonal: debe encontrar los contratos de un jugador")
    void testFindByPersonalIdPersonal() {
        List<ContratoJPAEntity> contratos = contratoRepository.findByPersonalIdPersonal(ID_JUGADOR_1);
        assertThat(contratos).hasSize(2);
        assertThat(contratos)
                .extracting(ContratoJPAEntity::getEstado)
                .containsExactlyInAnyOrder(EstadoContrato.ACTIVO, EstadoContrato.FINALIZADO);
    }

    @Test
    @DisplayName("findByEquipoIdEquipo: debe encontrar los contratos de un club")
    void testFindByEquipoIdEquipo() {
        List<ContratoJPAEntity> contratos = contratoRepository.findByEquipoIdEquipo(ID_CLUB_1);
        assertThat(contratos).hasSize(2);
        assertThat(contratos)
                .extracting(c -> c.getPersonal().getIdPersonal())
                .containsExactlyInAnyOrder(ID_JUGADOR_1, ID_JUGADOR_2);
    }

    @Test
    @DisplayName("findVigenteByPersonal: debe encontrar el contrato vigente de un jugador")
    void testFindVigenteByPersonal() {
        Optional<ContratoJPAEntity> contratoVigente =
                contratoRepository.findVigenteByPersonal(ID_JUGADOR_1);

        assertThat(contratoVigente).isPresent();
        assertThat(contratoVigente.get().getIdContrato()).isEqualTo(ID_CONTRATO_ACTIVO);
        assertThat(contratoVigente.get().getEstado()).isEqualTo(EstadoContrato.ACTIVO);

        // Jugador 2 tiene un contrato futuro (no vigente aún)
        Optional<ContratoJPAEntity> contratoVigente2 =
                contratoRepository.findVigenteByPersonal(ID_JUGADOR_2);
        assertThat(contratoVigente2).isEmpty();
    }

    @Test
    @DisplayName("findVigentesByEquipo: debe encontrar los contratos vigentes de un club")
    void testFindVigentesByEquipo() {
        List<ContratoJPAEntity> vigentesClub1 = contratoRepository.findVigentesByEquipo(ID_CLUB_1);
        assertThat(vigentesClub1).hasSize(1);
        assertThat(vigentesClub1.get(0).getIdContrato()).isEqualTo(ID_CONTRATO_ACTIVO);

        List<ContratoJPAEntity> vigentesClub2 = contratoRepository.findVigentesByEquipo(ID_CLUB_2);
        assertThat(vigentesClub2).isEmpty();
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia de un contrato")
    void testExistsById() {
        assertThat(contratoRepository.existsById(idContratoActivo)).isTrue();
        assertThat(contratoRepository.existsById(idContratoFinalizado)).isTrue();
        assertThat(contratoRepository.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteContratoById: debe eliminar un contrato")
    void testDeleteById() {
        assertThat(contratoRepository.existsById(idContratoFinalizado)).isTrue();

        contratoRepository.deleteContratoById(idContratoFinalizado);

        assertThat(contratoRepository.existsById(idContratoFinalizado)).isFalse();

        List<ContratoJPAEntity> todos = contratoRepository.findAll();
        assertThat(todos).hasSize(2);
        assertThat(todos)
                .extracting(ContratoJPAEntity::getIdContrato)
                .containsExactlyInAnyOrder(idContratoActivo, idContratoFuturo);
    }

    @Test
    @DisplayName("save: debe guardar un nuevo contrato")
    void testSave() {
        UUID nuevoId = UUID.randomUUID();

        ContratoJPAEntity nuevoContrato = ContratoJPAEntity.builder()
                .idContrato(nuevoId)
                .personal(jugadorRepository.findById(ID_JUGADOR_2).orElseThrow())
                .equipo(clubRepository.findById(ID_CLUB_2).orElseThrow())
                .fechaInicio(NOW)
                .fechaFin(NOW.plusYears(1))
                .sueldo(150000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();

        ContratoJPAEntity guardado = contratoRepository.save(nuevoContrato);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdContrato()).isEqualTo(nuevoId);
        assertThat(guardado.getSueldo()).isEqualTo(150000.0);

        Optional<ContratoJPAEntity> encontrado = contratoRepository.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getSueldo()).isEqualTo(150000.0);

        assertThat(contratoRepository.findAll()).hasSize(4);
    }
}