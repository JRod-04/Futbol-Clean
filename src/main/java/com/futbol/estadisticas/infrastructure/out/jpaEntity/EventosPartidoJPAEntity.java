package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.TipoEvento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "eventos_partido")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventosPartidoJPAEntity {

    @Id
    @Column(name = "id_evento", nullable = false, updatable = false)
    private UUID idEvento;
 
    @Column(name = "minuto")
    private LocalTime minuto;
 
    @Column(name = "descripcion", length = 500)
    private String descripcion;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", length = 40)
    private TipoEvento tipoEvento;
 
    // FK partido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partido", nullable = false,
                foreignKey = @ForeignKey(name = "fk_evento_partido"))
    private PartidoJPAEntity partido;
 
    // FK personal (opcional — hay eventos sin jugador, ej. inicio de partido)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personal",
                foreignKey = @ForeignKey(name = "fk_evento_personal"))
    private PersonalDeportivoJPAEntity personal;
 
    // FK equipo favorecido (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo_favorecido",
                foreignKey = @ForeignKey(name = "fk_evento_equipo"))
    private ClubJPAEntity equipoFavorecido;
}
