package com.futbol.estadisticas.application.port.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RankingEstadisticasDTO (
        // Datos del jugador
        UUID idJugador,
        String nombre,
        String apellido,
        String nombreCompleto,
        Integer dorsal,
        String nombreEquipo,
        UUID idEquipo,

        // Estadísticas generales
        int minutosJugados,
        int partidosJugados,

        // Goles
        int goles,
        int golesPenal,
        int autogoles,

        // Asistencias y tiros
        int asistencias,
        int tirosAPuerta,
        int tirosFuera,

        // Penaltis
        int penalesAnotados,
        int penalesConseguidos,
        int penalesFallados,

        // Tarjetas
        int tarjetasAmarillas,
        int tarjetasRojas,

        // Porteros
        int paradas,
        int porteriasCero
) {
    public int getTotalTiros() {
        return tirosAPuerta + tirosFuera;
    }

    public double getEfectividadTiros() {
        int total = getTotalTiros();
        return total == 0 ? 0.0 : (double) tirosAPuerta / total;
    }

    // Métodos "with" para inmutabilidad
    public RankingEstadisticasDTO withMinutosJugados(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, value, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withPartidosJugados(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, value,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withGoles(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                value, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withGolesPenal(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, value, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withAutogoles(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, value, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withAsistencias(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, value,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withTirosAPuerta(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                value, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withTirosFuera(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, value, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withPenalesAnotados(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, value, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withPenalesConseguidos(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, value,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withPenalesFallados(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                value, tarjetasAmarillas, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withTarjetasAmarillas(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, value, tarjetasRojas,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withTarjetasRojas(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, value,
                paradas, porteriasCero
        );
    }

    public RankingEstadisticasDTO withParadas(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                value, porteriasCero
        );
    }

    public RankingEstadisticasDTO withPorteriasCero(int value) {
        return new RankingEstadisticasDTO(
                idJugador, nombre, apellido, nombreCompleto, dorsal,
                nombreEquipo, idEquipo, minutosJugados, partidosJugados,
                goles, golesPenal, autogoles, asistencias,
                tirosAPuerta, tirosFuera, penalesAnotados, penalesConseguidos,
                penalesFallados, tarjetasAmarillas, tarjetasRojas,
                paradas, value
        );
    }
}
