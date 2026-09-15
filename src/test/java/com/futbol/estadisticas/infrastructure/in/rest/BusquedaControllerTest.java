package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import com.futbol.estadisticas.application.port.in.CompeticionUseCase;
import com.futbol.estadisticas.application.port.in.EquipoUseCase;
import com.futbol.estadisticas.application.port.in.JugadoresUseCase;
import com.futbol.estadisticas.application.port.in.TecnicoUseCase;
import com.futbol.estadisticas.domain.model.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BusquedaController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class BusquedaControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private JugadoresUseCase jugadorUseCase;
    @MockitoBean private TecnicoUseCase tecnicoUseCase;
    @MockitoBean private EquipoUseCase equipoUseCase;
    @MockitoBean private CompeticionUseCase competicionUseCase;

    private static final UUID ID = UUID.randomUUID();


    private static JugadorResponse jugadorResponse() {
        return JugadorResponse.builder()
                .idPersonal(ID)
                .nombre("Bukayo")
                .apellido("Saka")
                .build();
    }

    private static TecnicoResponse tecnicoResponse() {
        return TecnicoResponse.builder()
                .idPersonal(ID)
                .nombre("Mikel")
                .apellido("Arteta")
                .build();
    }

    private static EquipoResponse equipoResponse() {
        return EquipoResponse.builder()
                .idEquipo(ID)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .build();
    }

    private static CompeticionResponse competicionResponse() {
        return CompeticionResponse.builder()
                .idCompeticion(ID)
                .nombre("Premier League")
                .build();
    }


    @Nested
    @DisplayName("GET /apifutbol/buscar/jugadores")
    class BuscarJugadores {

        @Test
        @DisplayName("devuelve 200 con la página")
        void ok() throws Exception {
            when(jugadorUseCase.buscarJugadores(eq("Saka"), any()))
                    .thenReturn(new PageImpl<>(List.of(jugadorResponse())));

            mockMvc.perform(get("/apifutbol/buscar/jugadores")
                            .param("q", "Saka"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idPersonal").value(ID.toString()))
                    .andExpect(jsonPath("$.content[0].nombre").value("Bukayo"));
        }

        @Test
        @DisplayName("devuelve 200 con página vacía si q está vacío")
        void qVacio() throws Exception {
            mockMvc.perform(get("/apifutbol/buscar/jugadores"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/buscar/tecnicos")
    class BuscarTecnicos {

        @Test
        @DisplayName("devuelve 200 con la página")
        void ok() throws Exception {
            when(tecnicoUseCase.buscarTecnicos(eq("Arteta"), any()))
                    .thenReturn(new PageImpl<>(List.of(tecnicoResponse())));

            mockMvc.perform(get("/apifutbol/buscar/tecnicos")
                            .param("q", "Arteta"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idPersonal").value(ID.toString()))
                    .andExpect(jsonPath("$.content[0].nombre").value("Mikel"));
        }

        @Test
        @DisplayName("devuelve 200 con página vacía si q está vacío")
        void qVacio() throws Exception {
            mockMvc.perform(get("/apifutbol/buscar/tecnicos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/buscar/equipos")
    class BuscarEquipos {

        @Test
        @DisplayName("devuelve 200 con la página")
        void ok() throws Exception {
            when(equipoUseCase.buscarEquipos(eq("Arsenal"), any()))
                    .thenReturn(new PageImpl<>(List.of(equipoResponse())));

            mockMvc.perform(get("/apifutbol/buscar/equipos")
                            .param("q", "Arsenal"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idEquipo").value(ID.toString()))
                    .andExpect(jsonPath("$.content[0].nombre").value("Arsenal FC"));
        }

        @Test
        @DisplayName("devuelve 200 con página vacía si q está vacío")
        void qVacio() throws Exception {
            mockMvc.perform(get("/apifutbol/buscar/equipos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/buscar/competiciones")
    class BuscarCompeticiones {

        @Test
        @DisplayName("devuelve 200 con la página")
        void ok() throws Exception {
            when(competicionUseCase.buscarCompeticiones(eq("Premier"), any()))
                    .thenReturn(new PageImpl<>(List.of(competicionResponse())));

            mockMvc.perform(get("/apifutbol/buscar/competiciones")
                            .param("q", "Premier"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idCompeticion").value(ID.toString()))
                    .andExpect(jsonPath("$.content[0].nombre").value("Premier League"));
        }

        @Test
        @DisplayName("devuelve 200 con página vacía si q está vacío")
        void qVacio() throws Exception {
            mockMvc.perform(get("/apifutbol/buscar/competiciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/buscar/global")
    class BusquedaGlobal {

        @Test
        @DisplayName("devuelve 200 con las cuatro categorías cuando q está presente")
        void ok() throws Exception {
            when(jugadorUseCase.buscarJugadores(eq("Arsenal"), any()))
                    .thenReturn(new PageImpl<>(List.of(jugadorResponse())));
            when(tecnicoUseCase.buscarTecnicos(eq("Arsenal"), any()))
                    .thenReturn(new PageImpl<>(List.of(tecnicoResponse())));
            when(equipoUseCase.buscarEquipos(eq("Arsenal"), any()))
                    .thenReturn(new PageImpl<>(List.of(equipoResponse())));
            when(competicionUseCase.buscarCompeticiones(eq("Arsenal"), any()))
                    .thenReturn(new PageImpl<>(List.of(competicionResponse())));

            mockMvc.perform(get("/apifutbol/buscar/global")
                            .param("q", "Arsenal"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.jugadores.content[0].nombre").value("Bukayo"))
                    .andExpect(jsonPath("$.tecnicos.content[0].nombre").value("Mikel"))
                    .andExpect(jsonPath("$.equipos.content[0].nombre").value("Arsenal FC"))
                    .andExpect(jsonPath("$.competiciones.content[0].nombre").value("Premier League"));
        }

        @Test
        @DisplayName("devuelve 200 con páginas vacías cuando q está vacío")
        void qVacio() throws Exception {
            mockMvc.perform(get("/apifutbol/buscar/global"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.jugadores").exists())
                    .andExpect(jsonPath("$.tecnicos").exists())
                    .andExpect(jsonPath("$.clubes").exists())
                    .andExpect(jsonPath("$.competiciones").exists())
                    .andExpect(jsonPath("$.jugadores.content").isEmpty())
                    .andExpect(jsonPath("$.tecnicos.content").isEmpty())
                    .andExpect(jsonPath("$.clubes.content").isEmpty())
                    .andExpect(jsonPath("$.competiciones.content").isEmpty());
        }
    }
}