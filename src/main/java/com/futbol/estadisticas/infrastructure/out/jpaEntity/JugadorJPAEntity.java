package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.futbol.estadisticas.domain.model.enums.JuegoPies;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "jugadores")
@DiscriminatorValue("JUGADOR")
@PrimaryKeyJoinColumn(name = "id_personal")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@SuperBuilder
public class JugadorJPAEntity extends PersonalDeportivoJPAEntity{

    @Enumerated(EnumType.STRING)
    @Column(name = "pie_habil", length = 20)
    private JuegoPies pieHabil;
 
    @Column(name = "altura")
    private Integer altura;
 
    @Column(name = "peso")
    private Integer peso;
 
    @Column(name = "fecha_actualizacion")
    private LocalDate fechaActualizacion;
 
    // Datos deportivos — relación OneToOne, DatosDeportivos es dueño
    @OneToOne(mappedBy = "jugador", cascade = CascadeType.ALL,
              orphanRemoval = true, fetch = FetchType.LAZY)
    private DatosDeportivosJPAEntity datosDeportivos;
 
    // Lesiones del jugador
    @OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LesionJPAEntity> lesiones = new ArrayList<>();
}
