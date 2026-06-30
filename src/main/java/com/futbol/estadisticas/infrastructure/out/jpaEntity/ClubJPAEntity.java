package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clubes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ClubJPAEntity {
    @Id
    @Column(name = "id_equipo", nullable = false, updatable = false)
    private UUID idEquipo;
 
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
 
    @Column(name = "nombre_corto", nullable = false, length = 10)
    private String nombreCorto;
 
    @Column(name = "fecha_fundacion")
    private LocalDate fechaFundacion;
 
    // Un club tiene un estadio (opcional). La FK queda en esta tabla.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_estadio", foreignKey = @ForeignKey(name = "fk_club_estadio"))
    private EstadioJPAEntity estadio;
 
    // Técnico actual (opcional)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tecnico_actual", foreignKey = @ForeignKey(name = "fk_club_tecnico_actual"))
    private TecnicoJPAEntity tecnicoActual;
 
    // Contratos del club (uno a muchos)
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ContratoJPAEntity> contratos = new ArrayList<>();
 
    // Partidos como local
    @OneToMany(mappedBy = "equipoLocal", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PartidoJPAEntity> partidosLocal = new ArrayList<>();
 
    // Partidos como visitante
    @OneToMany(mappedBy = "equipoVisitante", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PartidoJPAEntity> partidosVisitante = new ArrayList<>();
}
