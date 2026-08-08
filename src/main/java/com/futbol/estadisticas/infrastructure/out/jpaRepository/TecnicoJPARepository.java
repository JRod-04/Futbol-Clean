package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.TecnicoJPAEntity;

public interface TecnicoJPARepository extends JpaRepository <TecnicoJPAEntity,UUID>{
    @Query("SELECT t FROM TecnicoJPAEntity t WHERE t.equipoActual.idEquipo = :idEquipo")
    List<TecnicoJPAEntity> findByEquipo(@Param("idEquipo") UUID idEquipo);
 
    @Query("SELECT t FROM TecnicoJPAEntity t WHERE t.equipoActual.idEquipo = :idEquipo")
    Optional<TecnicoJPAEntity> findTecnicoActualByEquipo(@Param("idEquipo") UUID idEquipo);

    @Query("SELECT t FROM TecnicoJPAEntity t " +
            "WHERE LOWER(CONCAT(COALESCE(t.nombre, ''), ' ', COALESCE(t.apellido, ''))) " +
            "LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<TecnicoJPAEntity> buscarTecnicoPorTexto(@Param("texto") String texto, Pageable pageable);
}
