package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;

import com.futbol.estadisticas.domain.model.enums.FaseTorneo;
import com.futbol.estadisticas.domain.model.enums.JornadaPartido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partidos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PartidoJPAEntity {

    @Id
    @Column(name = "id_partido", nullable = false, updatable = false)
    private UUID idPartido;
 
    @Column(name = "fecha_y_hora")
    private LocalDateTime fechaYHora;
 

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30)
    private EstadoPartido estado;
 
    @Column(name = "goles_local", nullable = false)
    private int golesLocal;
 
    @Column(name = "goles_visitante", nullable = false)
    private int golesVisitante;

    @Enumerated(EnumType.STRING)
    @Column(name = "fase")
    private FaseTorneo fase;

    @Enumerated(EnumType.STRING)
    @Column(name = "jornada_torneo")
    private JornadaPartido jornadaTorneo;
 
    // FK equipo local
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo_local", nullable = false,
                foreignKey = @ForeignKey(name = "fk_partido_local"))
    private EquipoJPAEntity equipoLocal;
 
    // FK equipo visitante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo_visitante", nullable = false,
                foreignKey = @ForeignKey(name = "fk_partido_visitante"))
    private EquipoJPAEntity equipoVisitante;
 
    // FK estadio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estadio",
                foreignKey = @ForeignKey(name = "fk_partido_estadio"))
    private EstadioJPAEntity estadio;
 
    // FK árbitro
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_arbitro",
                foreignKey = @ForeignKey(name = "fk_partido_arbitro"))
    private ArbitroJPAEntity arbitro;
 
    // FK competición
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competicion",
                foreignKey = @ForeignKey(name = "fk_partido_competicion"))
    private CompeticionJPAEntity competicion;
 
    // Eventos del partido (relación inversa)
    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EventosPartidoJPAEntity> eventos = new ArrayList<>();
}
