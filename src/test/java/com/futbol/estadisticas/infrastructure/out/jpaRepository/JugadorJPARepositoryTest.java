package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.PostgresTestContainerConfig;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter.JugadorRepositoryAdapter;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EquipoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ContratoJPAEntity;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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
    private EquipoJPARepository clubRepository;

    private static final UUID ID_JUGADOR_1 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private static final UUID ID_JUGADOR_2 = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID ID_JUGADOR_3 = UUID.fromString("22222222-3333-4444-5555-666666666666");
    private static final UUID ID_JUGADOR_4 = UUID.fromString("33333333-4444-5555-6666-777777777777");

    private static final UUID ID_CLUB = UUID.fromString("44444444-5555-6666-7777-888888888888");

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        clubRepository.deleteAll();

        // Crear club
        EquipoJPAEntity club = EquipoJPAEntity.builder()
                .idEquipo(ID_CLUB)
                .nombre("FC Barcelona")
                .nombreCorto("Barça")
                .fechaFundacion(LocalDate.of(1899, 11, 29))
                .build();
        clubRepository.save(club);

        // JUGADOR 1: TITULAR - EXTREMO DERECHO - CONTRATO ACTIVO
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

        DatosDeportivosJPAEntity datos1 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .jugador(jugador1)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.EXTREMO_DERECHO)))
                .dorsal(7)
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(85000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();
        jugador1.setDatosDeportivos(datos1);

        ContratoJPAEntity contrato1 = ContratoJPAEntity.builder()
                .idContrato(UUID.randomUUID())
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .sueldo(5000000.0)
                .estado(EstadoContrato.ACTIVO)
                .personal(jugador1)
                .equipo(club)
                .build();
        jugador1.setContratos(List.of(contrato1));

        // JUGADOR 2: SUPLENTE - MEDIOCAMPISTA - CONTRATO ACTIVO
        JugadorJPAEntity jugador2 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_2)
                .nombre("Andrés")
                .apellido("Iniesta")
                .fechaNacimiento(LocalDate.of(1984, 5, 11))
                .nacionalidad(Nacion.ESPAÑA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(171)
                .peso(68)
                .fechaActualizacion(LocalDate.now())
                .build();

        DatosDeportivosJPAEntity datos2 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .jugador(jugador2)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.MEDIOCENTRO)))
                .dorsal(8)
                .estadoJugador(EstadoJugador.SUPLENTE)
                .valorMercado(8000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();
        jugador2.setDatosDeportivos(datos2);

        ContratoJPAEntity contrato2 = ContratoJPAEntity.builder()
                .idContrato(UUID.randomUUID())
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .sueldo(3000000.0)
                .estado(EstadoContrato.ACTIVO)
                .personal(jugador2)
                .equipo(club)
                .build();
        jugador2.setContratos(List.of(contrato2));

        // JUGADOR 3: LESIONADO - DEFENSA - CONTRATO ACTIVO
        JugadorJPAEntity jugador3 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_3)
                .nombre("Gerard")
                .apellido("Piqué")
                .fechaNacimiento(LocalDate.of(1987, 2, 2))
                .nacionalidad(Nacion.ESPAÑA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(194)
                .peso(85)
                .fechaActualizacion(LocalDate.now())
                .build();

        DatosDeportivosJPAEntity datos3 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .jugador(jugador3)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.CENTRAL)))
                .dorsal(3)
                .estadoJugador(EstadoJugador.LESIONADO)
                .valorMercado(5000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();
        jugador3.setDatosDeportivos(datos3);

        ContratoJPAEntity contrato3 = ContratoJPAEntity.builder()
                .idContrato(UUID.randomUUID())
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .sueldo(4000000.0)
                .estado(EstadoContrato.ACTIVO)
                .personal(jugador3)
                .equipo(club)
                .build();
        jugador3.setContratos(List.of(contrato3));

        // JUGADOR 4: TITULAR - DELANTERO - SIN CONTRATO
        JugadorJPAEntity jugador4 = JugadorJPAEntity.builder()
                .idPersonal(ID_JUGADOR_4)
                .nombre("Kylian")
                .apellido("Mbappé")
                .fechaNacimiento(LocalDate.of(1998, 12, 20))
                .nacionalidad(Nacion.FRANCIA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(182)
                .peso(75)
                .fechaActualizacion(LocalDate.now())
                .build();

        DatosDeportivosJPAEntity datos4 = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .jugador(jugador4)
                .posiciones(new ArrayList<>(List.of(PosicionJugador.DELANTERO)))
                .dorsal(7)
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(200000000.0)
                .fechaActualizacion(LocalDate.now())
                .build();
        jugador4.setDatosDeportivos(datos4);

        repository.saveAll(List.of(jugador1, jugador2, jugador3, jugador4));
    }

    @Test
    @DisplayName("findByClub: debe encontrar jugadores con contrato activo en un club")
    void testFindByEquipo() {
        List<Jugador> jugadores = adapter.findByEquipo(ID_CLUB);
        
        assertThat(jugadores).hasSize(3);
        assertThat(jugadores)
                .extracting(Jugador::getNombre)
                .containsExactlyInAnyOrder("Bukayo", "Andrés", "Gerard");
        assertThat(jugadores)
                .extracting(Jugador::getApellido)
                .containsExactlyInAnyOrder("Saka", "Iniesta", "Piqué");
        assertThat(jugadores)
                .extracting(j -> j.getDatosDeportivos().getEstadoJugador())
                .containsExactlyInAnyOrder(EstadoJugador.TITULAR, EstadoJugador.SUPLENTE, EstadoJugador.LESIONADO);
    }

    @Test
    @DisplayName("findByEstado: debe encontrar jugadores por estado")
    void testFindByEstado() {
        // Buscar titulares
        List<Jugador> titulares = adapter.findByEstado(EstadoJugador.TITULAR);
        assertThat(titulares).hasSize(2);
        assertThat(titulares)
                .extracting(Jugador::getNombre)
                .containsExactlyInAnyOrder("Bukayo", "Kylian");
        assertThat(titulares)
                .allMatch(j -> j.getDatosDeportivos().getEstadoJugador() == EstadoJugador.TITULAR);

        // Buscar suplentes
        List<Jugador> suplentes = adapter.findByEstado(EstadoJugador.SUPLENTE);
        assertThat(suplentes).hasSize(1);
        assertThat(suplentes.get(0).getNombre()).isEqualTo("Andrés");
        assertThat(suplentes.get(0).getDatosDeportivos().getEstadoJugador()).isEqualTo(EstadoJugador.SUPLENTE);

        // Buscar lesionados
        List<Jugador> lesionados = adapter.findByEstado(EstadoJugador.LESIONADO);
        assertThat(lesionados).hasSize(1);
        assertThat(lesionados.get(0).getNombre()).isEqualTo("Gerard");
        assertThat(lesionados.get(0).getDatosDeportivos().getEstadoJugador()).isEqualTo(EstadoJugador.LESIONADO);
    }

    @Test
    @DisplayName("findByPosicion: debe encontrar jugadores por posición")
    void testFindByPosicion() {
        // Buscar extremos derechos
        List<Jugador> extremosDerechos = adapter.findByPosicion(PosicionJugador.EXTREMO_DERECHO);
        assertThat(extremosDerechos).hasSize(1);
        assertThat(extremosDerechos.get(0).getNombre()).isEqualTo("Bukayo");
        assertThat(extremosDerechos.get(0).getDatosDeportivos().getPosiciones())
                .contains(PosicionJugador.EXTREMO_DERECHO);

        // Buscar mediocentros
        List<Jugador> mediocentros = adapter.findByPosicion(PosicionJugador.MEDIOCENTRO);
        assertThat(mediocentros).hasSize(1);
        assertThat(mediocentros.get(0).getNombre()).isEqualTo("Andrés");

        // Buscar centrales
        List<Jugador> centrales = adapter.findByPosicion(PosicionJugador.CENTRAL);
        assertThat(centrales).hasSize(1);
        assertThat(centrales.get(0).getNombre()).isEqualTo("Gerard");

        // Buscar delanteros
        List<Jugador> delanteros = adapter.findByPosicion(PosicionJugador.DELANTERO);
        assertThat(delanteros).hasSize(1);
        assertThat(delanteros.get(0).getNombre()).isEqualTo("Kylian");

        // Buscar posición sin jugadores
        List<Jugador> porteros = adapter.findByPosicion(PosicionJugador.PORTERO);
        assertThat(porteros).isEmpty();
    }

    @Test
    @DisplayName("findDisponibles: debe encontrar jugadores disponibles (TITULAR o SUPLENTE)")
    void testFindDisponibles() {
        List<Jugador> disponibles = adapter.findDisponibles();
        
        assertThat(disponibles).hasSize(3);
        assertThat(disponibles)
                .extracting(Jugador::getNombre)
                .containsExactlyInAnyOrder("Bukayo", "Andrés", "Kylian");
        assertThat(disponibles)
                .allMatch(j -> j.getDatosDeportivos().getEstadoJugador() == EstadoJugador.TITULAR ||
                              j.getDatosDeportivos().getEstadoJugador() == EstadoJugador.SUPLENTE);
        assertThat(disponibles)
                .extracting(j -> j.getDatosDeportivos().getEstadoJugador())
                .doesNotContain(EstadoJugador.LESIONADO);
    }

    @Test
    @DisplayName("findLesionados: debe encontrar jugadores lesionados")
    void testFindLesionados() {
        List<Jugador> lesionados = adapter.findLesionados();
        
        assertThat(lesionados).hasSize(1);
        assertThat(lesionados.get(0).getNombre()).isEqualTo("Gerard");
        assertThat(lesionados.get(0).getApellido()).isEqualTo("Piqué");
        assertThat(lesionados.get(0).getDatosDeportivos().getEstadoJugador()).isEqualTo(EstadoJugador.LESIONADO);
    }

    @Test
    @DisplayName("findById: debe encontrar un jugador por su ID")
    void testFindById() {
        Optional<Jugador> jugador = adapter.findById(ID_JUGADOR_1);
        
        assertThat(jugador).isPresent();
        assertThat(jugador.get().getNombre()).isEqualTo("Bukayo");
        assertThat(jugador.get().getApellido()).isEqualTo("Saka");
        assertThat(jugador.get().getNacionalidad()).isEqualTo(Nacion.INGLATERRA);
        assertThat(jugador.get().getDatosDeportivos()).isNotNull();
        assertThat(jugador.get().getDatosDeportivos().getDorsal()).isEqualTo(7);
        assertThat(jugador.get().getDatosDeportivos().getEstadoJugador()).isEqualTo(EstadoJugador.TITULAR);
    }

    @Test
    @DisplayName("findAll: debe encontrar todos los jugadores")
    void testFindAll() {
        List<Jugador> todos = adapter.findAll();
        
        assertThat(todos).hasSize(4);
        assertThat(todos)
                .extracting(Jugador::getNombre)
                .containsExactlyInAnyOrder("Bukayo", "Andrés", "Gerard", "Kylian");
    }

    @Test
    @DisplayName("existsById: debe verificar si un jugador existe")
    void testExistsById() {
        assertThat(adapter.existsById(ID_JUGADOR_1)).isTrue();
        assertThat(adapter.existsById(ID_JUGADOR_2)).isTrue();
        assertThat(adapter.existsById(ID_JUGADOR_3)).isTrue();
        assertThat(adapter.existsById(ID_JUGADOR_4)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById: debe eliminar un jugador")
    void testDeleteById() {
        assertThat(adapter.existsById(ID_JUGADOR_4)).isTrue();
        
        adapter.deleteById(ID_JUGADOR_4);
        
        assertThat(adapter.existsById(ID_JUGADOR_4)).isFalse();
        assertThat(adapter.existsById(ID_JUGADOR_1)).isTrue();
        assertThat(adapter.existsById(ID_JUGADOR_2)).isTrue();
        assertThat(adapter.existsById(ID_JUGADOR_3)).isTrue();
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
                .nacionalidad(Nacion.ESPAÑA)
                .pieHabil(JuegoPies.DERECHO)
                .altura(180)
                .peso(75)
                .fechaActualizacion(LocalDate.now())
                .build();

        Jugador guardado = adapter.save(nuevoJugador);
        
        assertThat(guardado).isNotNull();
        assertThat(guardado.getIdPersonal()).isEqualTo(nuevoId);
        assertThat(guardado.getNombre()).isEqualTo("Nuevo");
        assertThat(guardado.getApellido()).isEqualTo("Jugador");
        assertThat(guardado.getNacionalidad()).isEqualTo(Nacion.ESPAÑA);
        
        assertThat(adapter.existsById(nuevoId)).isTrue();
        
        Optional<Jugador> encontrado = adapter.findById(nuevoId);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Nuevo");
    }

    @Test
    @DisplayName("findById: debe retornar Optional.empty cuando el jugador no existe")
    void testFindById_NoExiste() {
        Optional<Jugador> jugador = adapter.findById(UUID.randomUUID());
        assertThat(jugador).isEmpty();
    }

    @Test
    @DisplayName("findByClub: debe retornar lista vacía cuando el club no tiene jugadores con contrato activo")
    void testFindByEquipo_SinJugadores() {
        UUID clubSinJugadores = UUID.randomUUID();
        List<Jugador> jugadores = adapter.findByEquipo(clubSinJugadores);
        assertThat(jugadores).isEmpty();
    }
}