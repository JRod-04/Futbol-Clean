package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.enums.EstadoCompeticion;
import com.futbol.estadisticas.domain.model.enums.Temporada;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.CompeticionRepositoryAdapter;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.CompeticionJPAEntity;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CompeticionJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private CompeticionRepositoryAdapter adapter;

    @Autowired
    private CompeticionJPARepository repository;

    private static final UUID ID_PREMIER       = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID ID_CHAMPIONS     = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final UUID ID_FA_CUP        = UUID.fromString("77777777-7777-7777-7777-777777777777");
    private static final UUID ID_ALWAYS_ACTIVE = UUID.fromString("99999999-9999-9999-9999-999999999999");

    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.flush();

        // Finalizada (en el pasado)
        CompeticionJPAEntity premier = CompeticionJPAEntity.builder()
                .idCompeticion(ID_PREMIER)
                .nombre("Premier League")
                .temporada(Temporada.T2023_24)
                .estado(EstadoCompeticion.FINALIZADA)
                .fechaInicio(NOW.minusYears(1).minusMonths(2))
                .fechaFin(NOW.minusMonths(3))
                .build();

        // Activa (empezó hace poco, termina en el futuro)
        CompeticionJPAEntity champions = CompeticionJPAEntity.builder()
                .idCompeticion(ID_CHAMPIONS)
                .nombre("UEFA Champions League")
                .temporada(Temporada.T2024_25)
                .estado(EstadoCompeticion.EN_CURSO)
                .fechaInicio(NOW.minusMonths(1))
                .fechaFin(NOW.plusMonths(2))
                .build();

        // Futura (aún no empieza)
        CompeticionJPAEntity faCup = CompeticionJPAEntity.builder()
                .idCompeticion(ID_FA_CUP)
                .nombre("FA Cup")
                .temporada(Temporada.T2024_25)
                .estado(EstadoCompeticion.POR_INICIAR)
                .fechaInicio(NOW.plusMonths(3))
                .fechaFin(NOW.plusMonths(6))
                .build();

        // Siempre activa
        CompeticionJPAEntity alwaysActive = CompeticionJPAEntity.builder()
                .idCompeticion(ID_ALWAYS_ACTIVE)
                .nombre("Always Active Competition")
                .temporada(Temporada.T2024_25)
                .estado(EstadoCompeticion.EN_CURSO)
                .fechaInicio(NOW.minusDays(1))
                .fechaFin(NOW.plusDays(365))
                .build();

        repository.saveAll(List.of(premier, champions, faCup, alwaysActive));
        repository.flush();
    }

    @Test
    @DisplayName("findById: debe encontrar Premier League con todos sus campos")
    void testFindById() {
        Optional<Competicion> comp = adapter.findById(ID_PREMIER);
        assertThat(comp).isPresent();

        Competicion c = comp.get();
        assertThat(c.getIdCompeticion()).isEqualTo(ID_PREMIER);
        assertThat(c.getNombre()).isEqualTo("Premier League");
        assertThat(c.getTemporada()).isEqualTo(Temporada.T2023_24);
        assertThat(c.getEstado()).isEqualTo(EstadoCompeticion.FINALIZADA);
        assertThat(c.getFechaInicio()).isEqualToIgnoringNanos(NOW.minusYears(1).minusMonths(2));
        assertThat(c.getFechaFin()).isEqualToIgnoringNanos(NOW.minusMonths(3));
    }

    @Test
    @DisplayName("findById: debe devolver vacío si no existe")
    void testFindByIdNoExiste() {
        assertThat(adapter.findById(UUID.randomUUID())).isEmpty();
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
    @DisplayName("findActivas: debe retornar solo las competiciones con fecha actual dentro del rango")
    void testFindActivas() {
        List<Competicion> activas = adapter.findActivas();

        // Champions (NOW dentro del rango) + Always Active = 2
        assertThat(activas).hasSize(2);
        assertThat(activas)
                .extracting(Competicion::getNombre)
                .containsExactlyInAnyOrder(
                        "UEFA Champions League",
                        "Always Active Competition"
                )
                .doesNotContain("Premier League", "FA Cup");
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
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: elimina la competición indicada")
    void testDeleteById() {
        assertThat(adapter.existsById(ID_FA_CUP)).isTrue();

        adapter.deleteById(ID_FA_CUP);

        assertThat(adapter.existsById(ID_FA_CUP)).isFalse();

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

    @Test
    @DisplayName("save: debe guardar una nueva competición con todos sus campos")
    void testSave() {
        UUID nuevoId = UUID.randomUUID();
        Competicion nueva = Competicion.builder()
                .idCompeticion(nuevoId)
                .nombre("Copa del Rey")
                .temporada(Temporada.T2024_25)
                .estado(EstadoCompeticion.POR_INICIAR)
                .fechaInicio(NOW.plusDays(10))
                .fechaFin(NOW.plusDays(60))
                .build();

        Competicion guardada = adapter.save(nueva);
        assertThat(guardada).isNotNull();
        assertThat(guardada.getIdCompeticion()).isEqualTo(nuevoId);
        assertThat(guardada.getNombre()).isEqualTo("Copa del Rey");
        assertThat(guardada.getTemporada()).isEqualTo(Temporada.T2024_25);
        assertThat(guardada.getEstado()).isEqualTo(EstadoCompeticion.POR_INICIAR);

        Optional<Competicion> encontrada = adapter.findById(nuevoId);
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNombre()).isEqualTo("Copa del Rey");

        assertThat(adapter.findAll()).hasSize(5);
    }

    @Test
    @DisplayName("buscarCompeticionesPorNombre: página con texto parcial")
    void testBuscarCompeticionesPorNombre() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Competicion> page = adapter.buscarCompeticionesPorNombre("Prem", pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent())
                .extracting(Competicion::getNombre)
                .containsExactly("Premier League");
    }

    @Test
    @DisplayName("buscarCompeticionesPorNombre: texto vacío devuelve página vacía")
    void testBuscarCompeticionesPorNombre_Vacio() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Competicion> page = adapter.buscarCompeticionesPorNombre("   ", pageable);

        assertThat(page).isEmpty();
    }

    @Test
    @DisplayName("buscarCompeticionesPorNombre: paginación funciona")
    void testBuscarCompeticionesPorNombre_Paginacion() {
        Pageable page0 = PageRequest.of(0, 2);
        Pageable page1 = PageRequest.of(1, 2);

        Page<Competicion> p0 = adapter.buscarCompeticionesPorNombre("a", page0);
        Page<Competicion> p1 = adapter.buscarCompeticionesPorNombre("a", page1);

        // Todas contienen 'a' o 'A' en su nombre
        assertThat(p0.getTotalElements()).isEqualTo(4);
        assertThat(p0.getContent()).hasSize(2);
        assertThat(p1.getContent()).hasSize(2);
        assertThat(p0.getContent())
                .extracting(Competicion::getIdCompeticion)
                .doesNotContainAnyElementsOf(
                        p1.getContent().stream().map(Competicion::getIdCompeticion).toList()
                );
    }
}