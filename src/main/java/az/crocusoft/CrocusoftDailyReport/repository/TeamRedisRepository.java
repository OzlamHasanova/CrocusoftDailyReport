package az.crocusoft.CrocusoftDailyReport.repository;

import az.crocusoft.CrocusoftDailyReport.model.TeamRedis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface TeamRedisRepository extends CrudRepository<TeamRedis,Long> {
}
