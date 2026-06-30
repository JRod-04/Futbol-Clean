package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PersonalDeportivoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.TecnicoJPAEntity;

import jakarta.transaction.Transactional;
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
class PersonalDeportivoJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private PersonalDeportivoJPARepository repository;

    private static final UUID ID_PERSONAL_1 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private static final UUID ID_PERSONAL_2 = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID ID_PERSONAL_3 = UUID.fromString("22222222-3333-4444-5555-666666666666");
    private static final UUID ID_PERSONAL_4 = UUID.fromString("33333333-4444-5555-6666-777777777777");
    private static final UUID ID_PERSONAL_5 = UUID.fromString("44444444-5555-6666-7777-888888888888");

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // Crear personal deportivo SIN establecer tipoPersonal
        // JPA lo asignará automáticamente mediante el discriminador
        PersonalDeportivoJPAEntity personal1 = PersonalDeportivoJPAEntity.builder()
                .idPersonal(ID_PERSONAL_1)
                .nombre("Bukayo")
                .apellido("Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .build();

        PersonalDeportivoJPAEntity personal2 = PersonalDeportivoJPAEntity.builder()
                .idPersonal(ID_PERSONAL_2)
                .nombre("Mikel")
                .apellido("Arteta")
                .fechaNacimiento(LocalDate.of(1982, 3, 26))
                .build();

        PersonalDeportivoJPAEntity personal3 = PersonalDeportivoJPAEntity.builder()
                .idPersonal(ID_PERSONAL_3)
                .nombre("Doctor")
                .apellido("House")
                .fechaNacimiento(LocalDate.of(1975, 6, 15))
                .build();

        PersonalDeportivoJPAEntity personal4 = PersonalDeportivoJPAEntity.builder()
                .idPersonal(ID_PERSONAL_4)
                .nombre("Pep")
                .apellido("Guardiola")
                .fechaNacimiento(LocalDate.of(1971, 1, 18))
                .build();

        PersonalDeportivoJPAEntity personal5 = PersonalDeportivoJPAEntity.builder()
                .idPersonal(ID_PERSONAL_5)
                .nombre("Physical")
                .apellido("Trainer")
                .fechaNacimiento(LocalDate.of(1980, 5, 10))
                .build();

        repository.saveAll(List.of(personal1, personal2, personal3, personal4, personal5));
    }

    @Test
    @DisplayName("findById: debe encontrar el personal por ID")
    void testFindById() {
        Optional<PersonalDeportivoJPAEntity> personal = repository.findById(ID_PERSONAL_1);
        assertThat(personal).isPresent();
        assertThat(personal.get().getNombre()).isEqualTo("Bukayo");
        assertThat(personal.get().getApellido()).isEqualTo("Saka");
        // ✅ No verificamos getTipoPersonal() porque la entidad no tiene ese campo
    }

    @Test
    @DisplayName("findAll: debe retornar todo el personal")
    void testFindAll() {
        List<PersonalDeportivoJPAEntity> todos = repository.findAll();
        assertThat(todos).hasSize(5);
        // ✅ Verificamos por nombre en lugar de tipoPersonal
        assertThat(todos)
                .extracting(PersonalDeportivoJPAEntity::getNombre)
                .containsExactlyInAnyOrder("Bukayo", "Mikel", "Doctor", "Pep", "Physical");
    }

    


    @Test
    @DisplayName("findByNombreOrApellido: debe buscar personal por nombre o apellido")
    void testFindByNombreOrApellido() {
        List<PersonalDeportivoJPAEntity> resultados = repository.findByNombreOrApellido("Saka");
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Bukayo");
        assertThat(resultados.get(0).getApellido()).isEqualTo("Saka");

        List<PersonalDeportivoJPAEntity> resultados2 = repository.findByNombreOrApellido("Mikel");
        assertThat(resultados2).hasSize(1);
        assertThat(resultados2.get(0).getNombre()).isEqualTo("Mikel");
        assertThat(resultados2.get(0).getApellido()).isEqualTo("Arteta");

        List<PersonalDeportivoJPAEntity> resultados3 = repository.findByNombreOrApellido("Perez");
        assertThat(resultados3).isEmpty();
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia")
    void testExistsById() {
        assertThat(repository.existsById(ID_PERSONAL_1)).isTrue();
        assertThat(repository.existsById(ID_PERSONAL_2)).isTrue();
        assertThat(repository.existsById(ID_PERSONAL_3)).isTrue();
        assertThat(repository.existsById(ID_PERSONAL_4)).isTrue();
        assertThat(repository.existsById(ID_PERSONAL_5)).isTrue();
        assertThat(repository.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar un personal")
    void testDeleteById() {
        assertThat(repository.existsById(ID_PERSONAL_5)).isTrue();
        repository.deleteById(ID_PERSONAL_5);
        assertThat(repository.existsById(ID_PERSONAL_5)).isFalse();
        
        List<PersonalDeportivoJPAEntity> todos = repository.findAll();
        assertThat(todos).hasSize(4);
        // ✅ Verificamos por nombre en lugar de tipoPersonal
        assertThat(todos)
                .extracting(PersonalDeportivoJPAEntity::getNombre)
                .containsExactlyInAnyOrder("Bukayo", "Mikel", "Doctor", "Pep");
    }

    @Test
    @DisplayName("findByNombreOrApellido: debe ser insensible a mayúsculas")
    void testFindByNombreOrApellido_Insensitive() {
        List<PersonalDeportivoJPAEntity> resultados = repository.findByNombreOrApellido("SAKA");
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Bukayo");

        List<PersonalDeportivoJPAEntity> resultados2 = repository.findByNombreOrApellido("arteta");
        assertThat(resultados2).hasSize(1);
        assertThat(resultados2.get(0).getNombre()).isEqualTo("Mikel");
    }



    @Test
    @DisplayName("save: debe guardar un nuevo personal")
    void testSave() {
        UUID nuevoId = UUID.randomUUID();
        PersonalDeportivoJPAEntity nuevoPersonal = PersonalDeportivoJPAEntity.builder()
                .idPersonal(nuevoId)
                .nombre("Nuevo")
                .apellido("Personal")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();

        PersonalDeportivoJPAEntity guardado = repository.save(nuevoPersonal);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdPersonal()).isEqualTo(nuevoId);
        assertThat(guardado.getNombre()).isEqualTo("Nuevo");

        Optional<PersonalDeportivoJPAEntity> encontrado = repository.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Nuevo");
        
        List<PersonalDeportivoJPAEntity> todos = repository.findAll();
        assertThat(todos).hasSize(6);
    }

    @Test
    @DisplayName("findById: debe retornar Optional.empty cuando el ID no existe")
    void testFindById_NoExiste() {
        Optional<PersonalDeportivoJPAEntity> personal = repository.findById(UUID.randomUUID());
        assertThat(personal).isEmpty();
    }
}