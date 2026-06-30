package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.JugadorRepositoryAdapter;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ContratoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.DatosDeportivosJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional  // <-- IMPORTANTE: Mantiene la sesión abierta
class JugadorJPARepositoryTest extends PostgresTestContainerConfig {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private JugadorRepositoryAdapter adapter;

    @Autowired
    private JugadorJPARepository repository;

    @Autowired
    private ClubJPARepository clubRepository;

    @Autowired
    private EstadioJPARepository estadioRepository;

    @Autowired
    private ContratoJPARepository contratoRepository;

    @Autowired
    private DatosDeportivosJPARepository datosDeportivosRepository;

    private static final UUID ID_JUGADOR_1 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private static final UUID ID_JUGADOR_2 = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID ID_JUGADOR_3 = UUID.fromString("22222222-3333-4444-5555-666666666666");
    private static final UUID ID_CLUB_1 = UUID.fromString("44444444-5555-6666-7777-888888888888");
    private static final UUID ID_ESTADIO = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        // Limpiar en orden inverso
        contratoRepository.deleteAll();
        datosDeportivosRepository.deleteAll();
        repository.deleteAll();
        clubRepository.deleteAll();
        estadioRepository.deleteAll();

        // Crear estadio
        EstadioJPAEntity estadio = EstadioJPAEntity.builder()
                .idEstadio(ID_ESTADIO)
                .nombre("Emirates Stadium")
                .direccion("Highbury, Londres")
                .capacidad(60704)
                .fechaFundacion(LocalDate.of(2006, 7, 22))
                .build();
        estadioRepository.save(estadio);

        // Crear club
        ClubJPAEntity club = ClubJPAEntity.builder()
                .idEquipo(ID_CLUB_1)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .fechaFundacion(LocalDate.of(1886, 12, 1))
                .estadio(estadio)
                .build();
        clubRepository.save(club);

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

        repository.saveAll(List.of(jugador1, jugador2, jugador3));

        // Crear datos deportivos
        DatosDeportivosJPAEntity datos1 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .jugador(jugador1)
                .posicion(PosicionJugador.EXTREMO_DERECHO)
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(85000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();

        DatosDeportivosJPAEntity datos2 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .jugador(jugador2)
                .posicion(PosicionJugador.DELANTERO)
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(200000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();

        DatosDeportivosJPAEntity datos3 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .jugador(jugador3)
                .posicion(PosicionJugador.MEDIOCENTRO)
                .estadoJugador(EstadoJugador.LESIONADO)
                .valorMercado(70000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();

        datosDeportivosRepository.saveAll(List.of(datos1, datos2, datos3));

        // Crear contratos
        ContratoJPAEntity contrato1 = ContratoJPAEntity.builder()
                .idContrato(UUID.randomUUID())
                .personal(jugador1)
                .club(club)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .sueldo(250000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();

        ContratoJPAEntity contrato2 = ContratoJPAEntity.builder()
                .idContrato(UUID.randomUUID())
                .personal(jugador2)
                .club(club)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .sueldo(375000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();

        ContratoJPAEntity contrato3 = ContratoJPAEntity.builder()
                .idContrato(UUID.randomUUID())
                .personal(jugador3)
                .club(club)
                .fechaInicio(LocalDateTime.now().minusMonths(12))
                .fechaFin(LocalDateTime.now().minusMonths(1))
                .sueldo(150000.0)
                .estado(EstadoContrato.FINALIZADO)
                .build();

        contratoRepository.saveAll(List.of(contrato1, contrato2, contrato3));
    }

    @Test
    @DisplayName("findById: debe encontrar el jugador por ID")
    void testFindById() {
        Optional<Jugador> jugador = adapter.findById(ID_JUGADOR_1);
        assertThat(jugador).isPresent();
        assertThat(jugador.get().getNombre()).isEqualTo("Bukayo");
        assertThat(jugador.get().getApellido()).isEqualTo("Saka");
    }

    @Test
    @DisplayName("findAll: debe retornar todos los jugadores")
    void testFindAll() {
        List<Jugador> todos = adapter.findAll();
        assertThat(todos).hasSize(3);
    }

    @Test
    @DisplayName("findByClub: debe buscar jugadores por club")
    void testFindByClub() {
        List<Jugador> jugadores = adapter.findByClub(ID_CLUB_1);
        assertThat(jugadores).hasSize(2);
        assertThat(jugadores)
                .extracting(Jugador::getApellido)
                .containsExactlyInAnyOrder("Saka", "Haaland");
    }

    @Test
    @DisplayName("findByEstado: debe buscar jugadores por estado")
    void testFindByEstado() {
        List<Jugador> titulares = adapter.findByEstado(EstadoJugador.TITULAR);
        assertThat(titulares).hasSize(2);
        assertThat(titulares)
                .extracting(Jugador::getApellido)
                .containsExactlyInAnyOrder("Saka", "Haaland");

        List<Jugador> lesionados = adapter.findByEstado(EstadoJugador.LESIONADO);
        assertThat(lesionados).hasSize(1);
        assertThat(lesionados.get(0).getApellido()).isEqualTo("Palmer");
    }

    @Test
    @DisplayName("findByPosicion: debe buscar jugadores por posición")
    void testFindByPosicion() {
        List<Jugador> delanteros = adapter.findByPosicion(PosicionJugador.DELANTERO);
        assertThat(delanteros).hasSize(1);
        assertThat(delanteros.get(0).getApellido()).isEqualTo("Haaland");
    }

    @Test
    @DisplayName("findDisponibles: debe buscar jugadores disponibles")
    void testFindDisponibles() {
        List<Jugador> disponibles = adapter.findDisponibles();
        assertThat(disponibles).hasSize(2);
        assertThat(disponibles)
                .extracting(Jugador::getApellido)
                .containsExactlyInAnyOrder("Saka", "Haaland");
    }

    @Test
    @DisplayName("findLesionados: debe buscar jugadores lesionados")
    void testFindLesionados() {
        List<Jugador> lesionados = adapter.findLesionados();
        assertThat(lesionados).hasSize(1);
        assertThat(lesionados.get(0).getApellido()).isEqualTo("Palmer");
    }

    @Test
    @DisplayName("existsById: debe confirmar existencia")
    void testExistsById() {
        assertThat(adapter.existsById(ID_JUGADOR_1)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar un jugador")
    void testDeleteById() {
        // Primero eliminar contratos y datos deportivos del jugador 3
        contratoRepository.deleteAll(contratoRepository.findByPersonalIdPersonal(ID_JUGADOR_3));
        datosDeportivosRepository.deleteById(
            datosDeportivosRepository.findByJugadorIdPersonal(ID_JUGADOR_3).get().getIdHistorialDeportivo()
        );
        
        assertThat(adapter.existsById(ID_JUGADOR_3)).isTrue();
        adapter.deleteById(ID_JUGADOR_3);
        assertThat(adapter.existsById(ID_JUGADOR_3)).isFalse();
        List<Jugador> todos = adapter.findAll();
        assertThat(todos).hasSize(2);
    }

    @Test
    @DisplayName("save: debe guardar un nuevo jugador")
    void testSave() {
        UUID nuevoId = UUID.randomUUID();
        Jugador nuevoJugador = Jugador.builder()
                .idPersonal(nuevoId)
                .nombre("Nuevo")
                .apellido("Jugador")
                .fechaNacimiento(LocalDate.of(1995, 5, 5))
                .nacionalidad(Nacion.ESPANA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .build();

        Jugador guardado = adapter.save(nuevoJugador);
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdPersonal()).isEqualTo(nuevoId);

        Optional<Jugador> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(adapter.findAll()).hasSize(4);
    }
}