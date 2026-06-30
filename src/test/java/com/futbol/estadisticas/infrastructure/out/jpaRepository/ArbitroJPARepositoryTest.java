package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Arbitro;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.ArbitroRepositoryAdapter;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ArbitroJPAEntity;
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
class ArbitroJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ArbitroRepositoryAdapter adapter;

    @Autowired
    private ArbitroJPARepository repository;

    private static final UUID ID_ARBITRO_1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ID_ARBITRO_2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID ID_ARBITRO_3 = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    @BeforeEach
    void setUp() {
        repository.deleteAll();

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

        ArbitroJPAEntity arbitro3 = ArbitroJPAEntity.builder()
                .idArbitro(ID_ARBITRO_3)
                .nombre("Stuart")
                .apellido("Attwell")
                .fechaNacimiento(LocalDate.of(1982, 10, 6))
                .build();

        repository.saveAll(List.of(arbitro1, arbitro2, arbitro3));
    }

    @Test
    @DisplayName("findById: debe encontrar el árbitro por ID")
    void testFindById() {
        Optional<Arbitro> arbitro = adapter.findById(ID_ARBITRO_1);
        assertThat(arbitro).isPresent();
        assertThat(arbitro.get().getNombre()).isEqualTo("Michael");
        assertThat(arbitro.get().getApellido()).isEqualTo("Oliver");
    }

    @Test
    @DisplayName("findAll: debe retornar todos los árbitros")
    void testFindAll() {
        List<Arbitro> todos = adapter.findAll();
        assertThat(todos).hasSize(3);
        assertThat(todos)
                .extracting(Arbitro::getApellido)
                .containsExactlyInAnyOrder("Oliver", "Taylor", "Attwell");
    }

    @Test
    @DisplayName("findByNombreOrApellido: debe buscar por nombre o apellido")
    void testFindByNombreOrApellido() {
        List<Arbitro> resultados = adapter.findByNombreOrApellido("oliv");
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getApellido()).isEqualTo("Oliver");

        List<Arbitro> resultados2 = adapter.findByNombreOrApellido("anth");
        assertThat(resultados2).hasSize(1);
        assertThat(resultados2.get(0).getNombre()).isEqualTo("Anthony");
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia")
    void testExistsById() {
        assertThat(adapter.existsById(ID_ARBITRO_2)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar un árbitro")
    void testDeleteById() {
        assertThat(adapter.existsById(ID_ARBITRO_3)).isTrue();
        adapter.deleteById(ID_ARBITRO_3);
        assertThat(adapter.existsById(ID_ARBITRO_3)).isFalse();
        assertThat(adapter.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("save: debe guardar un nuevo árbitro")
    void testSave() {
        UUID nuevoId = UUID.randomUUID();
        Arbitro nuevoArbitro = Arbitro.builder()
                .idArbitro(nuevoId)
                .nombre("Nuevo")
                .apellido("Arbitro")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();

        Arbitro guardado = adapter.save(nuevoArbitro);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdArbitro()).isEqualTo(nuevoId);

        Optional<Arbitro> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(adapter.findAll()).hasSize(4);
    }
}