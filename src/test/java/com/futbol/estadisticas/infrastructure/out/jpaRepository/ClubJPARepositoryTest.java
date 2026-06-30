package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.ClubRepositoryAdapter;

import jakarta.transaction.Transactional;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ClubJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ClubRepositoryAdapter adapter;

    @Autowired
    private ClubJPARepository repository;

    @Autowired
    private EstadioJPARepository estadioRepository;

    private static final UUID ID_CLUB_1 = UUID.fromString("44444444-5555-6666-7777-888888888888");
    private static final UUID ID_CLUB_2 = UUID.fromString("55555555-6666-7777-8888-999999999999");
    private static final UUID ID_CLUB_3 = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa");
    private static final UUID ID_ESTADIO_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_ESTADIO_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_ESTADIO_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        estadioRepository.deleteAll();

        // Crear 3 estadios diferentes (cada uno para un club diferente)
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

        // Cada club tiene su propio estadio (sin duplicados)
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
                .estadio(estadio3)  // Estadio 3 (diferente)
                .build();

        repository.saveAll(List.of(club1, club2, club3));
    }

    @Test
    @DisplayName("findById: debe encontrar el club por ID")
    void testFindById() {
        Optional<Club> club = adapter.findById(ID_CLUB_1);
        assertThat(club).isPresent();
        assertThat(club.get().getNombre()).isEqualTo("Arsenal FC");
        assertThat(club.get().getNombreCorto()).isEqualTo("ARS");
    }

    @Test
    @DisplayName("findAll: debe retornar todos los clubes")
    void testFindAll() {
        List<Club> todos = adapter.findAll();
        assertThat(todos).hasSize(3);
        assertThat(todos)
                .extracting(Club::getNombreCorto)
                .containsExactlyInAnyOrder("ARS", "MCI", "CHE");
    }

    @Test
    @DisplayName("findByNombre: debe buscar clubes por nombre")
    void testFindByNombre() {
        List<Club> resultados = adapter.findByNombre("City");
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Manchester City");
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia")
    void testExistsById() {
        assertThat(adapter.existsById(ID_CLUB_2)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar un club")
    void testDeleteById() {
        assertThat(adapter.existsById(ID_CLUB_3)).isTrue();
        adapter.deleteById(ID_CLUB_3);
        assertThat(adapter.existsById(ID_CLUB_3)).isFalse();
        assertThat(adapter.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("save: debe guardar un nuevo club con un estadio")
    void testSave() {
        // Crear un nuevo estadio para el nuevo club
        UUID nuevoEstadioId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        EstadioJPAEntity nuevoEstadio = EstadioJPAEntity.builder()
                .idEstadio(nuevoEstadioId)
                .nombre("Nuevo Estadio")
                .direccion("Nueva Dirección")
                .capacidad(50000)
                .fechaFundacion(LocalDate.of(2000, 1, 1))
                .build();
        estadioRepository.save(nuevoEstadio);

        // Crear el nuevo club con su estadio
        UUID nuevoId = UUID.randomUUID();
        Club nuevoClub = Club.builder()
                .idEquipo(nuevoId)
                .nombre("Nuevo Club")
                .nombreCorto("NCL")
                .fechaFundacion(LocalDate.of(2000, 1, 1))
                .build();

        Club guardado = adapter.save(nuevoClub);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdEquipo()).isEqualTo(nuevoId);
        assertThat(guardado.getNombre()).isEqualTo("Nuevo Club");

        Optional<Club> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(adapter.findAll()).hasSize(4);
    }
}