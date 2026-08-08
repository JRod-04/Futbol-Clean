package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "tecnicos")
@DiscriminatorValue("TECNICO")
@PrimaryKeyJoinColumn(name = "id_personal")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class TecnicoJPAEntity extends PersonalDeportivoJPAEntity {
    
    @Column(name = "estilo_juego", length = 200)
    private String estiloJuego;
 
    @Column(name = "alineacion_favorita", length = 20)
    private String alineacionFavorita;
 
    // Club actual — relación inversa (EquipoJPAEntity es dueño de la FK)
    @OneToOne(mappedBy = "tecnicoActual", fetch = FetchType.LAZY)
    private EquipoJPAEntity equipoActual;
}
