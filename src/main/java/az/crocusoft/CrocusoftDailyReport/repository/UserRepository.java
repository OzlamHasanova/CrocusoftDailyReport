package az.crocusoft.CrocusoftDailyReport.repository;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;

import az.crocusoft.CrocusoftDailyReport.model.enums.RoleEnum;
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

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.isDeleted = false " +
            "AND (:name IS NULL OR u.name = :name) " +
            "AND (:surname IS NULL OR u.surname = :surname) " +
            "AND (:teamIds IS NULL OR u.team.Id IN :teamIds) " +
            "AND (:projectIds IS NULL OR EXISTS (SELECT p FROM u.projects p WHERE p.Id IN :projectIds))"+
    " AND (u.roleEnum ='EMPLOYEE' OR (u.roleEnum='ADMIN' AND u.id=:userId))")
    Page<UserEntity> filterAdmin(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("teamIds") List<Long> teamIds,
            @Param("projectIds") List<Long> projectIds,
            @Param("userId") Long userId,
            Pageable pageable);
  Optional<UserEntity> findByEmail(String email);
  UserEntity findByOtp(String otp);

  UserEntity findByEmailAndIsDeletedAndStatus(String email,Boolean isDeleted,Status status);
  List<UserEntity> findAllByRoleEnumAndIsDeletedAndStatus(RoleEnum roleEnum,Boolean isDeleted,Status status);

  Optional<UserEntity> findByIdAndIsDeletedAndStatus(Long id,Boolean isDeleted,Status status);


    boolean existsByRoleEnum(RoleEnum roleEnum);
    boolean existsByIdAndNameAndSurnameAndEmailAndTeamIdAndRoleEnum(Long id,String name,String surname,String email,Long teamId,RoleEnum role);
}
