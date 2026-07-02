package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.DatosDeportivosRepositoryAdapter;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.DatosDeportivosJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DatosDeportivosJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private DatosDeportivosRepositoryAdapter adapter;

    @Autowired
    private DatosDeportivosJPARepository repository;

    @Autowired
    private JugadorJPARepository jugadorRepository;

    private static final UUID ID_JUGADOR_1 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private static final UUID ID_JUGADOR_2 = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID ID_JUGADOR_3 = UUID.fromString("22222222-3333-4444-5555-666666666666");

    private static final UUID ID_DATOS_1 = UUID.fromString("33333333-4444-5555-6666-777777777777");
    private static final UUID ID_DATOS_2 = UUID.fromString("44444444-5555-6666-7777-888888888888");
    private static final UUID ID_DATOS_3 = UUID.fromString("55555555-6666-7777-8888-999999999999");

    @BeforeEach
    void setUp() {
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

        // Crear datos deportivos con lista de posiciones y dorsal
        DatosDeportivosJPAEntity datos1 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(ID_DATOS_1)
                .jugador(jugador1)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.EXTREMO_DERECHO)))  // ✅ Lista
                .dorsal(7)  // ✅ Dorsal
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(85000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();

        DatosDeportivosJPAEntity datos2 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(ID_DATOS_2)
                .jugador(jugador2)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.DELANTERO)))  // ✅ Lista
                .dorsal(9)  // ✅ Dorsal
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(200000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();

        DatosDeportivosJPAEntity datos3 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(ID_DATOS_3)
                .jugador(jugador3)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.MEDIOCENTRO)))  // ✅ Lista
                .dorsal(20)  // ✅ Dorsal
                .estadoJugador(EstadoJugador.LESIONADO)
                .valorMercado(70000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();

        repository.saveAll(List.of(datos1, datos2, datos3));
    }

    @Test
    @DisplayName("findById: debe encontrar los datos deportivos por ID")
    void testFindById() {
        Optional<DatosDeportivos> datos = adapter.findById(ID_DATOS_1);
        assertThat(datos).isPresent();
        assertThat(datos.get().getPosicionActual()).isEqualTo(PosicionJugador.EXTREMO_DERECHO);  // ✅ Usar getPosicionActual()
        assertThat(datos.get().getPosiciones()).hasSize(1);  // ✅ Verificar lista
        assertThat(datos.get().getDorsal()).isEqualTo(7);  // ✅ Verificar dorsal
        assertThat(datos.get().getValorMercado()).isEqualTo(85000000.0);
        assertThat(datos.get().getEstadoJugador()).isEqualTo(EstadoJugador.TITULAR);
    }

    @Test
    @DisplayName("findByJugador: debe encontrar datos deportivos por jugador")
    void testFindByJugador() {
        Optional<DatosDeportivos> datos = adapter.findByJugador(ID_JUGADOR_1);
        assertThat(datos).isPresent();
        assertThat(datos.get().getPosicionActual()).isEqualTo(PosicionJugador.EXTREMO_DERECHO);
        assertThat(datos.get().getDorsal()).isEqualTo(7);
        assertThat(datos.get().getValorMercado()).isEqualTo(85000000.0);
    }

    @Test
    @DisplayName("findByEstado: debe buscar datos deportivos por estado del jugador")
    void testFindByEstado() {
        // Buscar titulares
        List<DatosDeportivos> titulares = adapter.findByEstado(EstadoJugador.TITULAR);
        assertThat(titulares).hasSize(2);
        assertThat(titulares)
                .extracting(DatosDeportivos::getValorMercado)
                .containsExactlyInAnyOrder(85000000.0, 200000000.0);
        assertThat(titulares)
                .extracting(DatosDeportivos::getEstadoJugador)
                .allMatch(estado -> estado == EstadoJugador.TITULAR);

        // Buscar lesionados
        List<DatosDeportivos> lesionados = adapter.findByEstado(EstadoJugador.LESIONADO);
        assertThat(lesionados).hasSize(1);
        assertThat(lesionados.get(0).getValorMercado()).isEqualTo(70000000.0);
        assertThat(lesionados.get(0).getEstadoJugador()).isEqualTo(EstadoJugador.LESIONADO);

        // Buscar suplentes (no debería haber)
        List<DatosDeportivos> suplentes = adapter.findByEstado(EstadoJugador.SUPLENTE);
        assertThat(suplentes).isEmpty();
    }

    @Test
    @DisplayName("existsByJugador: debe verificar existencia de datos deportivos por jugador")
    void testExistsByJugador() {
        assertThat(adapter.existsByJugador(ID_JUGADOR_1)).isTrue();
        assertThat(adapter.existsByJugador(ID_JUGADOR_2)).isTrue();
        assertThat(adapter.existsByJugador(ID_JUGADOR_3)).isTrue();
        assertThat(adapter.existsByJugador(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar datos deportivos por ID de jugador")
    void testDeleteById() {
        assertThat(adapter.existsByJugador(ID_JUGADOR_3)).isTrue();
        
        adapter.deleteById(ID_DATOS_3);
        
        boolean existsAfterDelete = adapter.existsByJugador(ID_JUGADOR_3);
        assertThat(existsAfterDelete).isFalse();
        
        assertThat(adapter.existsByJugador(ID_JUGADOR_1)).isTrue();
        assertThat(adapter.existsByJugador(ID_JUGADOR_2)).isTrue();
    }

    @Test
    @DisplayName("save: debe guardar nuevos datos deportivos para un jugador existente")
    void testSave() {
        // Crear nuevo jugador
        UUID nuevoJugadorId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        JugadorJPAEntity nuevoJugador = JugadorJPAEntity.builder()
                .idPersonal(nuevoJugadorId)
                .nombre("Nuevo")
                .apellido("Jugador")
                .fechaNacimiento(LocalDate.of(1995, 5, 5))
                .nacionalidad(Nacion.ESPAÑA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .fechaActualizacion(LocalDate.now())
                .build();
        jugadorRepository.save(nuevoJugador);

        // Crear datos deportivos con lista de posiciones y dorsal
        Jugador jugadorDomain = Jugador.builder()
                .idPersonal(nuevoJugadorId)
                .nombre("Nuevo")
                .apellido("Jugador")
                .build();

        UUID nuevoId = UUID.randomUUID();
        DatosDeportivos nuevosDatos = DatosDeportivos.builder()
                .idHistorialDeportivo(nuevoId)
                .jugador(jugadorDomain)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.CENTRAL)))  // ✅ Lista con posición inicial
                .dorsal(10)  // ✅ Dorsal
                .estadoJugador(EstadoJugador.SUPLENTE)
                .valorMercado(50000000.0)
                .build();

        DatosDeportivos guardado = adapter.save(nuevosDatos);
        
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdHistorialDeportivo()).isEqualTo(nuevoId);
        assertThat(guardado.getPosicionActual()).isEqualTo(PosicionJugador.CENTRAL);
        assertThat(guardado.getPosiciones()).hasSize(1);
        assertThat(guardado.getDorsal()).isEqualTo(10);
        assertThat(guardado.getValorMercado()).isEqualTo(50000000.0);
        assertThat(guardado.getEstadoJugador()).isEqualTo(EstadoJugador.SUPLENTE);

        assertThat(adapter.existsByJugador(nuevoJugadorId)).isTrue();
        
        Optional<DatosDeportivos> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getValorMercado()).isEqualTo(50000000.0);
        assertThat(encontrado.get().getDorsal()).isEqualTo(10);
    }

    @Test
    @DisplayName("save: debe actualizar datos deportivos existentes")
    void testUpdate() {
        assertThat(adapter.existsByJugador(ID_JUGADOR_1)).isTrue();
        
        Optional<DatosDeportivos> datosOptional = adapter.findById(ID_DATOS_1);
        assertThat(datosOptional).isPresent();
        
        DatosDeportivos datos = datosOptional.get();
        assertThat(datos.getValorMercado()).isEqualTo(85000000.0);
        assertThat(datos.getDorsal()).isEqualTo(7);
        
        Jugador jugador = Jugador.builder()
                .idPersonal(ID_JUGADOR_1)
                .build();
        
        // Agregar nueva posición y actualizar dorsal
        DatosDeportivos datosActualizados = DatosDeportivos.builder()
                .idHistorialDeportivo(datos.getIdHistorialDeportivo())
                .jugador(jugador)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.EXTREMO_DERECHO, PosicionJugador.DELANTERO)))  // ✅ Agregar nueva posición
                .dorsal(11)  // ✅ Nuevo dorsal
                .estadoJugador(datos.getEstadoJugador())
                .valorMercado(90000000.0)
                .build();
        
        DatosDeportivos guardado = adapter.save(datosActualizados);
        
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdHistorialDeportivo()).isEqualTo(ID_DATOS_1);
        assertThat(guardado.getPosiciones()).hasSize(2);
        assertThat(guardado.getPosicionActual()).isEqualTo(PosicionJugador.DELANTERO);
        assertThat(guardado.getDorsal()).isEqualTo(11);
        assertThat(guardado.getValorMercado()).isEqualTo(90000000.0);
        
        Optional<DatosDeportivos> encontrado = adapter.findById(ID_DATOS_1);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getDorsal()).isEqualTo(11);
        assertThat(encontrado.get().getValorMercado()).isEqualTo(90000000.0);
        
        assertThat(adapter.existsByJugador(ID_JUGADOR_1)).isTrue();
    }

    @Test
    @DisplayName("findByJugador: debe retornar Optional.empty cuando el jugador no tiene datos deportivos")
    void testFindByJugador_NoExiste() {
        UUID idJugadorSinDatos = UUID.randomUUID();
        
        JugadorJPAEntity jugadorSinDatos = JugadorJPAEntity.builder()
                .idPersonal(idJugadorSinDatos)
                .nombre("Sin")
                .apellido("Datos")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .nacionalidad(Nacion.ESPAÑA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .fechaActualizacion(LocalDate.now())
                .build();
        jugadorRepository.save(jugadorSinDatos);
        
        Optional<DatosDeportivos> datos = adapter.findByJugador(idJugadorSinDatos);
        assertThat(datos).isEmpty();
    }

    @Test
    @DisplayName("existsByJugador: debe retornar false cuando el jugador no tiene datos deportivos")
    void testExistsByJugador_NoExiste() {
        UUID idJugadorSinDatos = UUID.randomUUID();
        JugadorJPAEntity jugadorSinDatos = JugadorJPAEntity.builder()
                .idPersonal(idJugadorSinDatos)
                .nombre("Sin")
                .apellido("Datos")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .nacionalidad(Nacion.ESPAÑA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .fechaActualizacion(LocalDate.now())
                .build();
        jugadorRepository.save(jugadorSinDatos);
        
        assertThat(adapter.existsByJugador(idJugadorSinDatos)).isFalse();
        assertThat(adapter.existsByJugador(UUID.randomUUID())).isFalse();
    }
}