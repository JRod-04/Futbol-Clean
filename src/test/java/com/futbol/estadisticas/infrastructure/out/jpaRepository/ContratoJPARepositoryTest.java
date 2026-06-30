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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ContratoJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ContratoJPARepository contratoRepository;

    @Autowired
    private JugadorJPARepository jugadorRepository;

    @Autowired
    private ClubJPARepository clubRepository;

    @Autowired
    private EstadioJPARepository estadioRepository;

    // IDs fijos para las pruebas
    private static final UUID ID_JUGADOR_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_JUGADOR_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_CLUB_1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID ID_CLUB_2 = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID ID_ESTADIO_1 = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID ID_ESTADIO_2 = UUID.fromString("66666666-6666-6666-6666-666666666666");

    private static final UUID ID_CONTRATO_ACTIVO = UUID.fromString("77777777-7777-7777-7777-777777777777");
    private static final UUID ID_CONTRATO_FINALIZADO = UUID.fromString("88888888-8888-8888-8888-888888888888");
    private static final UUID ID_CONTRATO_FUTURO = UUID.fromString("99999999-9999-9999-9999-999999999999");

    private UUID idJugador1;
    private UUID idJugador2;
    private UUID idClub1;
    private UUID idClub2;
    private UUID idContratoActivo;
    private UUID idContratoFinalizado;
    private UUID idContratoFuturo;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada prueba
        contratoRepository.deleteAll();
        jugadorRepository.deleteAll();
        clubRepository.deleteAll();
        estadioRepository.deleteAll();

        // 1. Crear estadios
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

        // 2. Crear clubes
        ClubJPAEntity club1 = ClubJPAEntity.builder()
                .idEquipo(ID_CLUB_1)
                .nombre("Club 1")
                .nombreCorto("CL1")
                .fechaFundacion(LocalDate.of(1990, 1, 1))
                .estadio(estadio1)
                .build();

        ClubJPAEntity club2 = ClubJPAEntity.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("Club 2")
                .nombreCorto("CL2")
                .fechaFundacion(LocalDate.of(1991, 1, 1))
                .estadio(estadio2)
                .build();

        clubRepository.saveAll(List.of(club1, club2));

        // 3. Crear jugadores
        JugadorJPAEntity jugador1 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_1)
                .nombre("Jugador")
                .apellido("Uno")
                .fechaNacimiento(LocalDate.of(1995, 1, 1))
                .nacionalidad(Nacion.ESPANA)
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

        // 4. Crear contratos
        // Contrato activo (vigente)
        ContratoJPAEntity contratoActivo = ContratoJPAEntity.builder()
                .idContrato(ID_CONTRATO_ACTIVO)
                .personal(jugador1)
                .club(club1)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .sueldo(100000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();

        // Contrato finalizado (no vigente)
        ContratoJPAEntity contratoFinalizado = ContratoJPAEntity.builder()
                .idContrato(ID_CONTRATO_FINALIZADO)
                .personal(jugador1)
                .club(club2)
                .fechaInicio(LocalDateTime.now().minusMonths(12))
                .fechaFin(LocalDateTime.now().minusMonths(1))
                .sueldo(80000.0)
                .estado(EstadoContrato.FINALIZADO)
                .build();

        // Contrato futuro (aún no vigente)
        ContratoJPAEntity contratoFuturo = ContratoJPAEntity.builder()
                .idContrato(ID_CONTRATO_FUTURO)
                .personal(jugador2)
                .club(club1)
                .fechaInicio(LocalDateTime.now().plusMonths(1))
                .fechaFin(LocalDateTime.now().plusMonths(13))
                .sueldo(120000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();

        contratoRepository.saveAll(List.of(contratoActivo, contratoFinalizado, contratoFuturo));

        // Guardar IDs para los tests
        this.idJugador1 = ID_JUGADOR_1;
        this.idJugador2 = ID_JUGADOR_2;
        this.idClub1 = ID_CLUB_1;
        this.idClub2 = ID_CLUB_2;
        this.idContratoActivo = ID_CONTRATO_ACTIVO;
        this.idContratoFinalizado = ID_CONTRATO_FINALIZADO;
        this.idContratoFuturo = ID_CONTRATO_FUTURO;
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
    @DisplayName("findAll: debe retornar los 3 contratos del setup")
    void testFindAll() {
        List<ContratoJPAEntity> todos = contratoRepository.findAll();
        assertThat(todos).hasSize(3);
    }

    @Test
    @DisplayName("findByPersonalIdPersonal: debe encontrar los contratos de un jugador")
    void testFindByPersonalIdPersonal() {
        List<ContratoJPAEntity> contratos = contratoRepository.findByPersonalIdPersonal(idJugador1);
        assertThat(contratos).hasSize(2);
        assertThat(contratos)
                .extracting(ContratoJPAEntity::getEstado)
                .containsExactlyInAnyOrder(EstadoContrato.ACTIVO, EstadoContrato.FINALIZADO);
    }

    @Test
    @DisplayName("findByClubIdEquipo: debe encontrar los contratos de un club")
    void testFindByClubIdEquipo() {
        List<ContratoJPAEntity> contratos = contratoRepository.findByClubIdEquipo(idClub1);
        assertThat(contratos).hasSize(2);
        assertThat(contratos)
                .extracting(ContratoJPAEntity::getPersonal)
                .extracting(PersonalDeportivoJPAEntity::getIdPersonal)
                .containsExactlyInAnyOrder(idJugador1, idJugador2);
    }

    @Test
    @DisplayName("findVigenteByPersonal: debe encontrar el contrato vigente de un jugador")
    void testFindVigenteByPersonal() {
        // Jugador 1 tiene un contrato activo vigente
        Optional<ContratoJPAEntity> contratoVigente = contratoRepository.findVigenteByPersonal(idJugador1);
        assertThat(contratoVigente).isPresent();
        assertThat(contratoVigente.get().getIdContrato()).isEqualTo(idContratoActivo);
        assertThat(contratoVigente.get().getEstado()).isEqualTo(EstadoContrato.ACTIVO);

        // Jugador 2 tiene un contrato futuro (no vigente aún)
        Optional<ContratoJPAEntity> contratoVigente2 = contratoRepository.findVigenteByPersonal(idJugador2);
        assertThat(contratoVigente2).isEmpty();
    }

    @Test
    @DisplayName("findVigentesByClub: debe encontrar los contratos vigentes de un club")
    void testFindVigentesByClub() {
        // Club 1 tiene 1 contrato vigente (Jugador 1)
        List<ContratoJPAEntity> contratosVigentes = contratoRepository.findVigentesByClub(idClub1);
        assertThat(contratosVigentes).hasSize(1);
        assertThat(contratosVigentes.get(0).getIdContrato()).isEqualTo(idContratoActivo);

        // Club 2 tiene 0 contratos vigentes
        List<ContratoJPAEntity> contratosVigentes2 = contratoRepository.findVigentesByClub(idClub2);
        assertThat(contratosVigentes2).isEmpty();
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia de un contrato")
    void testExistsById() {
        assertThat(contratoRepository.existsById(idContratoActivo)).isTrue();
        assertThat(contratoRepository.existsById(idContratoFinalizado)).isTrue();
        assertThat(contratoRepository.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar un contrato")
    void testDeleteById() {
        // Verificar que existe
        assertThat(contratoRepository.existsById(idContratoFinalizado)).isTrue();

        // Eliminar
        contratoRepository.deleteById(idContratoFinalizado);

        // Verificar que ya no existe
        assertThat(contratoRepository.existsById(idContratoFinalizado)).isFalse();

        // Verificar que quedan 2 contratos
        List<ContratoJPAEntity> todos = contratoRepository.findAll();
        assertThat(todos).hasSize(2);
        assertThat(todos)
                .extracting(ContratoJPAEntity::getIdContrato)
                .containsExactlyInAnyOrder(idContratoActivo, idContratoFuturo);
    }

    @Test
    @DisplayName("save: debe guardar un nuevo contrato")
    void testSave() {
        // Crear un nuevo contrato
        UUID nuevoId = UUID.randomUUID();
        ContratoJPAEntity nuevoContrato = ContratoJPAEntity.builder()
                .idContrato(nuevoId)
                .personal(jugadorRepository.findById(idJugador2).orElseThrow())
                .club(clubRepository.findById(idClub2).orElseThrow())
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusYears(1))
                .sueldo(150000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();

        // Guardar
        ContratoJPAEntity guardado = contratoRepository.save(nuevoContrato);

        // Verificar
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdContrato()).isEqualTo(nuevoId);
        assertThat(guardado.getSueldo()).isEqualTo(150000.0);

        // Verificar que se encuentra en la BD
        Optional<ContratoJPAEntity> encontrado = contratoRepository.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getSueldo()).isEqualTo(150000.0);

        // Verificar el total
        List<ContratoJPAEntity> todos = contratoRepository.findAll();
        assertThat(todos).hasSize(4);
    }
}