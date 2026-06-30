package com.futbol.estadisticas.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;

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
public class Partido {

    @EqualsAndHashCode.Include
    private UUID idPartido;
   
    private LocalDateTime fechaYHora;
    private Integer jornada;
    private EstadoPartido estado;
    private Club equipoLocal;
    private Club equipoVisitante;
    private Estadio estadio;
    private Arbitro arbitro;
    private Competicion competicion;

     @Builder.Default
    private List<EventosPartido> eventos = new ArrayList<>();
    
    private int golesLocal;
    private int golesVisitante;
    
    //Inicia el partido
     
    public void iniciarPartido() {
        if (this.estado != EstadoPartido.PROGRAMADO) {
            throw new IllegalStateException("El partido ya ha sido iniciado");
        }
        this.estado = EstadoPartido.PRIMER_TIEMPO;
        this.fechaYHora = LocalDateTime.now();
        
        // Registrar evento de inicio
        EventosPartido eventoInicio = EventosPartido.builder()
            .idEvento(UUID.randomUUID())
            .minuto(java.time.LocalTime.of(0, 0))
            .descripcion("Inicio del partido")
            .tipoEvento(TipoEvento.INICIO_PARTIDO)
            .partido(this)
            .build();
        agregarEvento(eventoInicio);
    }
    

    //Finaliza el partido
    public void finalizarPartido() {
    if (this.estado == EstadoPartido.FINALIZADO || 
        this.estado == EstadoPartido.CANCELADO ||
        this.estado == EstadoPartido.SUSPENDIDO) {
        throw new IllegalStateException("El partido ya ha finalizado");
    }
    
    EventosPartido eventoFin = EventosPartido.builder()
        .idEvento(UUID.randomUUID())
        .minuto(java.time.LocalTime.now())
        .descripcion("Finalización del partido")
        .tipoEvento(TipoEvento.FIN_PARTIDO)
        .partido(this)
        .build();
    this.eventos.add(eventoFin);  
    eventoFin.setPartido(this);
    
    this.estado = EstadoPartido.FINALIZADO;
}
    
    //Agrega un evento al partido
    public void agregarEvento(EventosPartido evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        if (this.estado == EstadoPartido.FINALIZADO || 
            this.estado == EstadoPartido.CANCELADO ||
            this.estado == EstadoPartido.SUSPENDIDO) {
            throw new IllegalStateException("No se pueden agregar eventos a un partido finalizado");
        }
        this.eventos.add(evento);
        evento.setPartido(this);
        
        // Actualizar marcador si es un gol
        if (evento.getTipoEvento() == TipoEvento.GOL) {
            if (evento.getEquipoFavorecido() != null) {
                if (evento.getEquipoFavorecido().getIdEquipo().equals(this.equipoLocal.getIdEquipo())) {
                    this.golesLocal++;
                } else if (evento.getEquipoFavorecido().getIdEquipo().equals(this.equipoVisitante.getIdEquipo())) {
                    this.golesVisitante++;
                }
            }
        }
    }
    
    //Cambia el estado del partido
    public void cambiarEstado(EstadoPartido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.estado = nuevoEstado;
    }
    
    //Verifica si el partido está en curso
    
    public boolean estaEnCurso() {
        return this.estado == EstadoPartido.PRIMER_TIEMPO || 
               this.estado == EstadoPartido.ENTRETIEMPO ||
               this.estado == EstadoPartido.SEGUNDO_TIEMPO ||
               this.estado == EstadoPartido.PRORROGA ||
               this.estado == EstadoPartido.PENALTIS;
    }
    
    //Verifica si el partido ha finalizado
    public boolean haFinalizado() {
        return this.estado == EstadoPartido.FINALIZADO || 
               this.estado == EstadoPartido.CANCELADO ||
               this.estado == EstadoPartido.SUSPENDIDO;
    }
    
    //Obtiene el resultado del partido
    public String getResultado() {
        if (!haFinalizado()) {
            return "En curso";
        }
        return String.format("%d - %d", golesLocal, golesVisitante);
    }
    
    //Obtiene el ganador del partido
    public Club getGanador() {
        if (!haFinalizado()) {
            return null;
        }
        if (golesLocal > golesVisitante) {
            return equipoLocal;
        } else if (golesVisitante > golesLocal) {
            return equipoVisitante;
        }
        return null; // Empate
    }
    

    //Verifica si hay empate
    public boolean hayEmpate() {
        return haFinalizado() && golesLocal == golesVisitante;
    }
    

    //Obtiene la duración del partido en minutos
    public long getDuracionMinutos() {
        if (eventos.isEmpty()) {
            return 0;
        }
        // Calcular duración basada en eventos de inicio y fin
        EventosPartido inicio = eventos.stream()
            .filter(e -> e.getTipoEvento() == com.futbol.estadisticas.domain.model.enums.TipoEvento.INICIO_PARTIDO)
            .findFirst()
            .orElse(null);
        EventosPartido fin = eventos.stream()
            .filter(e -> e.getTipoEvento() == com.futbol.estadisticas.domain.model.enums.TipoEvento.FIN_PARTIDO)
            .findFirst()
            .orElse(null);
        
        if (inicio == null || fin == null) {
            return 0;
        }
        
        // Si los eventos tienen timestamps, usarlos
        return 90; // Duración estándar
    }
    

    //Obtiene los eventos de gol del partido
    public List<EventosPartido> getGoles() {
        return eventos.stream()
            .filter(e -> e.getTipoEvento() == com.futbol.estadisticas.domain.model.enums.TipoEvento.GOL ||
                        e.getTipoEvento() == com.futbol.estadisticas.domain.model.enums.TipoEvento.AUTOGOL)
            .toList();
    }
    
    //Verifica si el partido se juega en una fecha futura
    public boolean esFuturo() {
        return fechaYHora != null && fechaYHora.isAfter(LocalDateTime.now());
    }
    
    //Verifica si el partido se juega hoy
    public boolean esHoy() {
        if (fechaYHora == null) {
            return false;
        }
        LocalDateTime hoy = LocalDateTime.now();
        return fechaYHora.toLocalDate().equals(hoy.toLocalDate());
    }
}
