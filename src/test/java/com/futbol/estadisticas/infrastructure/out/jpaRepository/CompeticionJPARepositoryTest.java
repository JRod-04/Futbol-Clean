package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.CompeticionRepositoryAdapter;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.CompeticionJPAEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompeticionJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private CompeticionRepositoryAdapter adapter;

    @Autowired
    private CompeticionJPARepository repository;

    // IDs fijos para las pruebas
    private static final UUID ID_PREMIER = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID ID_CHAMPIONS = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final UUID ID_FA_CUP = UUID.fromString("77777777-7777-7777-7777-77777777777a");
    private static final UUID ID_ALWAYS_ACTIVE = UUID.fromString("99999999-9999-9999-9999-9999999999ab");

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada prueba
        repository.deleteAll();

        // Crear competiciones de prueba
        CompeticionJPAEntity premier = CompeticionJPAEntity.builder()
                .idCompeticion(ID_PREMIER)
                .nombre("Premier League")
                .fechaInicio(LocalDateTime.of(2024, 8, 16, 0, 0))
                .fechaFin(LocalDateTime.of(2025, 5, 25, 23, 59))
                .build();

        CompeticionJPAEntity champions = CompeticionJPAEntity.builder()
                .idCompeticion(ID_CHAMPIONS)
                .nombre("UEFA Champions League")
                .fechaInicio(LocalDateTime.of(2024, 9, 17, 0, 0))
                .fechaFin(LocalDateTime.of(2025, 6, 10, 23, 59))
                .build();

        CompeticionJPAEntity faCup = CompeticionJPAEntity.builder()
                .idCompeticion(ID_FA_CUP)
                .nombre("FA Cup")
                .fechaInicio(LocalDateTime.of(2025, 1, 11, 0, 0))
                .fechaFin(LocalDateTime.of(2025, 5, 17, 23, 59))
                .build();

        CompeticionJPAEntity alwaysActive = CompeticionJPAEntity.builder()
                .idCompeticion(ID_ALWAYS_ACTIVE)
                .nombre("Always Active Competition")
                .fechaInicio(LocalDateTime.now().minusDays(1))
                .fechaFin(LocalDateTime.now().plusDays(365))
                .build();

        repository.saveAll(List.of(premier, champions, faCup, alwaysActive));
    }

    @Test
    @DisplayName("findById: debe encontrar Premier League")
    void testFindById() {
        Optional<Competicion> comp = adapter.findById(ID_PREMIER);
        assertThat(comp).isPresent();
        assertThat(comp.get().getNombre()).isEqualTo("Premier League");
    }

    @Test
    @DisplayName("findAll: debe retornar las 4 competiciones del setup")
    void testFindAll() {
        List<Competicion> todas = adapter.findAll();
        assertThat(todas).hasSize(4);
        assertThat(todas)
                .extracting(Competicion::getNombre)
                .containsExactlyInAnyOrder(
                        "Premier League",
                        "UEFA Champions League",
                        "FA Cup",
                        "Always Active Competition"
                );
    }

    @Test
    @DisplayName("findActivas: debe encontrar al menos la competición Always Active")
    void testFindActivas() {
        List<Competicion> activas = adapter.findActivas();
        assertThat(activas).isNotEmpty();

        // La competición Always Active siempre debe estar presente
        assertThat(activas)
                .extracting(Competicion::getNombre)
                .contains("Always Active Competition");

        // Si estamos en 2024-2025, Premier y Champions también deberían estar activas
        int year = LocalDateTime.now().getYear();
        if (year >= 2024 && year <= 2025) {
            assertThat(activas)
                    .extracting(Competicion::getNombre)
                    .contains("Premier League", "UEFA Champions League");
        }
    }

    @Test
    @DisplayName("findByNombre: búsqueda parcial por 'Premier'")
    void testFindByNombre() {
        List<Competicion> resultado = adapter.findByNombre("Premier");
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Premier League");
    }

    @Test
    @DisplayName("findByNombre: búsqueda insensible a mayúsculas")
    void testFindByNombre_Insensitive() {
        List<Competicion> resultado = adapter.findByNombre("champions");
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("UEFA Champions League");
    }

    @Test
    @DisplayName("findByNombre: retorna vacío si no existe")
    void testFindByNombre_NoExiste() {
        List<Competicion> resultado = adapter.findByNombre("LaLiga");
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia de FA Cup")
    void testExistsById() {
        assertThat(adapter.existsById(ID_FA_CUP)).isTrue();
    }

    @Test
    @DisplayName("deleteById: solo elimina competiciones sin partidos")
    void testDeleteById() {
        // Verificar que FA Cup existe
        assertThat(adapter.existsById(ID_FA_CUP)).isTrue();

        // Eliminar FA Cup (no tiene partidos asociados)
        adapter.deleteById(ID_FA_CUP);

        // Verificar que ya no existe
        assertThat(adapter.existsById(ID_FA_CUP)).isFalse();

        // Verificar que quedan 3 competiciones
        List<Competicion> todas = adapter.findAll();
        assertThat(todas).hasSize(3);
        assertThat(todas)
                .extracting(Competicion::getNombre)
                .containsExactlyInAnyOrder(
                        "Premier League",
                        "UEFA Champions League",
                        "Always Active Competition"
                );
    }
}