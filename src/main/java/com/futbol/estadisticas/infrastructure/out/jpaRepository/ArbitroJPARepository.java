package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.ArbitroJPAEntity;

public interface ArbitroJPARepository extends JpaRepository<ArbitroJPAEntity,UUID>{
   @Query("SELECT a FROM ArbitroJPAEntity a WHERE " +
           "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(a.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<ArbitroJPAEntity> findByNombreOrApellido(@Param("termino") String termino);
}
