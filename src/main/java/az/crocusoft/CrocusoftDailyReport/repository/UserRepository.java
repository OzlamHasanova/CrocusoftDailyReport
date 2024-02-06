package az.crocusoft.CrocusoftDailyReport.repository;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;

import az.crocusoft.CrocusoftDailyReport.model.enums.RoleEnum;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
//    Optional<UserEntity> findByEmailAndStatus(String email,Status status);
    Boolean existsByEmail(String email);
    Boolean existsByName(String name);
    boolean existsByRole(Role role);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE (:name IS NULL OR u.name = :name) " +
            "AND (:surname IS NULL OR u.surname = :surname) " +
            "AND (:teamIds IS NULL OR u.team.Id IN :teamIds) " +
            "AND (:projectIds IS NULL OR EXISTS (SELECT p FROM u.projects p WHERE p.Id IN :projectIds))")
    List<UserEntity> filterUsers(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("teamIds") List<Long> teamIds,
            @Param("projectIds") List<Long> projectIds);
  UserEntity findByEmail(String email);

//    Optional<UserEntity> findBy(String name);
}
