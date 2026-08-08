package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "competiciones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CompeticionJPAEntity {

    @Id
    @Column(name = "id_competicion", nullable = false, updatable = false)
    private UUID idCompeticion;
 
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
 
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
 
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @ManyToOne
    @JoinColumn(name = "id_equipo_ganador")
    private EquipoJPAEntity equipoGanador;

    // Partidos de esta competición (relación inversa)
    @OneToMany(mappedBy = "competicion", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PartidoJPAEntity> partidos = new ArrayList<>();
}
