package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Estadio;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.EstadioRepositoryAdapter;
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
class EstadioJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private EstadioRepositoryAdapter adapter;

    @Autowired
    private EstadioJPARepository repository;

    @Autowired
    private ClubJPARepository clubRepository;

    private static final UUID ID_ESTADIO_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_ESTADIO_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_CLUB_1 = UUID.fromString("44444444-5555-6666-7777-888888888888");

    @BeforeEach
    void setUp() {
        // IMPORTANTE: Primero eliminar clubes que referencian a estadios
        clubRepository.deleteAll();
        // Luego eliminar estadios
        repository.deleteAll();

        // Crear estadios
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

        repository.saveAll(List.of(estadio1, estadio2));

        // Crear club que referencia al estadio1
        ClubJPAEntity club = ClubJPAEntity.builder()
                .idEquipo(ID_CLUB_1)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .fechaFundacion(LocalDate.of(1886, 12, 1))
                .estadio(estadio1)  // Referencia al estadio1
                .build();

        clubRepository.save(club);
    }

    @Test
    @DisplayName("findById: debe encontrar el estadio por ID")
    void testFindById() {
        Optional<Estadio> estadio = adapter.findById(ID_ESTADIO_1);
        assertThat(estadio).isPresent();
        assertThat(estadio.get().getNombre()).isEqualTo("Emirates Stadium");
        assertThat(estadio.get().getCapacidad()).isEqualTo(60704);
    }

    @Test
    @DisplayName("findAll: debe retornar todos los estadios")
    void testFindAll() {
        List<Estadio> todos = adapter.findAll();
        assertThat(todos).hasSize(2);
        assertThat(todos)
                .extracting(Estadio::getNombre)
                .containsExactlyInAnyOrder("Emirates Stadium", "Etihad Stadium");
    }

    @Test
    @DisplayName("findByClubPrincipal: debe encontrar el estadio de un club")
    void testFindByClubPrincipal() {
        Optional<Estadio> estadio = adapter.findByClubPrincipal(ID_CLUB_1);
        assertThat(estadio).isPresent();
        assertThat(estadio.get().getNombre()).isEqualTo("Emirates Stadium");
        assertThat(estadio.get().getCapacidad()).isEqualTo(60704);
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia")
    void testExistsById() {
        assertThat(adapter.existsById(ID_ESTADIO_1)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar un estadio que no tiene clubes asociados")
    void testDeleteById() {
        // El estadio2 no tiene clubes asociados, se puede eliminar
        assertThat(adapter.existsById(ID_ESTADIO_2)).isTrue();
        adapter.deleteById(ID_ESTADIO_2);
        assertThat(adapter.existsById(ID_ESTADIO_2)).isFalse();
        assertThat(adapter.findAll()).hasSize(1);
        assertThat(adapter.findAll().get(0).getNombre()).isEqualTo("Emirates Stadium");
    }

    @Test
    @DisplayName("save: debe guardar un nuevo estadio")
    void testSave() {
        UUID nuevoId = UUID.randomUUID();
        Estadio nuevoEstadio = Estadio.builder()
                .idEstadio(nuevoId)
                .nombre("Nuevo Estadio")
                .direccion("Nueva Dirección")
                .capacidad(70000)
                .fechaFundacion(LocalDate.of(2020, 1, 1))
                .build();

        Estadio guardado = adapter.save(nuevoEstadio);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdEstadio()).isEqualTo(nuevoId);

        Optional<Estadio> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(adapter.findAll()).hasSize(3);
    }
}