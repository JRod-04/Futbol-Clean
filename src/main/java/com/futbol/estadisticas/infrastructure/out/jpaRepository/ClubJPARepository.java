package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;

public interface ClubJPARepository extends JpaRepository <ClubJPAEntity, UUID>{

    @Query("SELECT c FROM ClubJPAEntity c " +
            "LEFT JOIN FETCH c.contratos " +
            "LEFT JOIN FETCH c.contratos.personal " +
            "WHERE c.idEquipo = :id")
    Optional<ClubJPAEntity> findByIdWithContratos(@Param("id") UUID id);

    @Query("SELECT c FROM ClubJPAEntity c " +
            "LEFT JOIN FETCH c.estadio " +
            "LEFT JOIN FETCH c.tecnicoActual " +
            "LEFT JOIN FETCH c.contratos " +
            "LEFT JOIN FETCH c.contratos.personal " +
            "LEFT JOIN FETCH c.contratos.personal.datosDeportivos " +
            "WHERE c.idEquipo = :id")
    Optional<ClubJPAEntity> findByIdWithDetails(@Param("id") UUID id);


    @Query("SELECT c FROM ClubJPAEntity c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<ClubJPAEntity> findByNombre(@Param("nombre") String nombre);

    @Modifying
    @Query("UPDATE ClubJPAEntity c SET c.tecnicoActual.idPersonal = :idTecnico WHERE c.idEquipo = :idClub")
    void actualizarTecnicoActual(@Param("idClub") UUID idClub, @Param("idTecnico") UUID idTecnico);

    @Query("SELECT c FROM ClubJPAEntity c " +
            "WHERE LOWER(CONCAT(COALESCE(c.nombre, ''), ' ', COALESCE(c.nombreCorto, ''))) " +
            "LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<ClubJPAEntity> buscarClubPorTexto(@Param("texto") String texto, Pageable pageable);
}
