package com.futbol.estadisticas.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;

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
public class Contrato {

    @EqualsAndHashCode.Include
    private UUID idContrato;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Double sueldo;
    private Club club;
    private PersonalDeportivo personal;

    @Builder.Default
    private EstadoContrato estado = EstadoContrato.ACTIVO;   

    
    public boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return estado == EstadoContrato.ACTIVO && 
               fechaInicio != null && 
               fechaFin != null &&
               ahora.isAfter(fechaInicio) && 
               ahora.isBefore(fechaFin);
    }
    
    public void finalizar() {
        if (this.estado == EstadoContrato.FINALIZADO) {
            throw new IllegalStateException("El contrato ya está finalizado");
        }
        this.estado = EstadoContrato.FINALIZADO;
        this.fechaFin = LocalDateTime.now();
    }
    
    public void renovar(int mesesAdicionales) {
        if (mesesAdicionales <= 0) {
            throw new IllegalArgumentException("Los meses deben ser positivos");
        }
        if (this.estado != EstadoContrato.ACTIVO) {
            throw new IllegalStateException("No se puede renovar un contrato no activo");
        }
        this.fechaFin = this.fechaFin.plusMonths(mesesAdicionales);
    }


        public void rescindir() {
        if (this.estado == EstadoContrato.FINALIZADO) {
            throw new IllegalStateException("No se puede Rescindir un contrato Finalizado");
        }
        this.estado = EstadoContrato.RESCINDIDO;
        this.fechaFin = LocalDateTime.now();
   }
}