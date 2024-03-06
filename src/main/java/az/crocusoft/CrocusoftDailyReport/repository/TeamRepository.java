package az.crocusoft.CrocusoftDailyReport.repository;

import az.crocusoft.CrocusoftDailyReport.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
    boolean existsTeamByName(String name);
    boolean existsByIdAndName(Long id,String name);
}
