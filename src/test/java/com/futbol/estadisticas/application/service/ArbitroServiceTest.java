package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.CrearArbitroRequest;
import com.futbol.estadisticas.application.port.dto.response.ArbitroResponse;
import com.futbol.estadisticas.application.port.mapper.ArbitroMapper;
import com.futbol.estadisticas.application.port.out.ArbitroRepositoryPort;
import com.futbol.estadisticas.application.service.ArbitroService;
import com.futbol.estadisticas.domain.model.Arbitro;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArbitroServiceTest {

    @Mock
    private ArbitroRepositoryPort arbitroRepository;

    @Mock
    private ArbitroMapper arbitroMapper;

    @InjectMocks
    private ArbitroService arbitroService;

    private static final UUID ID_ARBITRO = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private Arbitro arbitro;
    private CrearArbitroRequest request;
    private ArbitroResponse response;

    @BeforeEach
    void setUp() {
        // Setup request
        request = CrearArbitroRequest.builder()
                .nombre("Michael")
                .apellido("Oliver")
                .fechaNacimiento(LocalDate.of(1985, 2, 20))
                .build();

        // Setup domain entity
        arbitro = Arbitro.builder()
                .idArbitro(ID_ARBITRO)
                .nombre("Michael")
                .apellido("Oliver")
                .fechaNacimiento(LocalDate.of(1985, 2, 20))
                .build();

        // Setup response
        response = ArbitroResponse.builder()
                .idArbitro(ID_ARBITRO)
                .nombre("Michael")
                .apellido("Oliver")
                .fechaNacimiento(LocalDate.of(1985, 2, 20))
                .build();
    }

    @Test
    @DisplayName("crearArbitro: debe crear un árbitro exitosamente")
    void testCrearArbitro() {
        // Given
        when(arbitroMapper.toEntity(request)).thenReturn(arbitro);
        when(arbitroRepository.save(any(Arbitro.class))).thenReturn(arbitro);
        when(arbitroMapper.toResponse(arbitro)).thenReturn(response);

        // When
        ArbitroResponse result = arbitroService.crearArbitro(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.idArbitro()).isEqualTo(ID_ARBITRO);
        assertThat(result.nombre()).isEqualTo("Michael");
        assertThat(result.apellido()).isEqualTo("Oliver");
        
        verify(arbitroMapper).toEntity(request);
        verify(arbitroRepository).save(arbitro);
        verify(arbitroMapper).toResponse(arbitro);
    }

    @Test
    @DisplayName("obtenerArbitroPorId: debe retornar el árbitro cuando existe")
    void testObtenerArbitroPorId_CuandoExiste() {
        // Given
        when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));
        when(arbitroMapper.toResponse(arbitro)).thenReturn(response);

        // When
        ArbitroResponse result = arbitroService.obtenerArbitroPorId(ID_ARBITRO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.idArbitro()).isEqualTo(ID_ARBITRO);
        assertThat(result.nombre()).isEqualTo("Michael");
        
        verify(arbitroRepository).findById(ID_ARBITRO);
        verify(arbitroMapper).toResponse(arbitro);
    }

    @Test
    @DisplayName("obtenerArbitroPorId: debe lanzar excepción cuando el árbitro no existe")
    void testObtenerArbitroPorId_CuandoNoExiste() {
        // Given
        when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> arbitroService.obtenerArbitroPorId(ID_ARBITRO))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Árbitro no encontrado con id: " + ID_ARBITRO);

        verify(arbitroRepository).findById(ID_ARBITRO);
        verify(arbitroMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("obtenerTodosLosArbitros: debe retornar lista de árbitros")
    void testObtenerTodosLosArbitros() {
        // Given
        List<Arbitro> arbitros = List.of(arbitro);
        List<ArbitroResponse> responses = List.of(response);

        when(arbitroRepository.findAll()).thenReturn(arbitros);
        when(arbitroMapper.toResponse(arbitro)).thenReturn(response);

        // When
        List<ArbitroResponse> result = arbitroService.obtenerTodosLosArbitros();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).idArbitro()).isEqualTo(ID_ARBITRO);
        
        verify(arbitroRepository).findAll();
        verify(arbitroMapper).toResponse(arbitro);
    }

    @Test
    @DisplayName("obtenerTodosLosArbitros: debe retornar lista vacía cuando no hay árbitros")
    void testObtenerTodosLosArbitros_Vacio() {
        // Given
        when(arbitroRepository.findAll()).thenReturn(List.of());

        // When
        List<ArbitroResponse> result = arbitroService.obtenerTodosLosArbitros();

        // Then
        assertThat(result).isEmpty();
        verify(arbitroRepository).findAll();
        verify(arbitroMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("buscarArbitrosPorNombre: debe buscar árbitros por nombre o apellido")
    void testBuscarArbitrosPorNombre() {
        // Given
        String termino = "Michael";
        List<Arbitro> arbitros = List.of(arbitro);
        List<ArbitroResponse> responses = List.of(response);

        when(arbitroRepository.findByNombreOrApellido(termino)).thenReturn(arbitros);
        when(arbitroMapper.toResponse(arbitro)).thenReturn(response);

        // When
        List<ArbitroResponse> result = arbitroService.buscarArbitrosPorNombre(termino);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Michael");
        
        verify(arbitroRepository).findByNombreOrApellido(termino);
    }

    @Test
    @DisplayName("buscarArbitrosPorNombre: debe retornar lista vacía cuando no hay coincidencias")
    void testBuscarArbitrosPorNombre_SinCoincidencias() {
        // Given
        String termino = "Inexistente";
        when(arbitroRepository.findByNombreOrApellido(termino)).thenReturn(List.of());

        // When
        List<ArbitroResponse> result = arbitroService.buscarArbitrosPorNombre(termino);

        // Then
        assertThat(result).isEmpty();
        verify(arbitroRepository).findByNombreOrApellido(termino);
    }

    @Test
    @DisplayName("eliminarArbitro: debe eliminar el árbitro cuando existe")
    void testEliminarArbitro_CuandoExiste() {
        // Given
        when(arbitroRepository.existsById(ID_ARBITRO)).thenReturn(true);
        doNothing().when(arbitroRepository).deleteById(ID_ARBITRO);

        // When
        arbitroService.eliminarArbitro(ID_ARBITRO);

        // Then
        verify(arbitroRepository).existsById(ID_ARBITRO);
        verify(arbitroRepository).deleteById(ID_ARBITRO);
    }

    @Test
    @DisplayName("eliminarArbitro: debe lanzar excepción cuando el árbitro no existe")
    void testEliminarArbitro_CuandoNoExiste() {
        // Given
        when(arbitroRepository.existsById(ID_ARBITRO)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> arbitroService.eliminarArbitro(ID_ARBITRO))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Árbitro no encontrado con id: " + ID_ARBITRO);

        verify(arbitroRepository).existsById(ID_ARBITRO);
        verify(arbitroRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("crearArbitro: debe manejar excepción del repository")
    void testCrearArbitro_ErrorRepository() {
        // Given
        when(arbitroMapper.toEntity(request)).thenReturn(arbitro);
        when(arbitroRepository.save(any(Arbitro.class)))
                .thenThrow(new RuntimeException("Error al guardar"));

        // When & Then
        assertThatThrownBy(() -> arbitroService.crearArbitro(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error al guardar");

        verify(arbitroMapper).toEntity(request);
        verify(arbitroRepository).save(arbitro);
    }
}