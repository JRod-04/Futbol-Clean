package com.futbol.estadisticas.domain.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.FaseTorneo;
import com.futbol.estadisticas.domain.model.enums.JornadaPartido;
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
    private EstadoPartido estado;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private Estadio estadio;
    private Arbitro arbitro;
    private Competicion competicion;
    private JornadaPartido jornada;
    private FaseTorneo fase;

     @Builder.Default
    private List<EventosPartido> eventos = new ArrayList<>();
    
    private int golesLocal;
    private int golesVisitante;


    public void iniciarPartido() {
        if (this.estado != EstadoPartido.PROGRAMADO) {
            throw new IllegalStateException("El partido ya ha sido iniciado");
        }

        validarAlineacionesCompletas();

        this.estado = EstadoPartido.PRIMER_TIEMPO;

        EventosPartido eventoInicio = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .minuto(java.time.LocalTime.of(0, 0))
                .descripcion("Inicio del partido")
                .tipoEvento(TipoEvento.INICIO_PARTIDO)
                .partido(this)
                .build();
        agregarEvento(eventoInicio);

        registrarEventosTitulares();
    }

    private void validarAlineacionesCompletas() {
        if (this.equipoLocal == null || this.equipoVisitante == null) {
            throw new IllegalStateException("El partido debe tener equipo local y visitante asignados");
        }

        int titularesLocal = this.equipoLocal.getJugadoresTitulares().size();
        int titularesVisitante = this.equipoVisitante.getJugadoresTitulares().size();

        if (titularesLocal != 11) {
            throw new IllegalStateException(
                    "El equipo local (" + this.equipoLocal.getNombre() +
                            ") debe tener exactamente 11 titulares antes de iniciar el partido, tiene " + titularesLocal);
        }
        if (titularesVisitante != 11) {
            throw new IllegalStateException(
                    "El equipo visitante (" + this.equipoVisitante.getNombre() +
                            ") debe tener exactamente 11 titulares antes de iniciar el partido, tiene " + titularesVisitante);
        }
    }

    private void registrarEventosTitulares() {
        List<Jugador> titularesLocal = equipoLocal.getJugadoresTitulares();
        List<Jugador> titularesVisitante = equipoVisitante.getJugadoresTitulares();

        titularesLocal.forEach(jugador -> agregarEventoTitular(jugador, equipoLocal));
        titularesVisitante.forEach(jugador -> agregarEventoTitular(jugador, equipoVisitante));
    }

    private void agregarEventoTitular(Jugador jugador, Equipo club) {
        EventosPartido eventoTitular = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .minuto(LocalTime.of(0, 0))
                .descripcion(jugador.getNombreCompleto() + " - Titular")
                .tipoEvento(TipoEvento.TITULAR)
                .personal(jugador)
                .equipoFavorecido(club)
                .partido(this)
                .build();
        agregarEvento(eventoTitular);
    }

    public void reanudarPartido() {
        if (this.estado == EstadoPartido.FINALIZADO ||
                this.estado == EstadoPartido.CANCELADO ||
                this.estado == EstadoPartido.SUSPENDIDO) {
            throw new IllegalStateException("El partido ya está finalizado");
        }

        if (this.estado == EstadoPartido.PROGRAMADO) {
            throw new IllegalStateException("No se puede avanzar un partido programado, use iniciarPartido()");
        }

        EstadoPartido siguiente = this.estado.getSiguienteEstado();

        if (siguiente == EstadoPartido.FINALIZADO) {
            this.finalizarPartido();
            return;
        }

        EstadoPartido estadoActual = this.estado;

        this.estado = siguiente;

       if (siguiente == EstadoPartido.SEGUNDO_TIEMPO) {
            EventosPartido eventoInicioSegundo = EventosPartido.builder()
                    .idEvento(UUID.randomUUID())
                    .minuto(java.time.LocalTime.of(0, 45))
                    .descripcion("Inicio del segundo tiempo")
                    .tipoEvento(TipoEvento.INICIO_SEGUNDO)
                    .partido(this)
                    .build();
            eventoInicioSegundo.setEstadoEvento(siguiente);
            this.eventos.add(eventoInicioSegundo);
            eventoInicioSegundo.setPartido(this);

        } else if (siguiente == EstadoPartido.PRIMER_TIEMPO_PRORROGA) {
            EventosPartido eventoProrroga = EventosPartido.builder()
                    .idEvento(UUID.randomUUID())
                    .minuto(java.time.LocalTime.of(1, 30))
                    .descripcion("Inicio de la prórroga - Primer tiempo")
                    .tipoEvento(TipoEvento.INICIO_PRIMERO_EXTRA)
                    .partido(this)
                    .build();
            eventoProrroga.setEstadoEvento(siguiente);
            this.eventos.add(eventoProrroga);
            eventoProrroga.setPartido(this);
        } else if (siguiente == EstadoPartido.SEGUNDO_TIEMPO_PRORROGA) {
            EventosPartido eventoInicioSegundoExtra = EventosPartido.builder()
                    .idEvento(UUID.randomUUID())
                    .minuto(java.time.LocalTime.of(1, 45))
                    .descripcion("Inicio del segundo tiempo de prórroga")
                    .tipoEvento(TipoEvento.INICIO_SEGUNDO_EXTRA)
                    .partido(this)
                    .build();
            eventoInicioSegundoExtra.setEstadoEvento(siguiente);
            this.eventos.add(eventoInicioSegundoExtra);
            eventoInicioSegundoExtra.setPartido(this);

        } else if (siguiente == EstadoPartido.PENALTIS) {
           EventosPartido eventoInicioPenaltis = EventosPartido.builder()
                   .idEvento(UUID.randomUUID())
                   .minuto(java.time.LocalTime.of(2, 0))
                   .descripcion("Inicio Definicion por Penaltis")
                   .tipoEvento(TipoEvento.INICIO_PENALTIS)
                   .partido(this)
                   .build();
           eventoInicioPenaltis.setEstadoEvento(siguiente);
           this.eventos.add(eventoInicioPenaltis);
           eventoInicioPenaltis.setPartido(this);
        }
    }

    public void finalizarPartido() {
        if (this.estado == EstadoPartido.FINALIZADO ||
                this.estado == EstadoPartido.CANCELADO ||
                this.estado == EstadoPartido.SUSPENDIDO) {
            throw new IllegalStateException("El partido ya ha finalizado");
        }

        EstadoPartido estadoActual = this.estado;

        if (estadoActual == EstadoPartido.PENALTIS) {
            this.estado = EstadoPartido.FINALIZADO;
            EventosPartido eventoFin = EventosPartido.builder()
                    .idEvento(UUID.randomUUID())
                    .minuto(java.time.LocalTime.of(2, 00))
                    .descripcion("Finalización del partido")
                    .tipoEvento(TipoEvento.FIN_PARTIDO)
                    .partido(this)
                    .build();
            eventoFin.setEstadoEvento(estadoActual);
            this.eventos.add(eventoFin);
            eventoFin.setPartido(this);
            return;
        }

        if (estadoActual.Finalizable()) {

            this.estado = EstadoPartido.FINALIZADO;
            EventosPartido eventoFin = EventosPartido.builder()
                    .idEvento(UUID.randomUUID())
                    .minuto(java.time.LocalTime.of(2, 0))
                    .descripcion("Finalización del partido")
                    .tipoEvento(TipoEvento.FIN_PARTIDO)
                    .partido(this)
                    .build();
            eventoFin.setEstadoEvento(estadoActual);
            this.eventos.add(eventoFin);
            eventoFin.setPartido(this);
        }

    }

    public void agregarEvento(EventosPartido evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        if (this.estado == EstadoPartido.FINALIZADO ||
                this.estado == EstadoPartido.CANCELADO ||
                this.estado == EstadoPartido.SUSPENDIDO) {
            throw new IllegalStateException("No se pueden agregar eventos a un partido finalizado");
        }

        if (this.estado == EstadoPartido.PENALTIS) {
            if (!evento.getTipoEvento().esTarjeta() && !evento.esPenalti()) {
                throw new IllegalStateException("En fase de penaltis solo se permiten tarjetas y eventos de penalti");
            }
        }

        boolean esEventoTransicion = evento.getTipoEvento() == TipoEvento.FIN_PRIMERO ||
                evento.getTipoEvento() == TipoEvento.INICIO_SEGUNDO ||
                evento.getTipoEvento() == TipoEvento.FIN_SEGUNDO ||
                evento.getTipoEvento() == TipoEvento.INICIO_PRIMERO_EXTRA ||
                evento.getTipoEvento() == TipoEvento.FIN_PRIMERO_EXTRA ||
                evento.getTipoEvento() == TipoEvento.INICIO_SEGUNDO_EXTRA ||
                evento.getTipoEvento() == TipoEvento.FIN_SEGUNDO_EXTRA ||
                evento.getTipoEvento() == TipoEvento.FIN_PARTIDO ||
                evento.getTipoEvento() == TipoEvento.INICIO_PARTIDO ||
                evento.getTipoEvento() == TipoEvento.PENALTI_CONCEDIDO ||
                evento.getTipoEvento() == TipoEvento.AGREGADO;

        if (!esEventoTransicion) {
            if (this.estado == EstadoPartido.ENTRETIEMPO ||
                    this.estado == EstadoPartido.ENTRETIEMPO_PRORROGA ||
                    this.estado == EstadoPartido.ESPERANDO_PRORROGA ||
                    this.estado == EstadoPartido.ESPERANDO_PENALTIS ||
                    this.estado == EstadoPartido.PROGRAMADO) {
                throw new IllegalStateException("No se pueden registrar eventos durante " + this.estado.getDisplayName());
            }
        }

        boolean requierePersonalEnCampo = evento.getPersonal() != null &&
                evento.getTipoEvento() != TipoEvento.TITULAR &&
                evento.getTipoEvento() != TipoEvento.SUB_IN;

        if (requierePersonalEnCampo && !estaEnCampo(evento.getPersonal())) {
            throw new IllegalStateException(
                    evento.getPersonal().getNombreCompleto() + " no está actualmente en el campo de juego");
        }

        evento.setEstadoEvento(this.estado);
        this.eventos.add(evento);
        evento.setPartido(this);

        if (evento.getMinuto() != null && evento.getTipoEvento() != TipoEvento.AGREGADO) {
            int totalMinutos =  (evento.getMinuto().getHour()*60) + evento.getMinuto().getMinute() + (evento.getMinuto().getSecond() > 0 ? 1 : 0);

            if (!esEventoTransicion) {
                if (this.estado.esTiempoValido()) {
                    if (totalMinutos > 120) {
                        throw new IllegalStateException("El minuto " + totalMinutos + "' excede el límite máximo de 120'");
                    }
                } else {
                    int limite = this.estado.getMinutoLimite();
                    if (totalMinutos > limite) {
                        throw new IllegalStateException(
                                "No se puede registrar un minuto mayor a " + limite +
                                        "' en " + this.estado.getDisplayName() + " sin tiempo agregado");
                    }
                }
            }
        }

        if (evento.getTipoEvento().afectaMarcador() && this.estado != EstadoPartido.PENALTIS) {
            actualizarGoles(evento);
        }
    }

    public void agregarTiempoAgregado(int minutos) {
        if (this.estado == EstadoPartido.FINALIZADO ||
                this.estado == EstadoPartido.CANCELADO ||
                this.estado == EstadoPartido.SUSPENDIDO) {
            throw new IllegalStateException("No se puede agregar tiempo a un partido finalizado");
        }

        if (minutos <= 0) {
            throw new IllegalArgumentException("Los minutos agregados deben ser positivos");
        }

        EstadoPartido estadoAnterior = this.estado;

        EstadoPartido nuevoEstado = switch (this.estado) {
            case PRIMER_TIEMPO -> EstadoPartido.AGREGADO_PRIMER_TIEMPO;
            case SEGUNDO_TIEMPO -> EstadoPartido.AGREGADO_SEGUNDO_TIEMPO;
            case PRIMER_TIEMPO_PRORROGA -> EstadoPartido.AGREGADO_PRORROGA_PRIMER;
            case SEGUNDO_TIEMPO_PRORROGA -> EstadoPartido.AGREGADO_PRORROGA_SEGUNDO;
            default -> throw new IllegalStateException("No se puede agregar tiempo en estado: " + this.estado);
        };


        int minutoBase = switch (estadoAnterior) {
            case PRIMER_TIEMPO -> 45;
            case SEGUNDO_TIEMPO -> 30;
            case PRIMER_TIEMPO_PRORROGA -> 45;
            case SEGUNDO_TIEMPO_PRORROGA -> 0;
            default -> 0;
        };

        int horaBase = switch (estadoAnterior) {
            case PRIMER_TIEMPO -> 0;
            case SEGUNDO_TIEMPO -> 1;
            case PRIMER_TIEMPO_PRORROGA -> 1;
            case SEGUNDO_TIEMPO_PRORROGA -> 2;
            default -> 0;
        };

        EventosPartido eventoAgregado = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .minuto(java.time.LocalTime.of(horaBase, minutoBase))
                .descripcion("+" + minutos + " minutos")
                .tipoEvento(TipoEvento.AGREGADO)
                .partido(this)
                .build();
        eventoAgregado.setEstadoEvento(estadoAnterior);
        this.eventos.add(eventoAgregado);
        eventoAgregado.setPartido(this);

        this.estado = nuevoEstado;
    }

    public void finalizarTiempo(LocalTime minutoFin) {
        if (this.estado == EstadoPartido.FINALIZADO ||
                this.estado == EstadoPartido.CANCELADO ||
                this.estado == EstadoPartido.SUSPENDIDO) {
            throw new IllegalStateException("El partido ya ha finalizado");
        }

        if (minutoFin == null) {
            throw new IllegalArgumentException("El minuto de finalización es obligatorio");
        }

        int totalMinutos = (minutoFin.getHour()*60) + minutoFin.getMinute() + (minutoFin.getSecond() > 0 ? 1 : 0);

        switch (this.estado) {
            case PRIMER_TIEMPO, AGREGADO_PRIMER_TIEMPO -> {
                if (totalMinutos < 45) {
                    throw new IllegalStateException("El primer tiempo no puede finalizar antes del minuto 45");
                }
            }
            case SEGUNDO_TIEMPO, AGREGADO_SEGUNDO_TIEMPO -> {
                if (totalMinutos < 90) {
                    throw new IllegalStateException("El segundo tiempo no puede finalizar antes del minuto 90");
                }
            }
            case PRIMER_TIEMPO_PRORROGA, AGREGADO_PRORROGA_PRIMER -> {
                if (totalMinutos < 105) {
                    throw new IllegalStateException("El primer tiempo de prorroga no puede finalizar antes del minuto 105");
                }
            }
            case SEGUNDO_TIEMPO_PRORROGA, AGREGADO_PRORROGA_SEGUNDO -> {
                if (totalMinutos < 120) {
                    throw new IllegalStateException("La prórroga no puede finalizar antes del minuto 120");
                }
            }
            case PENALTIS -> {
            }
            default -> throw new IllegalStateException("No se puede finalizar tiempo en estado: " + this.estado);
        }

        EstadoPartido estadoActual = this.estado;

        TipoEvento tipoFin = switch (estadoActual) {
            case PRIMER_TIEMPO, AGREGADO_PRIMER_TIEMPO -> TipoEvento.FIN_PRIMERO;
            case SEGUNDO_TIEMPO, AGREGADO_SEGUNDO_TIEMPO -> TipoEvento.FIN_SEGUNDO;
            case PRIMER_TIEMPO_PRORROGA, AGREGADO_PRORROGA_PRIMER -> TipoEvento.FIN_PRIMERO_EXTRA;
            case SEGUNDO_TIEMPO_PRORROGA, AGREGADO_PRORROGA_SEGUNDO -> TipoEvento.FIN_SEGUNDO_EXTRA;
            case PENALTIS -> TipoEvento.FIN_PARTIDO;
            default -> throw new IllegalStateException("Estado no válido para finalizar tiempo");
        };

        EstadoPartido siguienteEstado = switch (estadoActual) {
            case PRIMER_TIEMPO, AGREGADO_PRIMER_TIEMPO -> EstadoPartido.ENTRETIEMPO;
            case SEGUNDO_TIEMPO, AGREGADO_SEGUNDO_TIEMPO -> EstadoPartido.ESPERANDO_PRORROGA;
            case PRIMER_TIEMPO_PRORROGA, AGREGADO_PRORROGA_PRIMER -> EstadoPartido.ENTRETIEMPO_PRORROGA;
            case SEGUNDO_TIEMPO_PRORROGA, AGREGADO_PRORROGA_SEGUNDO -> EstadoPartido.ESPERANDO_PENALTIS;
            case PENALTIS -> EstadoPartido.FINALIZADO;
            default -> throw new IllegalStateException("Estado no válido para finalizar tiempo");
        };

        String descripcion = switch (estadoActual) {
            case PRIMER_TIEMPO, AGREGADO_PRIMER_TIEMPO -> "Fin del primer tiempo";
            case SEGUNDO_TIEMPO, AGREGADO_SEGUNDO_TIEMPO -> "Fin del segundo tiempo";
            case PRIMER_TIEMPO_PRORROGA, AGREGADO_PRORROGA_PRIMER -> "Fin del primer tiempo de prórroga";
            case SEGUNDO_TIEMPO_PRORROGA, AGREGADO_PRORROGA_SEGUNDO -> "Fin del segundo tiempo de prórroga";
            case PENALTIS -> "Fin de los penaltis";
            default -> "Fin del período";
        };

        EventosPartido eventoFin = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .minuto(minutoFin)
                .descripcion(descripcion)
                .tipoEvento(tipoFin)
                .partido(this)
                .build();
        eventoFin.setEstadoEvento(estadoActual);
        this.eventos.add(eventoFin);
        eventoFin.setPartido(this);

        this.estado = siguienteEstado;
    }


    public void cambiarEstado(EstadoPartido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.estado = nuevoEstado;
    }
    

    public boolean estaEnCurso() {
        return this.estado == EstadoPartido.PRIMER_TIEMPO ||
                this.estado == EstadoPartido.AGREGADO_PRIMER_TIEMPO ||
                this.estado == EstadoPartido.ENTRETIEMPO ||
                this.estado == EstadoPartido.SEGUNDO_TIEMPO ||
                this.estado == EstadoPartido.AGREGADO_SEGUNDO_TIEMPO ||
                this.estado == EstadoPartido.ESPERANDO_PRORROGA ||
                this.estado == EstadoPartido.PRIMER_TIEMPO_PRORROGA ||
                this.estado == EstadoPartido.AGREGADO_PRORROGA_PRIMER ||
                this.estado == EstadoPartido.ENTRETIEMPO_PRORROGA ||
                this.estado == EstadoPartido.SEGUNDO_TIEMPO_PRORROGA ||
                this.estado == EstadoPartido.AGREGADO_PRORROGA_SEGUNDO ||
                this.estado == EstadoPartido.PENALTIS;
    }
    
    public boolean haFinalizado() {
        return this.estado == EstadoPartido.FINALIZADO || 
               this.estado == EstadoPartido.CANCELADO ||
               this.estado == EstadoPartido.SUSPENDIDO;
    }
    
    public String getResultado() {
        if (!haFinalizado()) {
            return "En curso";
        }
        return String.format("%d - %d", golesLocal, golesVisitante);
    }

    public boolean estaEnCampo(PersonalDeportivo jugador) {
        if (jugador == null) {
            return false;
        }

        EventosPartido ultimoEventoAlineacion = this.eventos.stream()
                .filter(e -> e.getPersonal() != null && e.getPersonal().equals(jugador))
                .filter(e -> e.getTipoEvento() == TipoEvento.TITULAR ||
                        e.getTipoEvento() == TipoEvento.SUB_IN ||
                        e.getTipoEvento() == TipoEvento.SUB_OUT)
                .reduce((primero, ultimo) -> ultimo) // se queda con el más reciente por orden de inserción
                .orElse(null);

        if (ultimoEventoAlineacion == null) {
            return false;
        }

        return ultimoEventoAlineacion.getTipoEvento() == TipoEvento.TITULAR ||
                ultimoEventoAlineacion.getTipoEvento() == TipoEvento.SUB_IN;
    }

    

    public boolean hayEmpate() {
        return haFinalizado() && golesLocal == golesVisitante;
    }
    

    public long getDuracionMinutos() {
        if (eventos.isEmpty()) {
            return 0;
        }
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
        
        return 90;
    }


    public boolean esFuturo() {
        return fechaYHora != null && fechaYHora.isAfter(LocalDateTime.now());
    }
    
    public boolean esHoy() {
        if (fechaYHora == null) {
            return false;
        }
        LocalDateTime hoy = LocalDateTime.now();
        return fechaYHora.toLocalDate().equals(hoy.toLocalDate());
    }
    public int getPuntosParaClub(UUID idClub) {
        if (this.estado == EstadoPartido.CANCELADO ||
                this.estado == EstadoPartido.SUSPENDIDO) {
            return 0;
        }

        if (!haFinalizado() && !estaEnCurso()) {
            return 0;
        }

        boolean esLocal = equipoLocal.getIdEquipo().equals(idClub);
        int golesFavor = esLocal ? golesLocal : golesVisitante;
        int golesContra = esLocal ? golesVisitante : golesLocal;

        if (!haFinalizado() && estaEnCurso()) {
            if (golesFavor > golesContra) return 3;
            if (golesFavor == golesContra) return 1;
            return 0;
        }

        if (golesFavor > golesContra) return 3;
        if (golesFavor == golesContra) return 1;
        return 0;
    }


    private void actualizarGoles(EventosPartido evento) {
        if (this.estado == EstadoPartido.PENALTIS) {
            return;
        }

        if (evento.getEquipoFavorecido() == null) {
            return;
        }

        boolean esLocal = evento.getEquipoFavorecido().getIdEquipo().equals(this.equipoLocal.getIdEquipo());
        boolean esVisitante = evento.getEquipoFavorecido().getIdEquipo().equals(this.equipoVisitante.getIdEquipo());

        if (!esLocal && !esVisitante) {
            return;
        }

        if (evento.getTipoEvento() == TipoEvento.GOL_ANULADO) {
            restarUltimoGol(evento.getEquipoFavorecido());
            return;
        }

        if (evento.getTipoEvento().esGolValido()) {
            if (esLocal) {
                this.golesLocal++;
            } else if (esVisitante) {
                this.golesVisitante++;
            }
        }
    }


    private void restarUltimoGol(Equipo equipo) {
        boolean esLocal = equipo.getIdEquipo().equals(this.equipoLocal.getIdEquipo());


        EventosPartido ultimoGol = this.eventos.stream()
                .filter(e -> e.getEquipoFavorecido() != null)
                .filter(e -> e.getEquipoFavorecido().getIdEquipo().equals(equipo.getIdEquipo()))
                .filter(e -> e.getTipoEvento().esGolValido())
                .reduce((first, second) -> second)
                .orElse(null);

        if (ultimoGol == null) {
            throw new IllegalStateException(
                    "No se puede anular un gol porque no hay goles registrados para " + equipo.getNombreCorto()
            );
        }

        if (esLocal) {
            if (this.golesLocal <= 0) {
                throw new IllegalStateException("No se puede anular un gol porque el marcador local es 0");
            }
            this.golesLocal--;
        } else {
            if (this.golesVisitante <= 0) {
                throw new IllegalStateException("No se puede anular un gol porque el marcador visitante es 0");
            }
            this.golesVisitante--;
        }

    }



    public  List<EventosPartido> realizarSustitucion(Jugador jugadorEntrante, Jugador jugadorSaliente, Equipo club, LocalTime minuto) {
        if (jugadorEntrante == null || jugadorSaliente == null) {
            throw new IllegalArgumentException("Los jugadores de la sustitución no pueden ser nulos");
        }
        if (jugadorEntrante.equals(jugadorSaliente)) {
            throw new IllegalArgumentException("El jugador entrante no puede ser el mismo que el saliente");
        }
        if (club == null) {
            throw new IllegalArgumentException("El club no puede ser nulo");
        }

        EventosPartido eventoSale = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .minuto(minuto)
                .descripcion(jugadorSaliente.getNombreCompleto() + " sale del campo")
                .tipoEvento(TipoEvento.SUB_OUT)
                .personal(jugadorSaliente)
                .equipoFavorecido(club)
                .partido(this)
                .build();
        agregarEvento(eventoSale);


        EventosPartido eventoEntra = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .minuto(minuto)
                .descripcion(jugadorEntrante.getNombreCompleto() + " entra al campo")
                .tipoEvento(TipoEvento.SUB_IN)
                .personal(jugadorEntrante)
                .equipoFavorecido(club)
                .partido(this)
                .build();
        agregarEvento(eventoEntra);

        if (jugadorSaliente.getDatosDeportivos() != null) {
            jugadorSaliente.getDatosDeportivos().cambiarASuplente();
        }
        if (jugadorEntrante.getDatosDeportivos() != null) {
            jugadorEntrante.getDatosDeportivos().promoverATitular();
        }

        return List.of(eventoSale, eventoEntra);
    }


}
