package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Tecnico;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.TecnicoRepositoryAdapter;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EquipoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.TecnicoJPAEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TecnicoJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private TecnicoRepositoryAdapter adapter;

    @Autowired
    private TecnicoJPARepository repository;

    @Autowired
    private EquipoJPARepository clubRepository;

    @Autowired
    private EstadioJPARepository estadioRepository;

    private static final UUID ID_TECNICO_1 = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    private static final UUID ID_TECNICO_2 = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    private static final UUID ID_TECNICO_3 = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff0");
    private static final UUID ID_CLUB_1 = UUID.fromString("44444444-5555-6666-7777-888888888888");
    private static final UUID ID_CLUB_2 = UUID.fromString("55555555-6666-7777-8888-999999999999");
    private static final UUID ID_CLUB_3 = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa");
    private static final UUID ID_ESTADIO_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_ESTADIO_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ID_ESTADIO_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @BeforeEach
    void setUp() {
        // Limpiar en orden inverso por las relaciones
        repository.deleteAll();
        clubRepository.deleteAll();
        estadioRepository.deleteAll();

        // 1. Crear estadios
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

        // 2. Crear clubes (sin técnicos aún)
        EquipoJPAEntity club1 = EquipoJPAEntity.builder()
                .idEquipo(ID_CLUB_1)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .fechaFundacion(LocalDate.of(1886, 12, 1))
                .estadio(estadio1)
                .build();

        EquipoJPAEntity club2 = EquipoJPAEntity.builder()
                .idEquipo(ID_CLUB_2)
                .nombre("Manchester City")
                .nombreCorto("MCI")
                .fechaFundacion(LocalDate.of(1880, 11, 1))
                .estadio(estadio2)
                .build();

        EquipoJPAEntity club3 = EquipoJPAEntity.builder()
                .idEquipo(ID_CLUB_3)
                .nombre("Chelsea FC")
                .nombreCorto("CHE")
                .fechaFundacion(LocalDate.of(1905, 3, 10))
                .estadio(estadio3)
                .build();

        clubRepository.saveAll(List.of(club1, club2, club3));

        // 3. Crear técnicos
        TecnicoJPAEntity tecnico1 = TecnicoJPAEntity.builder()
                .idPersonal(ID_TECNICO_1)
                .nombre("Mikel")
                .apellido("Arteta")
                .fechaNacimiento(LocalDate.of(1982, 3, 26))
                .nacionalidad(Nacion.ESPAÑA)
                .estiloJuego("Presión alta y posesión")
                .alineacionFavorita("4-3-3")
                .build();

        TecnicoJPAEntity tecnico2 = TecnicoJPAEntity.builder()
                .idPersonal(ID_TECNICO_2)
                .nombre("Pep")
                .apellido("Guardiola")
                .fechaNacimiento(LocalDate.of(1971, 1, 18))
                .nacionalidad(Nacion.ESPAÑA)
                .estiloJuego("Fútbol posicional")
                .alineacionFavorita("4-3-3")
                .build();

        TecnicoJPAEntity tecnico3 = TecnicoJPAEntity.builder()
                .idPersonal(ID_TECNICO_3)
                .nombre("Jurgen")
                .apellido("Klopp")
                .fechaNacimiento(LocalDate.of(1967, 6, 16))
                .nacionalidad(Nacion.ALEMANIA)
                .estiloJuego("Gegenpressing")
                .alineacionFavorita("4-4-2")
                .build();

        repository.saveAll(List.of(tecnico1, tecnico2, tecnico3));

        // 4. Obtener las entidades gestionadas
        EquipoJPAEntity clubManaged1 = clubRepository.findById(ID_CLUB_1).orElseThrow();
        EquipoJPAEntity clubManaged2 = clubRepository.findById(ID_CLUB_2).orElseThrow();
        EquipoJPAEntity clubManaged3 = clubRepository.findById(ID_CLUB_3).orElseThrow();

        TecnicoJPAEntity tecnicoManaged1 = repository.findById(ID_TECNICO_1).orElseThrow();
        TecnicoJPAEntity tecnicoManaged2 = repository.findById(ID_TECNICO_2).orElseThrow();
        TecnicoJPAEntity tecnicoManaged3 = repository.findById(ID_TECNICO_3).orElseThrow();

        // 5. Establecer las relaciones bidireccionales
        clubManaged1.setTecnicoActual(tecnicoManaged1);
        tecnicoManaged1.setEquipoActual(clubManaged1);

        clubManaged2.setTecnicoActual(tecnicoManaged2);
        tecnicoManaged2.setEquipoActual(clubManaged2);

        clubManaged3.setTecnicoActual(tecnicoManaged3);
        tecnicoManaged3.setEquipoActual(clubManaged3);

        // 6. Guardar todo
        clubRepository.saveAll(List.of(clubManaged1, clubManaged2, clubManaged3));
        repository.saveAll(List.of(tecnicoManaged1, tecnicoManaged2, tecnicoManaged3));
    }

    @Test
    @DisplayName("findById: debe encontrar el técnico por ID")
    void testFindById() {
        Optional<Tecnico> tecnico = adapter.findById(ID_TECNICO_1);
        assertThat(tecnico).isPresent();
        assertThat(tecnico.get().getNombre()).isEqualTo("Mikel");
        assertThat(tecnico.get().getApellido()).isEqualTo("Arteta");
        assertThat(tecnico.get().getNacionalidad()).isEqualTo(Nacion.ESPAÑA);
        assertThat(tecnico.get().getEstiloJuego()).isEqualTo("Presión alta y posesión");
    }

    @Test
    @DisplayName("findAll: debe retornar todos los técnicos")
    void testFindAll() {
        List<Tecnico> todos = adapter.findAll();
        assertThat(todos).hasSize(3);
        assertThat(todos)
                .extracting(Tecnico::getApellido)
                .containsExactlyInAnyOrder("Arteta", "Guardiola", "Klopp");
    }

    @Test
    @DisplayName("findByClub: debe buscar técnicos por club")
    void testFindByEquipo() {
        List<Tecnico> tecnicos = adapter.findByEquipo(ID_CLUB_1);
        assertThat(tecnicos).hasSize(1);
        assertThat(tecnicos.get(0).getNombre()).isEqualTo("Mikel");
        assertThat(tecnicos.get(0).getApellido()).isEqualTo("Arteta");
    }

    @Test
    @DisplayName("findTecnicoActualByClub: debe buscar el técnico actual de un club")
    void testFindTecnicoActualByEquipo() {
        Optional<Tecnico> tecnico = adapter.findTecnicoActualByEquipo(ID_CLUB_1);
        assertThat(tecnico).isPresent();
        assertThat(tecnico.get().getNombre()).isEqualTo("Mikel");
        assertThat(tecnico.get().getApellido()).isEqualTo("Arteta");
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia")
    void testExistsById() {
        assertThat(adapter.existsById(ID_TECNICO_1)).isTrue();
        assertThat(adapter.existsById(ID_TECNICO_2)).isTrue();
        assertThat(adapter.existsById(ID_TECNICO_3)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar un técnico")
    void testDeleteById() {
        // Primero desasociar el técnico del club
        EquipoJPAEntity club = clubRepository.findById(ID_CLUB_3).orElseThrow();
        club.setTecnicoActual(null);
        clubRepository.save(club);

        // Eliminar el técnico
        assertThat(adapter.existsById(ID_TECNICO_3)).isTrue();
        adapter.deleteById(ID_TECNICO_3);
        assertThat(adapter.existsById(ID_TECNICO_3)).isFalse();

        List<Tecnico> todos = adapter.findAll();
        assertThat(todos).hasSize(2);
        assertThat(todos)
                .extracting(Tecnico::getApellido)
                .containsExactlyInAnyOrder("Arteta", "Guardiola");
    }

    @Test
    @DisplayName("save: debe guardar un nuevo técnico sin club")
    void testSave() {
        UUID nuevoId = UUID.randomUUID();
        Tecnico nuevoTecnico = Tecnico.builder()
                .idPersonal(nuevoId)
                .nombre("Nuevo")
                .apellido("Tecnico")
                .fechaNacimiento(LocalDate.of(1980, 1, 1))
                .nacionalidad(Nacion.ALEMANIA)
                .estiloJuego("Tiki-taka")
                .alineacionFavorita("4-2-3-1")
                .build();

        Tecnico guardado = adapter.save(nuevoTecnico);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdPersonal()).isEqualTo(nuevoId);
        assertThat(guardado.getNombre()).isEqualTo("Nuevo");

        Optional<Tecnico> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(adapter.findAll()).hasSize(4);
    }

    @Test
    @DisplayName("save: debe actualizar un técnico existente")
    void testUpdate() {
        Optional<Tecnico> tecnicoOptional = adapter.findById(ID_TECNICO_1);
        assertThat(tecnicoOptional).isPresent();

        Tecnico tecnico = tecnicoOptional.get();
        tecnico.setEstiloJuego("Nuevo estilo de juego");
        tecnico.setAlineacionFavorita("4-4-2");

        Tecnico actualizado = adapter.save(tecnico);
        assertThat(actualizado).isNotNull();
        assertThat(actualizado.getIdPersonal()).isEqualTo(ID_TECNICO_1);
        assertThat(actualizado.getEstiloJuego()).isEqualTo("Nuevo estilo de juego");
        assertThat(actualizado.getAlineacionFavorita()).isEqualTo("4-4-2");

        Optional<Tecnico> encontrado = adapter.findById(ID_TECNICO_1);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEstiloJuego()).isEqualTo("Nuevo estilo de juego");
        assertThat(adapter.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("buscarTecnicoPorNombre: debe buscar técnicos por nombre o apellido")
    void testBuscarTecnicoPorNombre() {
        // Buscar por apellido
        Page<Tecnico> page = adapter.buscarTecnicoPorNombre("Arteta", PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getApellido()).isEqualTo("Arteta");

        // Buscar por nombre parcial
        Page<Tecnico> page2 = adapter.buscarTecnicoPorNombre("Pep", PageRequest.of(0, 10));
        assertThat(page2.getContent()).hasSize(1);
        assertThat(page2.getContent().get(0).getNombre()).isEqualTo("Pep");

        // Buscar por coincidencia parcial
        Page<Tecnico> page3 = adapter.buscarTecnicoPorNombre("ard", PageRequest.of(0, 10));
        assertThat(page3.getContent()).hasSize(1);
        assertThat(page3.getContent().get(0).getApellido()).isEqualTo("Guardiola");
    }

    @Test
    @DisplayName("findByClub: debe retornar lista vacía cuando el club no tiene técnico")
    void testFindByEquipo_SinTecnico() {
        // Crear un club sin técnico
        UUID idClubSinTecnico = UUID.randomUUID();
        EquipoJPAEntity clubSinTecnico = EquipoJPAEntity.builder()
                .idEquipo(idClubSinTecnico)
                .nombre("Club Sin Tecnico")
                .nombreCorto("CST")
                .fechaFundacion(LocalDate.now())
                .build();
        clubRepository.save(clubSinTecnico);

        List<Tecnico> tecnicos = adapter.findByEquipo(idClubSinTecnico);
        assertThat(tecnicos).isEmpty();
    }

    @Test
    @DisplayName("findTecnicoActualByClub: debe retornar Optional.empty cuando el club no tiene técnico")
    void testFindTecnicoActualByClub_SinTecnico() {
        // Crear un club sin técnico
        UUID idClubSinTecnico = UUID.randomUUID();
        EquipoJPAEntity clubSinTecnico = EquipoJPAEntity.builder()
                .idEquipo(idClubSinTecnico)
                .nombre("Club Sin Tecnico")
                .nombreCorto("CST")
                .fechaFundacion(LocalDate.now())
                .build();
        clubRepository.save(clubSinTecnico);

        Optional<Tecnico> tecnico = adapter.findTecnicoActualByEquipo(idClubSinTecnico);
        assertThat(tecnico).isEmpty();
    }

    @Test
    @DisplayName("findById: debe retornar Optional.empty cuando el ID no existe")
    void testFindById_NoExiste() {
        Optional<Tecnico> tecnico = adapter.findById(UUID.randomUUID());
        assertThat(tecnico).isEmpty();
    }

    @Test
    @DisplayName("buscarTecnicoPorNombre: debe retornar página vacía cuando el texto es null o vacío")
    void testBuscarTecnicoPorNombre_TextoVacio() {
        // Test con null
        Page<Tecnico> page1 = adapter.buscarTecnicoPorNombre(null, PageRequest.of(0, 10));
        assertThat(page1).isEmpty();
        assertThat(page1.getTotalElements()).isEqualTo(0);

        // Test con texto vacío
        Page<Tecnico> page2 = adapter.buscarTecnicoPorNombre("", PageRequest.of(0, 10));
        assertThat(page2).isEmpty();
        assertThat(page2.getTotalElements()).isEqualTo(0);

        // Test con texto solo espacios
        Page<Tecnico> page3 = adapter.buscarTecnicoPorNombre("   ", PageRequest.of(0, 10));
        assertThat(page3).isEmpty();
        assertThat(page3.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("buscarTecnicoPorNombre: debe retornar página vacía cuando no hay coincidencias")
    void testBuscarTecnicoPorNombre_SinResultados() {
        Page<Tecnico> page = adapter.buscarTecnicoPorNombre("xyz", PageRequest.of(0, 10));
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("deleteById: debe lanzar excepción cuando el ID no existe")
    void testDeleteById_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        assertThat(adapter.existsById(idInexistente)).isFalse();
        adapter.deleteById(idInexistente); // No debe lanzar excepción
    }
}