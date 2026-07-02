package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.ActualizarTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import com.futbol.estadisticas.application.port.in.TecnicoUseCase;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TecnicoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TecnicoUseCase tecnicoUseCase;

    @InjectMocks
    private TecnicoController tecnicoController;

    private UUID idTecnico;
    private UUID idClub;
    private TecnicoResponse response;
    private CrearTecnicoRequest crearRequest;
    private ActualizarTecnicoRequest actualizarRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mockMvc = MockMvcBuilders.standaloneSetup(tecnicoController).build();

        idTecnico = UUID.randomUUID();
        idClub = UUID.randomUUID();

        response = TecnicoResponse.builder()
                .idPersonal(idTecnico)
                .nombre("Pep")
                .apellido("Guardiola")
                .nombreCompleto("Pep Guardiola")
                .fechaNacimiento(LocalDate.of(1971, 1, 18))
                .nacionalidad(Nacion.ESPAÑA)
                .estiloJuego("Tiki-taka")
                .alineacionFavorita("4-3-3")
                .clubActual("FC Barcelona")
                .idClubActual(idClub)
                .build();

        crearRequest = CrearTecnicoRequest.builder()
                .nombre("Pep")
                .apellido("Guardiola")
                .fechaNacimiento(LocalDate.of(1971, 1, 18))
                .nacionalidad(Nacion.ESPAÑA)
                .estiloJuego("Tiki-taka")
                .alineacionFavorita("4-3-3")
                .build();

        actualizarRequest = ActualizarTecnicoRequest.builder()
                .nombre("Pep Actualizado")
                .apellido("Guardiola Actualizado")
                .estiloJuego("Tiki-taka moderno")
                .alineacionFavorita("3-4-3")
                .build();
    }

    @Test
    @DisplayName("POST /apifutbol/tecnicos - debe crear un técnico")
    void testCrear() throws Exception {
        when(tecnicoUseCase.crearTecnico(any(CrearTecnicoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/apifutbol/tecnicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPersonal").value(idTecnico.toString()))
                .andExpect(jsonPath("$.nombre").value("Pep"));

        verify(tecnicoUseCase).crearTecnico(any(CrearTecnicoRequest.class));
    }

    @Test
    @DisplayName("GET /apifutbol/tecnicos - debe listar todos los técnicos")
    void testListarTodos() throws Exception {
        when(tecnicoUseCase.obtenerTodosTecnicos()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/tecnicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPersonal").value(idTecnico.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Pep"));

        verify(tecnicoUseCase).obtenerTodosTecnicos();
    }

    @Test
    @DisplayName("GET /apifutbol/tecnicos/{id} - debe obtener técnico por ID")
    void testObtenerPorId() throws Exception {
        when(tecnicoUseCase.obtenerTecnicoPorId(idTecnico)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/tecnicos/{id}", idTecnico))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(idTecnico.toString()))
                .andExpect(jsonPath("$.nombre").value("Pep"));

        verify(tecnicoUseCase).obtenerTecnicoPorId(idTecnico);
    }

    @Test
    @DisplayName("GET /apifutbol/tecnicos/club/{idClub}/actual - debe obtener técnico actual del club")
    void testTecnicoActualDeClub() throws Exception {
        when(tecnicoUseCase.obtenerTecnicoActualDeClub(idClub)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/tecnicos/club/{idClub}/actual", idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clubActual").value("FC Barcelona"))
                .andExpect(jsonPath("$.idClubActual").value(idClub.toString()));

        verify(tecnicoUseCase).obtenerTecnicoActualDeClub(idClub);
    }

    @Test
    @DisplayName("PATCH /apifutbol/tecnicos/{id} - debe actualizar un técnico")
    void testActualizar() throws Exception {
        when(tecnicoUseCase.actualizarTecnico(eq(idTecnico), any(ActualizarTecnicoRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/tecnicos/{id}", idTecnico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(idTecnico.toString()))
                .andExpect(jsonPath("$.nombre").value("Pep"));

        verify(tecnicoUseCase).actualizarTecnico(eq(idTecnico), any(ActualizarTecnicoRequest.class));
    }

    @Test
    @DisplayName("PUT /apifutbol/tecnicos/{id}/asignar-club/{idClub} - debe asignar técnico a un club")
    void testAsignarAClub() throws Exception {
        when(tecnicoUseCase.asignarTecnicoAClub(idTecnico, idClub)).thenReturn(response);

        mockMvc.perform(put("/apifutbol/tecnicos/{id}/asignar-club/{idClub}", idTecnico, idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(idTecnico.toString()))
                .andExpect(jsonPath("$.clubActual").value("FC Barcelona"));

        verify(tecnicoUseCase).asignarTecnicoAClub(idTecnico, idClub);
    }

    @Test
    @DisplayName("DELETE /apifutbol/tecnicos/club/{idClub}/desvincular - debe desvincular técnico del club")
    void testDesvincularDeClub() throws Exception {
        doNothing().when(tecnicoUseCase).desvincularTecnicoDeClub(idClub);

        mockMvc.perform(delete("/apifutbol/tecnicos/club/{idClub}/desvincular", idClub))
                .andExpect(status().isNoContent());

        verify(tecnicoUseCase).desvincularTecnicoDeClub(idClub);
    }

    @Test
    @DisplayName("DELETE /apifutbol/tecnicos/{id} - debe eliminar un técnico")
    void testEliminar() throws Exception {
        doNothing().when(tecnicoUseCase).eliminarTecnico(idTecnico);

        mockMvc.perform(delete("/apifutbol/tecnicos/{id}", idTecnico))
                .andExpect(status().isNoContent());

        verify(tecnicoUseCase).eliminarTecnico(idTecnico);
    }
}