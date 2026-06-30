package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.PartidoRepositoryAdapter;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ArbitroJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.CompeticionJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PartidoJPAEntity;
import jakarta.transaction.Transactional;
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
@Transactional
class PartidoJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private PartidoRepositoryAdapter adapter;

    @Autowired
    private PartidoJPARepository repository;

    @Autowired
    private ClubJPARepository clubRepository;

    @Autowired
    private EstadioJPARepository estadioRepository;

    @Autowired
    private CompeticionJPARepository competicionRepository;

    @Autowired
    private ArbitroJPARepository arbitroRepository;

    private static final UUID ID_PARTIDO_1 = UUID.fromString("cccccccc-dddd-eeee-ffff-000000000000");
    private static final UUID ID_PARTIDO_2 = UUID.fromString("dddddddd-eeee-ffff-0000-111111111111");
    private static final UUID ID_PARTIDO_3 = UUID.fromString("eeeeeeee-ffff-0000-1111-222222222222");
    private static final UUID ID_CLUB_1 = UUID.fromString("44444444-5555-6666-7777-888888888888");
    private static final UUID ID_CLUB_2 = UUID.fromString("55555555-6666-7777-8888-999999999999");
    private static final UUID ID_CLUB_3 = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa");
    private static final UUID ID_ESTADIO_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_ESTADIO_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_ESTADIO_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID ID_COMPETICION = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID ID_ARBITRO_1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ID_ARBITRO_2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    private LocalDateTime ahora;

    @BeforeEach
    void setUp() {
        ahora = LocalDateTime.now();
        
        // Limpiar en orden inverso
        repository.deleteAll();
        clubRepository.deleteAll();
        estadioRepository.deleteAll();
        competicionRepository.deleteAll();
        arbitroRepository.deleteAll();

        // Estadios - Cada club tiene su propio estadio
        EstadioJPAEntity estadio1 = EstadioJPAEntity.builder()
                .idEstadio(ID_ESTADIO_1)
                .nombre("Emirates Stadium")
                .direccion("Highbury, Londres")
                .capacidad(60704)
                .fechaFundacion(LocalDate.of(2006, 7, 22))
                .build();

        EstadioJPAEntity estadio2 = EstadioJPAEntity.builder()
                .idEstadio(ID_ESTADIO_2)
                .nombre("Etihad Stadium")
                .direccion("Ashton, Mánchester")
                .capacidad(53400)
                .fechaFundacion(LocalDate.of(2003, 8, 10))
                .build();

        EstadioJPAEntity estadio3 = EstadioJPAEntity.builder()
                .idEstadio(ID_ESTADIO_3)
                .nombre("Stamford Bridge")
                .direccion("Fulham, Londres")
                .capacidad(40834)
                .fechaFundacion(LocalDate.of(1877, 4, 28))
                .build();

        estadioRepository.saveAll(List.of(estadio1, estadio2, estadio3));

        // Competicion
        CompeticionJPAEntity competicion = CompeticionJPAEntity.builder()
                .idCompeticion(ID_COMPETICION)
                .nombre("Premier League")
                .fechaInicio(ahora.minusMonths(2))
                .fechaFin(ahora.plusMonths(6))
                .build();
        competicionRepository.save(competicion);

        // Arbitros
        ArbitroJPAEntity arbitro1 = ArbitroJPAEntity.builder()
                .idArbitro(ID_ARBITRO_1)
                .nombre("Michael")
                .apellido("Oliver")
                .fechaNacimiento(LocalDate.of(1985, 2, 20))
                .build();

        ArbitroJPAEntity arbitro2 = ArbitroJPAEntity.builder()
                .idArbitro(ID_ARBITRO_2)
                .nombre("Anthony")
                .apellido("Taylor")
                .fechaNacimiento(LocalDate.of(1978, 10, 20))
                .build();

        arbitroRepository.saveAll(List.of(arbitro1, arbitro2));

        // Clubes - Cada uno con su propio estadio
        ClubJPAEntity club1 = ClubJPAEntity.builder()
                .idEquipo(ID_CLUB_1)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .fechaFundacion(LocalDate.of(1886, 12, 1))
                .estadio(estadio1)  // Estadio 1
                .build();

        ClubJPAEntity club2 = ClubJPAEntity.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("Manchester City")
                .nombreCorto("MCI")
                .fechaFundacion(LocalDate.of(1880, 11, 1))
                .estadio(estadio2)  // Estadio 2
                .build();

        ClubJPAEntity club3 = ClubJPAEntity.builder()
                .idEquipo(ID_CLUB_3)
                .nombre("Chelsea FC")
                .nombreCorto("CHE")
                .fechaFundacion(LocalDate.of(1905, 3, 10))
                .estadio(estadio3)  // Estadio 3
                .build();

        clubRepository.saveAll(List.of(club1, club2, club3));

        // Partidos con fechas relativas
        // Partido 1: Finalizado (hace 2 meses) - Arsenal vs Manchester City en Emirates
        PartidoJPAEntity partido1 = PartidoJPAEntity.builder()
                .idPartido(ID_PARTIDO_1)
                .equipoLocal(club1)
                .equipoVisitante(club2)
                .fechaYHora(ahora.minusMonths(2))
                .jornada(5)
                .competicion(competicion)
                .arbitro(arbitro1)
                .estadio(estadio1)
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(2)
                .golesVisitante(1)
                .build();

        // Partido 2: Programado (en 2 semanas) - Chelsea vs Arsenal en Stamford Bridge
        PartidoJPAEntity partido2 = PartidoJPAEntity.builder()
                .idPartido(ID_PARTIDO_2)
                .equipoLocal(club3)
                .equipoVisitante(club1)
                .fechaYHora(ahora.plusWeeks(2))
                .jornada(8)
                .competicion(competicion)
                .arbitro(arbitro2)
                .estadio(estadio3)
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .build();

        // Partido 3: Programado (en 2 meses) - Manchester City vs Chelsea en Etihad
        PartidoJPAEntity partido3 = PartidoJPAEntity.builder()
                .idPartido(ID_PARTIDO_3)
                .equipoLocal(club2)
                .equipoVisitante(club3)
                .fechaYHora(ahora.plusMonths(2))
                .jornada(12)
                .competicion(competicion)
                .arbitro(arbitro1)
                .estadio(estadio2)
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .build();

        repository.saveAll(List.of(partido1, partido2, partido3));
    }

    @Test
    @DisplayName("findById: debe encontrar el partido por ID")
    void testFindById() {
        Optional<Partido> partido = adapter.findById(ID_PARTIDO_1);
        assertThat(partido).isPresent();
        assertThat(partido.get().getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
        assertThat(partido.get().getGolesLocal()).isEqualTo(2);
        assertThat(partido.get().getGolesVisitante()).isEqualTo(1);
        assertThat(partido.get().getJornada()).isEqualTo(5);
    }

    @Test
    @DisplayName("findAll: debe retornar todos los partidos")
    void testFindAll() {
        List<Partido> todos = adapter.findAll();
        assertThat(todos).hasSize(3);
        assertThat(todos)
                .extracting(Partido::getEstado)
                .containsExactlyInAnyOrder(
                        EstadoPartido.FINALIZADO,
                        EstadoPartido.PROGRAMADO,
                        EstadoPartido.PROGRAMADO
                );
    }

    @Test
    @DisplayName("findByClub: debe buscar partidos por club")
    void testFindByClub() {
        // Arsenal (club1) juega en partido1 y partido2
        List<Partido> partidos = adapter.findByClub(ID_CLUB_1);
        assertThat(partidos).hasSize(2);
        assertThat(partidos)
                .extracting(Partido::getEstado)
                .containsExactlyInAnyOrder(EstadoPartido.FINALIZADO, EstadoPartido.PROGRAMADO);
        
        // Chelsea (club3) juega en partido2 y partido3
        List<Partido> partidosChelsea = adapter.findByClub(ID_CLUB_3);
        assertThat(partidosChelsea).hasSize(2);
        assertThat(partidosChelsea)
                .extracting(Partido::getEstado)
                .containsExactlyInAnyOrder(EstadoPartido.PROGRAMADO, EstadoPartido.PROGRAMADO);
    }

    @Test
    @DisplayName("findByCompeticion: debe buscar partidos por competición")
    void testFindByCompeticion() {
        List<Partido> partidos = adapter.findByCompeticion(ID_COMPETICION);
        assertThat(partidos).hasSize(3);
        assertThat(partidos)
                .extracting(Partido::getCompeticion)
                .extracting(c -> c.getIdCompeticion())
                .allMatch(id -> id.equals(ID_COMPETICION));
    }

    @Test
    @DisplayName("findByEstado: debe buscar partidos por estado")
    void testFindByEstado() {
        // Buscar FINALIZADOS
        List<Partido> finalizados = adapter.findByEstado(EstadoPartido.FINALIZADO);
        assertThat(finalizados).hasSize(1);
        assertThat(finalizados.get(0).getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
        assertThat(finalizados.get(0).getIdPartido()).isEqualTo(ID_PARTIDO_1);

        // Buscar PROGRAMADOS
        List<Partido> programados = adapter.findByEstado(EstadoPartido.PROGRAMADO);
        assertThat(programados).hasSize(2);
        assertThat(programados)
                .extracting(Partido::getEstado)
                .allMatch(estado -> estado == EstadoPartido.PROGRAMADO);
    }

    @Test
    @DisplayName("findByFechaBetween: debe buscar partidos por rango de fechas")
    void testFindByFechaBetween() {
        // Buscar partidos entre hace 3 meses y hoy
        LocalDateTime desde = ahora.minusMonths(3);
        LocalDateTime hasta = ahora;
        List<Partido> partidos = adapter.findByFechaBetween(desde, hasta);
        assertThat(partidos).hasSize(1);
        assertThat(partidos.get(0).getIdPartido()).isEqualTo(ID_PARTIDO_1);

        // Buscar partidos entre hoy y 3 meses en el futuro
        LocalDateTime desde2 = ahora;
        LocalDateTime hasta2 = ahora.plusMonths(3);
        List<Partido> partidos2 = adapter.findByFechaBetween(desde2, hasta2);
        assertThat(partidos2).hasSize(2);
        assertThat(partidos2)
                .extracting(Partido::getIdPartido)
                .containsExactlyInAnyOrder(ID_PARTIDO_2, ID_PARTIDO_3);

        // Buscar partidos en un rango sin partidos
        LocalDateTime desde3 = ahora.plusMonths(4);
        LocalDateTime hasta3 = ahora.plusMonths(5);
        List<Partido> partidos3 = adapter.findByFechaBetween(desde3, hasta3);
        assertThat(partidos3).isEmpty();
    }

    @Test
    @DisplayName("findByArbitro: debe buscar partidos por árbitro")
    void testFindByArbitro() {
        // Arbitro 1 (Oliver) arbitra partido1 y partido3
        List<Partido> partidos = adapter.findByArbitro(ID_ARBITRO_1);
        assertThat(partidos).hasSize(2);
        assertThat(partidos)
                .extracting(Partido::getIdPartido)
                .containsExactlyInAnyOrder(ID_PARTIDO_1, ID_PARTIDO_3);
        
        // Arbitro 2 (Taylor) arbitra partido2
        List<Partido> partidos2 = adapter.findByArbitro(ID_ARBITRO_2);
        assertThat(partidos2).hasSize(1);
        assertThat(partidos2.get(0).getIdPartido()).isEqualTo(ID_PARTIDO_2);
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia")
    void testExistsById() {
        assertThat(adapter.existsById(ID_PARTIDO_1)).isTrue();
        assertThat(adapter.existsById(ID_PARTIDO_2)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar un partido")
    void testDeleteById() {
        // Verificar que existe
        assertThat(adapter.existsById(ID_PARTIDO_3)).isTrue();
        
        // Eliminar
        adapter.deleteById(ID_PARTIDO_3);
        
        // Verificar que ya no existe
        assertThat(adapter.existsById(ID_PARTIDO_3)).isFalse();
        
        // Verificar que quedan 2 partidos
        List<Partido> todos = adapter.findAll();
        assertThat(todos).hasSize(2);
        assertThat(todos)
                .extracting(Partido::getIdPartido)
                .containsExactlyInAnyOrder(ID_PARTIDO_1, ID_PARTIDO_2);
    }

    @Test
    @DisplayName("save: debe guardar un nuevo partido")
    void testSave() {
        // Obtener referencias necesarias de la BD
        ClubJPAEntity clubLocal = clubRepository.findById(ID_CLUB_1).orElseThrow();
        ClubJPAEntity clubVisitante = clubRepository.findById(ID_CLUB_2).orElseThrow();
        CompeticionJPAEntity competicion = competicionRepository.findById(ID_COMPETICION).orElseThrow();
        ArbitroJPAEntity arbitro = arbitroRepository.findById(ID_ARBITRO_1).orElseThrow();
        EstadioJPAEntity estadio = estadioRepository.findById(ID_ESTADIO_1).orElseThrow();

        // Crear Partido con todos los datos necesarios
        Partido nuevoPartido = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(com.futbol.estadisticas.domain.model.Club.builder()
                        .idEquipo(ID_CLUB_1)
                        .build())
                .equipoVisitante(com.futbol.estadisticas.domain.model.Club.builder()
                        .idEquipo(ID_CLUB_2)
                        .build())
                .estado(EstadoPartido.PROGRAMADO)
                .fechaYHora(ahora.plusMonths(3))
                .jornada(15)
                .golesLocal(0)
                .golesVisitante(0)
                .build();

        Partido guardado = adapter.save(nuevoPartido);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdPartido()).isNotNull();
        assertThat(guardado.getEstado()).isEqualTo(EstadoPartido.PROGRAMADO);
        assertThat(guardado.getJornada()).isEqualTo(15);
    }

    @Test
    @DisplayName("save: debe actualizar un partido existente")
    void testUpdate() {
        // Primero obtener los datos necesarios de la BD
        ClubJPAEntity clubLocal = clubRepository.findById(ID_CLUB_1).orElseThrow();
        ClubJPAEntity clubVisitante = clubRepository.findById(ID_CLUB_2).orElseThrow();
        CompeticionJPAEntity competicion = competicionRepository.findById(ID_COMPETICION).orElseThrow();
        ArbitroJPAEntity arbitro = arbitroRepository.findById(ID_ARBITRO_2).orElseThrow();
        EstadioJPAEntity estadio = estadioRepository.findById(ID_ESTADIO_1).orElseThrow();

        // Obtener el partido existente y verificar
        Optional<Partido> partidoOptional = adapter.findById(ID_PARTIDO_2);
        assertThat(partidoOptional).isPresent();
        
        // Crear el partido actualizado con todos los datos
        Partido partidoActualizado = Partido.builder()
                .idPartido(ID_PARTIDO_2)
                .equipoLocal(com.futbol.estadisticas.domain.model.Club.builder()
                        .idEquipo(ID_CLUB_1)
                        .build())
                .equipoVisitante(com.futbol.estadisticas.domain.model.Club.builder()
                        .idEquipo(ID_CLUB_2)
                        .build())
                .fechaYHora(ahora.plusWeeks(3))
                .jornada(8)
                .competicion(com.futbol.estadisticas.domain.model.Competicion.builder()
                        .idCompeticion(ID_COMPETICION)
                        .build())
                .arbitro(com.futbol.estadisticas.domain.model.Arbitro.builder()
                        .idArbitro(ID_ARBITRO_2)
                        .build())
                .estadio(com.futbol.estadisticas.domain.model.Estadio.builder()
                        .idEstadio(ID_ESTADIO_1)
                        .build())
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(3)
                .golesVisitante(1)
                .build();

        Partido guardado = adapter.save(partidoActualizado);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdPartido()).isEqualTo(ID_PARTIDO_2);
        assertThat(guardado.getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
        assertThat(guardado.getGolesLocal()).isEqualTo(3);
        assertThat(guardado.getGolesVisitante()).isEqualTo(1);
        
        // Verificar en la BD
        Optional<Partido> encontrado = adapter.findById(ID_PARTIDO_2);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
        assertThat(encontrado.get().getGolesLocal()).isEqualTo(3);
        assertThat(encontrado.get().getGolesVisitante()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByClub: debe retornar lista vacía cuando el club no tiene partidos")
    void testFindByClub_SinPartidos() {
        UUID idClubSinPartidos = UUID.fromString("77777777-8888-9999-aaaa-bbbbbbbbbbbb");
        List<Partido> partidos = adapter.findByClub(idClubSinPartidos);
        assertThat(partidos).isEmpty();
    }

    @Test
    @DisplayName("findByCompeticion: debe retornar lista vacía cuando la competición no tiene partidos")
    void testFindByCompeticion_SinPartidos() {
        UUID idCompeticionSinPartidos = UUID.randomUUID();
        List<Partido> partidos = adapter.findByCompeticion(idCompeticionSinPartidos);
        assertThat(partidos).isEmpty();
    }

    @Test
    @DisplayName("findByArbitro: debe retornar lista vacía cuando el árbitro no tiene partidos")
    void testFindByArbitro_SinPartidos() {
        UUID idArbitroSinPartidos = UUID.randomUUID();
        List<Partido> partidos = adapter.findByArbitro(idArbitroSinPartidos);
        assertThat(partidos).isEmpty();
    }
}