package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.CompeticionJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EquipoJPAEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipoJPARepository extends JpaRepository <EquipoJPAEntity, UUID>{

    @Query("SELECT c FROM EquipoJPAEntity c " +
            "LEFT JOIN FETCH c.contratos " +
            "LEFT JOIN FETCH c.contratos.personal " +
            "WHERE c.idEquipo = :id")
    Optional<EquipoJPAEntity> findByIdWithContratos(@Param("id") UUID id);

    @Query("SELECT c FROM EquipoJPAEntity c " +
            "LEFT JOIN FETCH c.estadio " +
            "LEFT JOIN FETCH c.tecnicoActual " +
            "LEFT JOIN FETCH c.contratos " +
            "LEFT JOIN FETCH c.contratos.personal " +
            "LEFT JOIN FETCH c.contratos.personal.datosDeportivos " +
            "WHERE c.idEquipo = :id")
    Optional<EquipoJPAEntity> findByIdWithDetails(@Param("id") UUID id);


    @Modifying
    @Query("UPDATE EquipoJPAEntity c SET c.tecnicoActual.idPersonal = :idTecnico WHERE c.idEquipo = :idEquipo")
    void actualizarTecnicoActual(@Param("idEquipo") UUID idEquipo, @Param("idTecnico") UUID idTecnico);

    @Query("SELECT c FROM EquipoJPAEntity c " +
            "WHERE LOWER(CONCAT(COALESCE(c.nombre, ''), ' ', COALESCE(c.nombreCorto, ''))) " +
            "LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<EquipoJPAEntity> buscarEquipoPorTexto(@Param("texto") String texto, Pageable pageable);

    @Query("SELECT DISTINCT c FROM CompeticionJPAEntity c " +
            "JOIN c.partidos p " +
            "WHERE p.equipoLocal.idEquipo = :idEquipo " +
            "OR p.equipoVisitante.idEquipo = :idEquipo")
    List<CompeticionJPAEntity> findCompeticionesByEquipo (@Param("idEquipo") UUID idEquipo);
}
