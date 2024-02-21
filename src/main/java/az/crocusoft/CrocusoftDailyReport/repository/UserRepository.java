package az.crocusoft.CrocusoftDailyReport.repository;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;

import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
//    Optional<UserEntity> findByEmailAndStatus(String email,Status status);
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.isDeleted = false " +
            "AND (:name IS NULL OR u.name = :name) " +
            "AND (:surname IS NULL OR u.surname = :surname) " +
            "AND (:teamIds IS NULL OR u.team.Id IN :teamIds) " +
            "AND (:projectIds IS NULL OR EXISTS (SELECT p FROM u.projects p WHERE p.Id IN :projectIds))")
    Page<UserEntity> filterUsers(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("teamIds") List<Long> teamIds,
            @Param("projectIds") List<Long> projectIds,
            Pageable pageable);

  UserEntity findByEmail(String email);

    Optional<UserEntity> findByIdAndStatus(Long id,Status status);
    Optional<UserEntity> findByIdAndIsDeleted(Long id,Boolean isDeleted);

    List<UserEntity> findAllByIsDeleted(boolean deleted);

}
