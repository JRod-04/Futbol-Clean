package com.futbol.estadisticas.infrastructure.out.jpaEntity;

import java.time.LocalDate;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

import jakarta.persistence.Column;
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
 
    @Enumerated(EnumType.STRING)
    @Column(name = "posicion", length = 40)
    private PosicionJugador posicion;
 
    // Dueño de la FK hacia jugadores
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jugador", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_datos_deportivos_jugador"))
    private JugadorJPAEntity jugador;
}
