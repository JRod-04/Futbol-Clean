package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;

public interface ClubJPARepository extends JpaRepository <ClubJPAEntity, UUID>{
@Query("SELECT c FROM ClubJPAEntity c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<ClubJPAEntity> findByNombre(@Param("nombre") String nombre);
}
