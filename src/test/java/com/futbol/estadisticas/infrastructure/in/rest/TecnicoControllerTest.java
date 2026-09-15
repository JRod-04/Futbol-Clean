package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.request.ActualizarTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponseEstadisticas;
import com.futbol.estadisticas.application.port.in.TecnicoUseCase;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.exception.GlobalExceptionHandler;
import com.futbol.estadisticas.domain.model.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TecnicoController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class TecnicoControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private TecnicoUseCase tecnicoUseCase;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private static final UUID ID = UUID.randomUUID();
    private static final UUID ID_EQUIPO = UUID.randomUUID();


    private static TecnicoResponse response() {
        return TecnicoResponse.builder()
                .idPersonal(ID)
                .nombre("Mikel")
                .apellido("Arteta")
                .build();
    }

    private static CrearTecnicoRequest crearRequest() {
        return CrearTecnicoRequest.builder()
                .nombre("Mikel")
                .apellido("Arteta")
                .fechaNacimiento(LocalDate.of(1982, 3, 26))
                .nacionalidad(Nacion.ESPAÑA)
                .estiloJuego("Posicional")
                .alineacionFavorita("4-3-3")
                .build();
    }

    private static ActualizarTecnicoRequest actualizarRequest() {
        return ActualizarTecnicoRequest.builder()
                .nombre("Nuevo")
                .apellido("Apellido")
                .build();
    }


    @Nested
    @DisplayName("POST /apifutbol/tecnicos")
    class Crear {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            when(tecnicoUseCase.crearTecnico(any())).thenReturn(response());

            mockMvc.perform(post("/apifutbol/tecnicos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idPersonal").value(ID.toString()))
                    .andExpect(jsonPath("$.nombre").value("Mikel"))
                    .andExpect(jsonPath("$.apellido").value("Arteta"));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(post("/apifutbol/tecnicos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/tecnicos")
    class Listar {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(tecnicoUseCase.obtenerTodosTecnicos()).thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/tecnicos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idPersonal").value(ID.toString()))
                    .andExpect(jsonPath("$[0].nombre").value("Mikel"));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/tecnicos/{id}")
    class PorId {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(tecnicoUseCase.obtenerTecnicoPorId(ID)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/tecnicos/{id}", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idPersonal").value(ID.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(tecnicoUseCase.obtenerTecnicoPorId(ID))
                    .thenThrow(new ResourceNotFoundException(
                            "Técnico no encontrado con id: " + ID));

            mockMvc.perform(get("/apifutbol/tecnicos/{id}", ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/tecnicos/{id}/estadisticas")
    class Estadisticas {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            TecnicoResponseEstadisticas est = TecnicoResponseEstadisticas.builder().build();
            when(tecnicoUseCase.obtenerTecnicoConEstadisticas(ID)).thenReturn(est);

            mockMvc.perform(get("/apifutbol/tecnicos/{id}/estadisticas", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 500 si el service lanza RuntimeException genérica")
        void runtimeException() throws Exception {
            when(tecnicoUseCase.obtenerTecnicoConEstadisticas(ID))
                    .thenThrow(new RuntimeException("Técnico no encontrado"));

            mockMvc.perform(get("/apifutbol/tecnicos/{id}/estadisticas", ID))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/tecnicos/{id}/partidos")
    class Partidos {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            PartidoResponse partido = PartidoResponse.builder()
                    .idPartido(UUID.randomUUID())
                    .build();
            when(tecnicoUseCase.obtenerPartidosPorTecnico(ID)).thenReturn(List.of(partido));

            mockMvc.perform(get("/apifutbol/tecnicos/{id}/partidos", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idPartido").value(partido.idPartido().toString()));
        }

        @Test
        @DisplayName("devuelve 404 si el técnico no existe")
        void noExiste() throws Exception {
            when(tecnicoUseCase.obtenerPartidosPorTecnico(ID))
                    .thenThrow(new ResourceNotFoundException(
                            "Técnico no encontrado con id: " + ID));

            mockMvc.perform(get("/apifutbol/tecnicos/{id}/partidos", ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/tecnicos/{idEquipo}/actual")
    class TecnicoActualDeEquipo {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(tecnicoUseCase.obtenerTecnicoActualDeEquipo(ID_EQUIPO)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/tecnicos/{idEquipo}/actual", ID_EQUIPO))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idPersonal").value(ID.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si no hay técnico asignado")
        void noHayTecnico() throws Exception {
            when(tecnicoUseCase.obtenerTecnicoActualDeEquipo(ID_EQUIPO))
                    .thenThrow(new ResourceNotFoundException(
                            "No hay técnico asignado al club con id: " + ID_EQUIPO));

            mockMvc.perform(get("/apifutbol/tecnicos/{idEquipo}/actual", ID_EQUIPO))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/tecnicos/{id}")
    class Actualizar {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(tecnicoUseCase.actualizarTecnico(eq(ID), any())).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/tecnicos/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(actualizarRequest())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idPersonal").value(ID.toString()));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(patch("/apifutbol/tecnicos/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 404 si el técnico no existe")
        void noExiste() throws Exception {
            when(tecnicoUseCase.actualizarTecnico(eq(ID), any()))
                    .thenThrow(new ResourceNotFoundException(
                            "Técnico no encontrado con id: " + ID));

            mockMvc.perform(patch("/apifutbol/tecnicos/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(actualizarRequest())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /apifutbol/tecnicos/{id}")
    class Eliminar {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            doNothing().when(tecnicoUseCase).eliminarTecnico(ID);

            mockMvc.perform(delete("/apifutbol/tecnicos/{id}", ID))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            doThrow(new ResourceNotFoundException(
                    "Técnico no encontrado con id: " + ID))
                    .when(tecnicoUseCase).eliminarTecnico(ID);

            mockMvc.perform(delete("/apifutbol/tecnicos/{id}", ID))
                    .andExpect(status().isNotFound());
        }
    }
}