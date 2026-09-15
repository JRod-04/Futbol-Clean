package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoEquipo;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EquipoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.EquipoRepositoryAdapter;

import jakarta.transaction.Transactional;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class EquipoJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    private EquipoRepositoryAdapter adapter;

    @Autowired
    private EquipoJPARepository repository;

    @Autowired
    private EstadioJPARepository estadioRepository;

    private static final UUID ID_CLUB_1   = UUID.fromString("44444444-5555-6666-7777-888888888888");
    private static final UUID ID_CLUB_2   = UUID.fromString("55555555-6666-7777-8888-999999999999");
    private static final UUID ID_CLUB_3   = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa");
    private static final UUID ID_ESTADIO_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_ESTADIO_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_ESTADIO_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        estadioRepository.deleteAll();
        repository.flush();
        estadioRepository.flush();

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
        estadioRepository.flush();

        EquipoJPAEntity club1 = EquipoJPAEntity.builder()
                .idEquipo(ID_CLUB_1)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .fechaFundacion(LocalDate.of(1886, 12, 1))
                .paisEquipo(Nacion.INGLATERRA)
                .tipo(TipoEquipo.CLUB_PROFESIONAL)
                .estadio(estadio1)
                .build();

        EquipoJPAEntity club2 = EquipoJPAEntity.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("Manchester City")
                .nombreCorto("MCI")
                .fechaFundacion(LocalDate.of(1880, 11, 1))
                .paisEquipo(Nacion.INGLATERRA)
                .tipo(TipoEquipo.CLUB_PROFESIONAL)
                .estadio(estadio2)
                .build();

        EquipoJPAEntity club3 = EquipoJPAEntity.builder()
                .idEquipo(ID_CLUB_3)
                .nombre("Chelsea FC")
                .nombreCorto("CHE")
                .fechaFundacion(LocalDate.of(1905, 3, 10))
                .paisEquipo(Nacion.INGLATERRA)
                .tipo(TipoEquipo.CLUB_PROFESIONAL)
                .estadio(estadio3)
                .build();

        repository.saveAll(List.of(club1, club2, club3));
        repository.flush();
    }

    @Test
    @DisplayName("findById: debe encontrar el club por ID")
    void testFindById() {
        Optional<Equipo> club = adapter.findById(ID_CLUB_1);
        assertThat(club).isPresent();
        assertThat(club.get().getNombre()).isEqualTo("Arsenal FC");
        assertThat(club.get().getNombreCorto()).isEqualTo("ARS");
    }

    @Test
    @DisplayName("findById: debe devolver vacío si no existe")
    void testFindByIdNoExiste() {
        assertThat(adapter.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("findAll: debe retornar todos los clubes")
    void testFindAll() {
        List<Equipo> todos = adapter.findAll();
        assertThat(todos).hasSize(3);
        assertThat(todos)
                .extracting(Equipo::getNombreCorto)
                .containsExactlyInAnyOrder("ARS", "MCI", "CHE");
    }

    @Test
    @DisplayName("buscarEquipoPorNombre: debe buscar clubes por nombre con paginación")
    void testBuscarEquipoPorNombre() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Equipo> resultados = adapter.buscarEquipoPorNombre("City", pageable);

        assertThat(resultados.getTotalElements()).isEqualTo(1);
        assertThat(resultados.getContent())
                .extracting(Equipo::getNombre)
                .containsExactly("Manchester City");
    }

    @Test
    @DisplayName("buscarEquipoPorNombre: texto vacío devuelve página vacía")
    void testBuscarEquipoPorNombre_Vacio() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Equipo> resultados = adapter.buscarEquipoPorNombre("   ", pageable);

        assertThat(resultados).isEmpty();
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
    @DisplayName("save: debe guardar un nuevo club")
    void testSave() {
        UUID nuevoId = UUID.randomUUID();
        Equipo nuevoClub = Equipo.builder()
                .idEquipo(nuevoId)
                .nombre("Nuevo Club")
                .nombreCorto("NCL")
                .fechaFundacion(LocalDate.of(2000, 1, 1))
                .build();

        Equipo guardado = adapter.save(nuevoClub);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdEquipo()).isEqualTo(nuevoId);
        assertThat(guardado.getNombre()).isEqualTo("Nuevo Club");
        assertThat(guardado.getNombreCorto()).isEqualTo("NCL");

        Optional<Equipo> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(adapter.findAll()).hasSize(4);
    }

}