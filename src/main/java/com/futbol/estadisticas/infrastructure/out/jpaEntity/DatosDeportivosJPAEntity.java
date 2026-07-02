package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "datos_deportivos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DatosDeportivosJPAEntity {

    @Id
    @Column(name = "id_historial_deportivo", nullable = false, updatable = false)
    private UUID idHistorialDeportivo;
 
    @Column(name = "fecha_actualizacion")
    private LocalDate fechaActualizacion;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_jugador", length = 30)
    private EstadoJugador estadoJugador;
 
    @Column(name = "valor_mercado")
    private Double valorMercado;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "jugador_posiciones",
        joinColumns = @JoinColumn(name = "id_historial")
    )
    @Column(name = "posicion")
    @Builder.Default
    private List<PosicionJugador> posiciones = new ArrayList<>();

    @Column(name = "dorsal")
    private Integer dorsal;

   
    // Dueño de la FK hacia jugadores
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jugador", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_datos_deportivos_jugador"))
    private JugadorJPAEntity jugador;
}
