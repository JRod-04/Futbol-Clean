package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;

public interface JugadorJPARepository extends JpaRepository<JugadorJPAEntity, UUID>{



    @Query("SELECT j FROM JugadorJPAEntity j " +
            "LEFT JOIN FETCH j.contratos c " +
            "LEFT JOIN FETCH c.club " +
            "LEFT JOIN FETCH j.datosDeportivos " +
            "WHERE j.idPersonal = :id")
    Optional<JugadorJPAEntity> findByIdWithContratos(@Param("id") UUID id);

     @Query("""
           SELECT DISTINCT j FROM JugadorJPAEntity j
           JOIN j.contratos c
           WHERE c.club.idEquipo = :idClub
             AND c.estado = 'ACTIVO'
             AND c.fechaInicio <= CURRENT_TIMESTAMP
             AND c.fechaFin >= CURRENT_TIMESTAMP
           """)
    List<JugadorJPAEntity> findByClub(@Param("idClub") UUID idClub);
 
    // Jugadores con un estado específico en sus datos deportivos
    @Query("SELECT j FROM JugadorJPAEntity j WHERE j.datosDeportivos.estadoJugador = :estado")
    List<JugadorJPAEntity> findByEstado(@Param("estado") EstadoJugador estado);
 
    // Jugadores con una posición específica (usa MEMBER OF porque posiciones es una lista)
    @Query("SELECT j FROM JugadorJPAEntity j WHERE :posicion MEMBER OF j.datosDeportivos.posiciones")
    List<JugadorJPAEntity> findByPosicion(@Param("posicion") PosicionJugador posicion);
 
    // Jugadores disponibles (TITULAR o SUPLENTE)
    @Query("""
           SELECT j FROM JugadorJPAEntity j
           WHERE j.datosDeportivos.estadoJugador IN ('TITULAR','SUPLENTE')
           """)
    List<JugadorJPAEntity> findDisponibles();
 
    // Jugadores lesionados
    @Query("SELECT j FROM JugadorJPAEntity j WHERE j.datosDeportivos.estadoJugador = 'LESIONADO'")
    List<JugadorJPAEntity> findLesionados();

    @Query("SELECT j FROM JugadorJPAEntity j " +
            "WHERE LOWER(CONCAT(COALESCE(j.nombre, ''), ' ', COALESCE(j.apellido, ''))) " +
            "LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<JugadorJPAEntity> buscarJugadorPorTexto(@Param("texto") String texto, Pageable pageable);




}
