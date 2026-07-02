package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DatosDeportivos {

    @EqualsAndHashCode.Include
    private UUID idHistorialDeportivo;

    private LocalDate fechaActualizacion;
    private EstadoJugador estadoJugador;
    private Double valorMercado;
    private Integer dorsal;
    private Jugador jugador;
     @Builder.Default
    private List<PosicionJugador> posiciones = new ArrayList<>();




    public void actualizarEstado(EstadoJugador nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        
        if (this.estadoJugador == EstadoJugador.RETIRADO && 
            nuevoEstado != EstadoJugador.RETIRADO) {
            throw new IllegalStateException("Un jugador retirado no puede cambiar de estado");
        }
        
        this.estadoJugador = nuevoEstado;
        this.fechaActualizacion = LocalDate.now();
    }
    

    //Actualiza el valor de mercado del jugador
    public void actualizarValorMercado(Double nuevoValor) {
        if (nuevoValor == null || nuevoValor < 0) {
            throw new IllegalArgumentException("El valor de mercado debe ser positivo");
        }
        this.valorMercado = nuevoValor;
        this.fechaActualizacion = LocalDate.now();
    }
    

    //Cambia la posición del jugador
  public void agregarPosicion(PosicionJugador nuevaPosicion) {
        if (nuevaPosicion == null) {
            throw new IllegalArgumentException("La posición no puede ser nula");
        }
        if (!posiciones.contains(nuevaPosicion)) {
            posiciones.add(nuevaPosicion);
        }
        this.fechaActualizacion = LocalDate.now();
    }

    public PosicionJugador getPosicionActual() {
        return posiciones.isEmpty() ? null : posiciones.get(posiciones.size() - 1);
    }

    // ── PARA DORSAL ──
    
    public void actualizarDorsal(Integer nuevoDorsal) {
        if (nuevoDorsal == null) {
            throw new IllegalArgumentException("El dorsal no puede ser nulo");
        }
        if (nuevoDorsal <= 0) {
            throw new IllegalArgumentException("El dorsal debe ser positivo");
        }
        this.dorsal = nuevoDorsal;
        this.fechaActualizacion = LocalDate.now();
    }
    
    
    //Verifica si el jugador es titular
    public boolean esTitular() {
        return this.estadoJugador == EstadoJugador.TITULAR;
    }
    

    //Verifica si el jugador es suplente
    public boolean esSuplente() {
        return this.estadoJugador == EstadoJugador.SUPLENTE;
    }
    
    //Verifica si el jugador está lesionado
    public boolean estaLesionado() {
        return this.estadoJugador == EstadoJugador.LESIONADO;
    }
    
    //Verifica si el jugador está disponible para jugar
    public boolean estaDisponible() {
        return this.estadoJugador == EstadoJugador.TITULAR || 
               this.estadoJugador == EstadoJugador.SUPLENTE;
    }
    

    //Obtiene el valor de mercado en millones de euros
    public double getValorMercadoEnMillones() {
        if (valorMercado == null) {
            return 0;
        }
        return valorMercado / 1_000_000.0;
    }
 
    
    //Promueve al jugador a titular
    public void promoverATitular() {
        if (this.estadoJugador == EstadoJugador.RETIRADO) {
            throw new IllegalStateException("Un jugador retirado no puede ser titular");
        }
        this.estadoJugador = EstadoJugador.TITULAR;
        this.fechaActualizacion = LocalDate.now();
    }
    

    //Cambia al jugador a suplente
    public void cambiarASuplente() {
        if (this.estadoJugador == EstadoJugador.RETIRADO) {
            throw new IllegalStateException("Un jugador retirado no puede ser suplente");
        }
        this.estadoJugador = EstadoJugador.SUPLENTE;
        this.fechaActualizacion = LocalDate.now();
    }
}
