package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "arbitros")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ArbitroJPAEntity {

    @Id
    @Column(name = "id_arbitro", nullable = false, updatable = false)
    private UUID idArbitro;
 
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
 
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;
 
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
 
    // Partidos arbitrados — relación inversa (PartidoJPAEntity es dueño)
    @OneToMany(mappedBy = "arbitro", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PartidoJPAEntity> partidos = new ArrayList<>();
}
