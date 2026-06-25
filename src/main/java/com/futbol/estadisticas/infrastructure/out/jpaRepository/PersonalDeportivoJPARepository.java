package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.domain.model.enums.TipoPersonal;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PersonalDeportivoJPAEntity;

public interface PersonalDeportivoJPARepository extends JpaRepository<PersonalDeportivoJPAEntity,UUID> {

    List<PersonalDeportivoJPAEntity> findByTipoPersonal(TipoPersonal tipoPersonal);
 
    @Query("SELECT p FROM PersonalDeportivoJPAEntity p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<PersonalDeportivoJPAEntity> findByNombreOrApellido(@Param("termino") String termino);
}

    
