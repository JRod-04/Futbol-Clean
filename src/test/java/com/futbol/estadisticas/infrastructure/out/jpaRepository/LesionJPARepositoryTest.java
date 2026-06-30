package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Lesion;
import com.futbol.estadisticas.domain.model.enums.Gravedad;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.LesionRepositoryAdapter;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.LesionJPAEntity;
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
class LesionJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private LesionRepositoryAdapter adapter;

    @Autowired
    private LesionJPARepository repository;

    @Autowired
    private JugadorJPARepository jugadorRepository;

    private static final UUID ID_JUGADOR_1 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private static final UUID ID_JUGADOR_2 = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID ID_JUGADOR_3 = UUID.fromString("22222222-3333-4444-5555-666666666666");

    private static final UUID ID_LESION_1 = UUID.fromString("33333333-4444-5555-6666-777777777777");
    private static final UUID ID_LESION_2 = UUID.fromString("44444444-5555-6666-7777-888888888888");
    private static final UUID ID_LESION_3 = UUID.fromString("55555555-6666-7777-8888-999999999999");

    private LocalDate hoy;

    @BeforeEach
    void setUp() {
        hoy = LocalDate.now();
        
        repository.deleteAll();
        jugadorRepository.deleteAll();

        // Crear jugadores
        JugadorJPAEntity jugador1 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_1)
                .nombre("Bukayo")
                .apellido("Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(178)
                .peso(70)
                .fechaActualizacion(LocalDate.now())
                .build();

        JugadorJPAEntity jugador2 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_2)
                .nombre("Erling")
                .apellido("Haaland")
                .fechaNacimiento(LocalDate.of(2000, 7, 21))
                .nacionalidad(Nacion.NORUEGA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(194)
                .peso(88)
                .fechaActualizacion(LocalDate.now())
                .build();

        JugadorJPAEntity jugador3 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_3)
                .nombre("Cole")
                .apellido("Palmer")
                .fechaNacimiento(LocalDate.of(2002, 5, 6))
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(185)
                .peso(75)
                .fechaActualizacion(LocalDate.now())
                .build();

        jugadorRepository.saveAll(List.of(jugador1, jugador2, jugador3));

        // Crear lesiones con fechas relativas al día de hoy
        // Lesión 1: Activa (fechaInicio = hace 2 meses, fechaFin = en 2 meses)
        LesionJPAEntity lesion1 = LesionJPAEntity.builder()
                .idLesion(ID_LESION_1)
                .jugador(jugador1)
                .nombreLesion("Rotura de ligamento cruzado anterior")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(hoy.minusMonths(2))
                .fechaFin(hoy.plusMonths(2))
                .curada(false)
                .build();

        // Lesión 2: Ya curada (fechaInicio = hace 4 meses, fechaFin = hace 1 mes)
        LesionJPAEntity lesion2 = LesionJPAEntity.builder()
                .idLesion(ID_LESION_2)
                .jugador(jugador2)
                .nombreLesion("Sobrecarga muscular en el cuádriceps")
                .gravedad(Gravedad.LEVE)
                .fechaInicio(hoy.minusMonths(4))
                .fechaFin(hoy.minusMonths(1))
                .curada(true)
                .build();

        // Lesión 3: Activa sin fecha fin (fechaInicio = hace 1 mes, fechaFin = null)
        LesionJPAEntity lesion3 = LesionJPAEntity.builder()
                .idLesion(ID_LESION_3)
                .jugador(jugador2)
                .nombreLesion("Fractura de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(hoy.minusMonths(1))
                .fechaFin(null)
                .curada(false)
                .build();

        repository.saveAll(List.of(lesion1, lesion2, lesion3));
    }

    @Test
    @DisplayName("findById: debe encontrar la lesión por ID")
    void testFindById() {
        Optional<Lesion> lesion = adapter.findById(ID_LESION_1);
        assertThat(lesion).isPresent();
        assertThat(lesion.get().getNombreLesion()).isEqualTo("Rotura de ligamento cruzado anterior");
        assertThat(lesion.get().getGravedad()).isEqualTo(Gravedad.GRAVE);
        assertThat(lesion.get().isCurada()).isFalse();
    }

    @Test
    @DisplayName("findByJugador: debe buscar lesiones por jugador")
    void testFindByJugador() {
        // Jugador 2 tiene 2 lesiones
        List<Lesion> lesiones = adapter.findByJugador(ID_JUGADOR_2);
        assertThat(lesiones).hasSize(2);
        assertThat(lesiones)
                .extracting(Lesion::getNombreLesion)
                .containsExactlyInAnyOrder("Sobrecarga muscular en el cuádriceps", "Fractura de tobillo");
        
        // Jugador 1 tiene 1 lesión
        List<Lesion> lesionesJugador1 = adapter.findByJugador(ID_JUGADOR_1);
        assertThat(lesionesJugador1).hasSize(1);
        assertThat(lesionesJugador1.get(0).getNombreLesion()).isEqualTo("Rotura de ligamento cruzado anterior");
        
        // Jugador 3 no tiene lesiones
        List<Lesion> lesionesJugador3 = adapter.findByJugador(ID_JUGADOR_3);
        assertThat(lesionesJugador3).isEmpty();
    }

    @Test
    @DisplayName("findActivasByJugador: debe buscar lesiones activas de un jugador")
    void testFindActivasByJugador() {
        // Jugador 2 tiene 1 lesión activa (Fractura de tobillo)
        List<Lesion> lesionesActivas = adapter.findActivasByJugador(ID_JUGADOR_2);
        assertThat(lesionesActivas).hasSize(1);
        assertThat(lesionesActivas.get(0).getNombreLesion()).isEqualTo("Fractura de tobillo");
        assertThat(lesionesActivas.get(0).isCurada()).isFalse();
        assertThat(lesionesActivas.get(0).getFechaFin()).isNull();
        
        // Jugador 1 tiene 1 lesión activa (Rotura de ligamento)
        List<Lesion> lesionesActivasJugador1 = adapter.findActivasByJugador(ID_JUGADOR_1);
        assertThat(lesionesActivasJugador1).hasSize(1);
        assertThat(lesionesActivasJugador1.get(0).getNombreLesion()).isEqualTo("Rotura de ligamento cruzado anterior");
        assertThat(lesionesActivasJugador1.get(0).isCurada()).isFalse();
        
        // Jugador 3 no tiene lesiones
        List<Lesion> lesionesActivasJugador3 = adapter.findActivasByJugador(ID_JUGADOR_3);
        assertThat(lesionesActivasJugador3).isEmpty();
    }

    @Test
    @DisplayName("findByGravedad: debe buscar lesiones por gravedad")
    void testFindByGravedad() {
        // Buscar lesiones GRAVE
        List<Lesion> graves = adapter.findByGravedad(Gravedad.GRAVE);
        assertThat(graves).hasSize(1);
        assertThat(graves.get(0).getNombreLesion()).isEqualTo("Rotura de ligamento cruzado anterior");

        // Buscar lesiones LEVE
        List<Lesion> leves = adapter.findByGravedad(Gravedad.LEVE);
        assertThat(leves).hasSize(1);
        assertThat(leves.get(0).getNombreLesion()).isEqualTo("Sobrecarga muscular en el cuádriceps");

        // Buscar lesiones MODERADA
        List<Lesion> moderadas = adapter.findByGravedad(Gravedad.MODERADA);
        assertThat(moderadas).hasSize(1);
        assertThat(moderadas.get(0).getNombreLesion()).isEqualTo("Fractura de tobillo");
    }

    @Test
    @DisplayName("findActivas: debe buscar todas las lesiones activas")
    void testFindActivas() {
        List<Lesion> activas = adapter.findActivas();
        // lesion1 está activa (fechaFin en futuro), lesion2 está curada, lesion3 está activa (fechaFin null)
        assertThat(activas).hasSize(2);
        assertThat(activas)
                .extracting(Lesion::getNombreLesion)
                .containsExactlyInAnyOrder(
                        "Rotura de ligamento cruzado anterior",
                        "Fractura de tobillo"
                );
        assertThat(activas)
                .extracting(Lesion::isCurada)
                .allMatch(curada -> curada == false);
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia")
    void testExistsById() {
        assertThat(adapter.existsById(ID_LESION_2)).isTrue();
        assertThat(adapter.existsById(ID_LESION_1)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar una lesión")
    void testDeleteById() {
        // Verificar que existe
        assertThat(adapter.existsById(ID_LESION_3)).isTrue();
        
        // Eliminar
        adapter.deleteById(ID_LESION_3);
        
        // Verificar que ya no existe
        assertThat(adapter.existsById(ID_LESION_3)).isFalse();
        
        // Verificar que el jugador 2 ahora tiene solo 1 lesión
        List<Lesion> lesionesJugador2 = adapter.findByJugador(ID_JUGADOR_2);
        assertThat(lesionesJugador2).hasSize(1);
        assertThat(lesionesJugador2.get(0).getNombreLesion()).isEqualTo("Sobrecarga muscular en el cuádriceps");
        
        // Verificar que las otras lesiones siguen existiendo
        assertThat(adapter.existsById(ID_LESION_1)).isTrue();
        assertThat(adapter.existsById(ID_LESION_2)).isTrue();
    }

    @Test
    @DisplayName("save: debe guardar una nueva lesión para un jugador existente")
    void testSave() {
        // Crear una nueva lesión para el jugador 3
        UUID nuevoId = UUID.randomUUID();
        Lesion nuevaLesion = Lesion.builder()
                .idLesion(nuevoId)
                .nombreLesion("Nueva lesión de prueba")
                .gravedad(Gravedad.LEVE)
                .fechaInicio(hoy)
                .fechaFin(hoy.plusWeeks(2))
                .curada(false)
                .build();

        Lesion guardado = adapter.save(nuevaLesion);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdLesion()).isEqualTo(nuevoId);
        assertThat(guardado.getNombreLesion()).isEqualTo("Nueva lesión de prueba");

        // Verificar que existe por ID
        Optional<Lesion> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombreLesion()).isEqualTo("Nueva lesión de prueba");
    }

    @Test
    @DisplayName("save: debe actualizar una lesión existente")
    void testUpdate() {
        // Obtener la lesión existente
        Optional<Lesion> lesionOptional = adapter.findById(ID_LESION_1);
        assertThat(lesionOptional).isPresent();
        
        Lesion lesion = lesionOptional.get();
        assertThat(lesion.getNombreLesion()).isEqualTo("Rotura de ligamento cruzado anterior");
        assertThat(lesion.isCurada()).isFalse();
        
        // Actualizar la lesión
        Lesion lesionActualizada = Lesion.builder()
                .idLesion(lesion.getIdLesion())
                .nombreLesion("Rotura de ligamento cruzado anterior - En recuperación")
                .gravedad(lesion.getGravedad())
                .fechaInicio(lesion.getFechaInicio())
                .fechaFin(hoy.plusMonths(2))
                .curada(false)
                .build();
        
        Lesion guardado = adapter.save(lesionActualizada);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdLesion()).isEqualTo(ID_LESION_1);
        assertThat(guardado.getNombreLesion()).isEqualTo("Rotura de ligamento cruzado anterior - En recuperación");
        
        // Verificar en la BD
        Optional<Lesion> encontrado = adapter.findById(ID_LESION_1);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombreLesion()).isEqualTo("Rotura de ligamento cruzado anterior - En recuperación");
        assertThat(encontrado.get().getFechaFin()).isAfter(hoy);
    }

    @Test
    @DisplayName("findByJugador: debe retornar lista vacía cuando el jugador no existe")
    void testFindByJugador_JugadorNoExiste() {
        UUID idJugadorInexistente = UUID.randomUUID();
        List<Lesion> lesiones = adapter.findByJugador(idJugadorInexistente);
        assertThat(lesiones).isEmpty();
    }

    @Test
    @DisplayName("findActivasByJugador: debe retornar lista vacía cuando el jugador no existe")
    void testFindActivasByJugador_JugadorNoExiste() {
        UUID idJugadorInexistente = UUID.randomUUID();
        List<Lesion> lesiones = adapter.findActivasByJugador(idJugadorInexistente);
        assertThat(lesiones).isEmpty();
    }

    @Test
    @DisplayName("findActivas: debe retornar lista vacía cuando no hay lesiones activas")
    void testFindActivas_Vacio() {
        // Primero eliminamos todas las lesiones
        repository.deleteAll();
        
        List<Lesion> activas = adapter.findActivas();
        assertThat(activas).isEmpty();
    }

    @Test
    @DisplayName("save: debe guardar una lesión sin jugador")
    void testSaveSinJugador() {
        UUID nuevoId = UUID.randomUUID();
        Lesion nuevaLesion = Lesion.builder()
                .idLesion(nuevoId)
                .nombreLesion("Lesión sin jugador")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(hoy)
                .curada(false)
                .build();

        Lesion guardado = adapter.save(nuevaLesion);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdLesion()).isEqualTo(nuevoId);
        
        // Verificar que existe
        Optional<Lesion> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombreLesion()).isEqualTo("Lesión sin jugador");
    }
}