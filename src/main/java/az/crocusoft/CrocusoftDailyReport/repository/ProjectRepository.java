package az.crocusoft.CrocusoftDailyReport.repository;

import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("SELECT p FROM Project p WHERE (:projectName IS NULL OR LOWER(p.name) LIKE %:projectName%) ORDER BY p.createDate DESC")
    Page<Project> findByNameContainingIgnoreCaseOrderByCreationDateDesc(@Param("projectName") String projectName,
                                                                        Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.users u WHERE (:projectName IS NULL OR LOWER(p.name) LIKE %:projectName%) AND (:userId IS NULL OR u.id = :userId) ORDER BY p.createDate DESC")
    Page<Project> findByNameContainingIgnoreCaseAndUserIdOrderByCreationDateDesc(@Param("projectName") String projectName,
                                                                                 @Param("userId") Long userId,
                                                                                 Pageable pageable);
//    boolean existsByNameAndUsersContains(String projectName, UserEntity user);
    boolean existsProjectByName(String name);
}
