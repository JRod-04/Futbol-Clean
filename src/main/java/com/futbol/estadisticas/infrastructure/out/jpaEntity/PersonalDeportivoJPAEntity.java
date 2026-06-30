package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "personal_deportivo")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_personal", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class PersonalDeportivoJPAEntity {

    @Id
    @Column(name = "id_personal", nullable = false, updatable = false)
    private UUID idPersonal;
 
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
 
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;
 
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "nacionalidad", length = 50)
    private Nacion nacionalidad;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_personal", length = 30,insertable = false, updatable = false)
    private TipoPersonal tipoPersonal;
 
    // Contratos del personal
    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ContratoJPAEntity> contratos = new ArrayList<>();
}
