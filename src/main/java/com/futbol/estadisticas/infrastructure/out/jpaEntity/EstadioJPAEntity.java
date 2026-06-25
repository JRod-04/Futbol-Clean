package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "estadios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EstadioJPAEntity {
    @Id
    @Column(name = "id_estadio", nullable = false, updatable = false)
    private UUID idEstadio;
 
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
 
    @Column(name = "direccion", length = 300)
    private String direccion;
 
    @Column(name = "capacidad")
    private Integer capacidad;
 
    @Column(name = "fecha_fundacion")
    private LocalDate fechaFundacion;
 
    // Relación inversa — Club es dueño de la FK
    @OneToOne(mappedBy = "estadio", fetch = FetchType.LAZY)
    private ClubJPAEntity clubPrincipal;
}
