package az.crocusoft.CrocusoftDailyReport.repository;

import az.crocusoft.CrocusoftDailyReport.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("SELECT p FROM Project p WHERE (:projectName IS NULL OR LOWER(p.name) LIKE %:projectName%)")
    List<Project> findByNameContainingIgnoreCase(@Param("projectName") String projectName);


    boolean existsProjectByName(String name);
}
