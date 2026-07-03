package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.ClasificacionResponse;
import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.EquipoClasificacion;
import com.futbol.estadisticas.application.port.out.CompeticionRepositoryPort;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClasificacionServiceTest {

    @Mock
    private PartidoRepositoryPort partidoRepository;

    @Mock
    private CompeticionRepositoryPort competicionRepository;

    @InjectMocks
    private ClasificacionService clasificacionService;

    private UUID idCompeticion;
    private UUID idClubLocal;
    private UUID idClubVisitante;
    private UUID idClubLocal2;
    private UUID idClubVisitante2;
    private Club clubLocal;
    private Club clubVisitante;
    private Club clubLocal2;
    private Club clubVisitante2;
    private Competicion competicion;

    @BeforeEach
    void setUp() {
        idCompeticion = UUID.randomUUID();
        idClubLocal = UUID.randomUUID();
        idClubVisitante = UUID.randomUUID();
        idClubLocal2 = UUID.randomUUID();
        idClubVisitante2 = UUID.randomUUID();

        clubLocal = Club.builder()
                .idEquipo(idClubLocal)
                .nombre("Arsenal FC")
                .build();

        clubVisitante = Club.builder()
                .idEquipo(idClubVisitante)
                .nombre("Manchester City")
                .build();

        clubLocal2 = Club.builder()
                .idEquipo(idClubLocal2)
                .nombre("Chelsea FC")
                .build();

        clubVisitante2 = Club.builder()
                .idEquipo(idClubVisitante2)
                .nombre("Liverpool FC")
                .build();

        competicion = Competicion.builder()
                .idCompeticion(idCompeticion)
                .nombre("Premier League Test")
                .fechaInicio(LocalDateTime.now().minusMonths(1))
                .fechaFin(LocalDateTime.now().plusMonths(5))
                .build();
    }

    @Test
    @DisplayName("obtenerTabla: debe calcular la tabla correctamente con partidos finalizados")
    void testObtenerTabla_PartidosFinalizados() {

        when(competicionRepository.findById(idCompeticion))
                .thenReturn(Optional.of(competicion));

        // Partido 1: Arsenal 2-1 Manchester City
        Partido partido1 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(clubLocal)
                .equipoVisitante(clubVisitante)
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(2)
                .golesVisitante(1)
                .build();

        // Partido 2: Chelsea 1-1 Liverpool
        Partido partido2 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(clubLocal2)
                .equipoVisitante(clubVisitante2)
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(1)
                .golesVisitante(1)
                .build();

        when(partidoRepository.findClasificacion(idCompeticion))
                .thenReturn(List.of(partido1, partido2));

        ClasificacionResponse tabla = clasificacionService.obtenerTabla(idCompeticion);

        assertThat(tabla).isNotNull();
        assertThat(tabla.idCompeticion()).isEqualTo(idCompeticion);
        assertThat(tabla.tabla()).hasSize(4);

        // Verificar Arsenal (3 puntos)
        EquipoClasificacion arsenal = tabla.tabla().stream()
                .filter(e -> e.idClub().equals(idClubLocal))
                .findFirst()
                .orElseThrow();
        assertThat(arsenal.partidosJugados()).isEqualTo(1);
        assertThat(arsenal.ganados()).isEqualTo(1);
        assertThat(arsenal.empatados()).isEqualTo(0);
        assertThat(arsenal.perdidos()).isEqualTo(0);
        assertThat(arsenal.golesFavor()).isEqualTo(2);
        assertThat(arsenal.golesContra()).isEqualTo(1);
        assertThat(arsenal.diferenciaGoles()).isEqualTo(1);
        assertThat(arsenal.puntos()).isEqualTo(3);

        // Verificar Chelsea (1 punto)
        EquipoClasificacion chelsea = tabla.tabla().stream()
                .filter(e -> e.idClub().equals(idClubLocal2))
                .findFirst()
                .orElseThrow();
        assertThat(chelsea.partidosJugados()).isEqualTo(1);
        assertThat(chelsea.ganados()).isEqualTo(0);
        assertThat(chelsea.empatados()).isEqualTo(1);
        assertThat(chelsea.perdidos()).isEqualTo(0);
        assertThat(chelsea.golesFavor()).isEqualTo(1);
        assertThat(chelsea.golesContra()).isEqualTo(1);
        assertThat(chelsea.diferenciaGoles()).isEqualTo(0);
        assertThat(chelsea.puntos()).isEqualTo(1);

        // Verificar Liverpool (1 punto)
        EquipoClasificacion liverpool = tabla.tabla().stream()
                .filter(e -> e.idClub().equals(idClubVisitante2))
                .findFirst()
                .orElseThrow();
        assertThat(liverpool.partidosJugados()).isEqualTo(1);
        assertThat(liverpool.ganados()).isEqualTo(0);
        assertThat(liverpool.empatados()).isEqualTo(1);
        assertThat(liverpool.perdidos()).isEqualTo(0);
        assertThat(liverpool.golesFavor()).isEqualTo(1);
        assertThat(liverpool.golesContra()).isEqualTo(1);
        assertThat(liverpool.diferenciaGoles()).isEqualTo(0);
        assertThat(liverpool.puntos()).isEqualTo(1);

        // Verificar Manchester City (0 puntos)
        EquipoClasificacion city = tabla.tabla().stream()
                .filter(e -> e.idClub().equals(idClubVisitante))
                .findFirst()
                .orElseThrow();
        assertThat(city.partidosJugados()).isEqualTo(1);
        assertThat(city.ganados()).isEqualTo(0);
        assertThat(city.empatados()).isEqualTo(0);
        assertThat(city.perdidos()).isEqualTo(1);
        assertThat(city.golesFavor()).isEqualTo(1);
        assertThat(city.golesContra()).isEqualTo(2);
        assertThat(city.diferenciaGoles()).isEqualTo(-1);
        assertThat(city.puntos()).isEqualTo(0);
    }

    @Test
    @DisplayName("obtenerTabla: debe ordenar correctamente por puntos y diferencia de goles")
    void testObtenerTabla_Ordenamiento() {

        when(competicionRepository.findById(idCompeticion))
                .thenReturn(Optional.of(competicion));

        // Partido 1: Arsenal 3-0 Manchester City
        Partido partido1 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(clubLocal)
                .equipoVisitante(clubVisitante)
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(3)
                .golesVisitante(0)
                .build();

        // Partido 2: Chelsea 2-0 Liverpool
        Partido partido2 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(clubLocal2)
                .equipoVisitante(clubVisitante2)
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(2)
                .golesVisitante(0)
                .build();

        when(partidoRepository.findClasificacion(idCompeticion))
                .thenReturn(List.of(partido1, partido2));

        ClasificacionResponse tabla = clasificacionService.obtenerTabla(idCompeticion);

        assertThat(tabla.tabla()).hasSize(4);

        // Verificar orden: Arsenal primero (+3 dif), Chelsea segundo (+2 dif)
        List<EquipoClasificacion> ordenados = tabla.tabla();
        assertThat(ordenados.get(0).idClub()).isEqualTo(idClubLocal);
        assertThat(ordenados.get(0).diferenciaGoles()).isEqualTo(3);
        assertThat(ordenados.get(1).idClub()).isEqualTo(idClubLocal2);
        assertThat(ordenados.get(1).diferenciaGoles()).isEqualTo(2);
    }

    @Test
    @DisplayName("obtenerTabla: debe incluir partidos EN CURSO con puntos parciales")
    void testObtenerTabla_PartidosEnCurso() {

        when(competicionRepository.findById(idCompeticion))
                .thenReturn(Optional.of(competicion));

        // Partido en curso: Arsenal 2-0 Manchester City
        Partido partido = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(clubLocal)
                .equipoVisitante(clubVisitante)
                .estado(EstadoPartido.SEGUNDO_TIEMPO)
                .golesLocal(2)
                .golesVisitante(0)
                .build();

        when(partidoRepository.findClasificacion(idCompeticion))
                .thenReturn(List.of(partido));

        ClasificacionResponse tabla = clasificacionService.obtenerTabla(idCompeticion);

        assertThat(tabla.tabla()).hasSize(2);

        // Arsenal debe tener 3 puntos (ganando en curso)
        EquipoClasificacion arsenal = tabla.tabla().stream()
                .filter(e -> e.idClub().equals(idClubLocal))
                .findFirst()
                .orElseThrow();
        assertThat(arsenal.partidosJugados()).isEqualTo(1);
        assertThat(arsenal.puntos()).isEqualTo(3);
        assertThat(arsenal.ganados()).isEqualTo(0);
        assertThat(arsenal.empatados()).isEqualTo(0);
        assertThat(arsenal.perdidos()).isEqualTo(0);

        EquipoClasificacion city = tabla.tabla().stream()
                .filter(e -> e.idClub().equals(idClubVisitante))
                .findFirst()
                .orElseThrow();
        assertThat(city.partidosJugados()).isEqualTo(1);
        assertThat(city.puntos()).isEqualTo(0);
    }

    @Test
    @DisplayName("obtenerTabla: partido EN CURSO con empate - puntos parciales")
    void testObtenerTabla_PartidoEnCursoEmpate() {
        // Given
        when(competicionRepository.findById(idCompeticion))
                .thenReturn(Optional.of(competicion));

        Partido partido = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(clubLocal)
                .equipoVisitante(clubVisitante)
                .estado(EstadoPartido.PRIMER_TIEMPO)
                .golesLocal(1)
                .golesVisitante(1)
                .build();

        when(partidoRepository.findClasificacion(idCompeticion))
                .thenReturn(List.of(partido));

        ClasificacionResponse tabla = clasificacionService.obtenerTabla(idCompeticion);

        assertThat(tabla.tabla()).hasSize(2);

        EquipoClasificacion local = tabla.tabla().stream()
                .filter(e -> e.idClub().equals(idClubLocal))
                .findFirst()
                .orElseThrow();
        assertThat(local.puntos()).isEqualTo(1);

        EquipoClasificacion visitante = tabla.tabla().stream()
                .filter(e -> e.idClub().equals(idClubVisitante))
                .findFirst()
                .orElseThrow();
        assertThat(visitante.puntos()).isEqualTo(1);
    }

    @Test
    @DisplayName("obtenerTabla: debe retornar tabla vacía si no hay partidos")
    void testObtenerTabla_SinPartidos() {

        when(competicionRepository.findById(idCompeticion))
                .thenReturn(Optional.of(competicion));
        when(partidoRepository.findClasificacion(idCompeticion))
                .thenReturn(List.of());

        ClasificacionResponse tabla = clasificacionService.obtenerTabla(idCompeticion);

        assertThat(tabla).isNotNull();
        assertThat(tabla.idCompeticion()).isEqualTo(idCompeticion);
        assertThat(tabla.tabla()).isEmpty();
    }

    @Test
    @DisplayName("obtenerTabla: debe lanzar excepción si la competición no existe")
    void testObtenerTabla_CompeticionNoExiste() {
        UUID idCompeticionInexistente = UUID.randomUUID();
        when(competicionRepository.findById(idCompeticionInexistente))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clasificacionService.obtenerTabla(idCompeticionInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Competición no encontrada con id: " + idCompeticionInexistente);
    }

    @Test
    @DisplayName("obtenerTabla: debe manejar múltiples partidos del mismo club")
    void testObtenerTabla_MultiplesPartidosMismoClub() {
        // Given
        when(competicionRepository.findById(idCompeticion))
                .thenReturn(Optional.of(competicion));

        // Arsenal juega 2 partidos: 2-1 y 0-0
        Partido partido1 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(clubLocal)
                .equipoVisitante(clubVisitante)
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(2)
                .golesVisitante(1)
                .build();

        Partido partido2 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(clubLocal2)
                .equipoVisitante(clubLocal)
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(0)
                .golesVisitante(0)
                .build();

        when(partidoRepository.findClasificacion(idCompeticion))
                .thenReturn(List.of(partido1, partido2));


        ClasificacionResponse tabla = clasificacionService.obtenerTabla(idCompeticion);

        EquipoClasificacion arsenal = tabla.tabla().stream()
                .filter(e -> e.idClub().equals(idClubLocal))
                .findFirst()
                .orElseThrow();

        assertThat(arsenal.partidosJugados()).isEqualTo(2);
        assertThat(arsenal.ganados()).isEqualTo(1);
        assertThat(arsenal.empatados()).isEqualTo(1);
        assertThat(arsenal.perdidos()).isEqualTo(0);
        assertThat(arsenal.golesFavor()).isEqualTo(2);
        assertThat(arsenal.golesContra()).isEqualTo(1);
        assertThat(arsenal.diferenciaGoles()).isEqualTo(1);
        assertThat(arsenal.puntos()).isEqualTo(4); // 3 + 1
    }
}