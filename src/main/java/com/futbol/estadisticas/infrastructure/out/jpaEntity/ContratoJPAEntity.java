package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;

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
@Table(name = "contratos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ContratoJPAEntity {

    @Id
    @Column(name = "id_contrato", nullable = false, updatable = false)
    private UUID idContrato;
 
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
 
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;
 
    @Column(name = "sueldo", nullable = false)
    private Double sueldo;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoContrato estado;
 
    // FK al personal (dueño)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personal", nullable = false,
                foreignKey = @ForeignKey(name = "fk_contrato_personal"))
    private PersonalDeportivoJPAEntity personal;
 
    // FK al club (dueño)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_club", nullable = false,
                foreignKey = @ForeignKey(name = "fk_contrato_club"))
    private ClubJPAEntity club;
}
