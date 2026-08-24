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
        UUID defensaCentral,

        // Carrileros
        UUID carrileroIzquierdo,
        UUID carrileroDerecho,

        // MedioCampistas
        UUID centroCampistaDefensivo,
        UUID centroCampistaDefensivoIzquierdo,
        UUID centroCampistaDefensivoDerecho,
        UUID centroCampistaIzquierdo,
        UUID centroCampistaDerecho,
        UUID centroCampista,
        UUID medioCentroIzquierdo,
        UUID medioCentroDerecho,

        // Ofensivos
        UUID centroCampistaOfensivo,
        UUID centroCampistaOfensivoIzquierdo,
        UUID centroCampistaOfensivoDerecho,

        // Delanteros
        UUID extremoIzquierdo,
        UUID extremoDerecho,
        UUID delanteroCentro,
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
        mapa.put("defensaCentral", defensaCentral);
        mapa.put("carrileroIzquierdo", carrileroIzquierdo);
        mapa.put("carrileroDerecho", carrileroDerecho);
        mapa.put("centroCampistaDefensivo", centroCampistaDefensivo);
        mapa.put("centroCampistaDefensivoIzquierdo", centroCampistaDefensivoIzquierdo);
        mapa.put("centroCampistaDefensivoDerecho", centroCampistaDefensivoDerecho);
        mapa.put("medioCentroIzquierdo", medioCentroIzquierdo);
        mapa.put("medioCentroDerecho", medioCentroDerecho);
        mapa.put("centroCampistaIzquierdo", centroCampistaIzquierdo);
        mapa.put("centroCampistaDerecho", centroCampistaDerecho);
        mapa.put("centroCampista", centroCampista);
        mapa.put("centroCampistaOfensivo", centroCampistaOfensivo);
        mapa.put("centroCampistaOfensivoIzquierdo", centroCampistaOfensivoIzquierdo);
        mapa.put("centroCampistaOfensivoDerecho", centroCampistaOfensivoDerecho);
        mapa.put("extremoIzquierdo", extremoIzquierdo);
        mapa.put("extremoDerecho", extremoDerecho);
        mapa.put("delanteroCentro", delanteroCentro);
        mapa.put("delanteroIzquierdo", delanteroIzquierdo);
        mapa.put("delanteroDerecho", delanteroDerecho);
        return mapa;
    }
}



