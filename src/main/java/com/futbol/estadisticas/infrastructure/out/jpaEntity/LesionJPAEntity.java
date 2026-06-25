package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDate;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Gravedad;

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
@Table(name = "lesiones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LesionJPAEntity {
    @Id
    @Column(name = "id_lesion", nullable = false, updatable = false)
    private UUID idLesion;
 
    @Column(name = "nombre_lesion", nullable = false, length = 200)
    private String nombreLesion;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "gravedad", length = 20)
    private Gravedad gravedad;
 
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;
 
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
 
    @Column(name = "curada", nullable = false)
    private boolean curada;
 
    // FK hacia jugador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jugador", nullable = false,
                foreignKey = @ForeignKey(name = "fk_lesion_jugador"))
    private JugadorJPAEntity jugador;
}
