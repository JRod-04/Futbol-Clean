package com.futbol.estadisticas.application.port.dto.request;


import com.futbol.estadisticas.domain.model.enums.Alineacion;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Builder
public record AlineacionRequest(

        @NotNull(message = "El ID del partido es obligatorio")
        UUID idPartido,

        @NotNull(message = "La formación es obligatoria")
        Alineacion alineacion,

        @NotNull(message = "El portero es obligatorio")
        UUID portero,

        // Defensas
        UUID lateralIzquierdo,
        UUID lateralDerecho,
        UUID centralIzquierdo,
        UUID centralDerecho,
        UUID central,

        // Carrileros
        UUID carrileroIzquierdo,
        UUID carrileroDerecho,

        // Mediocentros
        UUID mediocentroDefensivo,
        UUID mediocentroDefensivoIzquierdo,
        UUID mediocentroDefensivoDerecho,
        UUID mediocentroIzquierdo,
        UUID mediocentroDerecho,
        UUID mediocentro,

        // Ofensivos
        UUID mediocentroOfensivo,
        UUID mediocentroOfensivoIzquierdo,
        UUID mediocentroOfensivoDerecho,

        // Delanteros
        UUID extremoIzquierdo,
        UUID extremoDerecho,
        UUID delantero,
        UUID delanteroIzquierdo,
        UUID delanteroDerecho
){
    public Map<String, UUID> toMap() {
        Map<String, UUID> mapa = new HashMap<>();
        mapa.put("portero", portero);
        mapa.put("lateralIzquierdo", lateralIzquierdo);
        mapa.put("lateralDerecho", lateralDerecho);
        mapa.put("centralIzquierdo", centralIzquierdo);
        mapa.put("centralDerecho", centralDerecho);
        mapa.put("central", central);
        mapa.put("carrileroIzquierdo", carrileroIzquierdo);
        mapa.put("carrileroDerecho", carrileroDerecho);
        mapa.put("mediocentroDefensivo", mediocentroDefensivo);
        mapa.put("mediocentroDefensivoIzquierdo", mediocentroDefensivoIzquierdo);
        mapa.put("mediocentroDefensivoDerecho", mediocentroDefensivoDerecho);
        mapa.put("mediocentroIzquierdo", mediocentroIzquierdo);
        mapa.put("mediocentroDerecho", mediocentroDerecho);
        mapa.put("mediocentro", mediocentro);
        mapa.put("mediocentroOfensivo", mediocentroOfensivo);
        mapa.put("mediocentroOfensivoIzquierdo", mediocentroOfensivoIzquierdo);
        mapa.put("mediocentroOfensivoDerecho", mediocentroOfensivoDerecho);
        mapa.put("extremoIzquierdo", extremoIzquierdo);
        mapa.put("extremoDerecho", extremoDerecho);
        mapa.put("delantero", delantero);
        mapa.put("delanteroIzquierdo", delanteroIzquierdo);
        mapa.put("delanteroDerecho", delanteroDerecho);
        return mapa;
    }
}



