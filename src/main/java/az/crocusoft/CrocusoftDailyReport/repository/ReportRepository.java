package az.crocusoft.CrocusoftDailyReport.repository;

import az.crocusoft.CrocusoftDailyReport.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<DailyReport,Long> {
    @Query("SELECT dr FROM DailyReport dr " +
            "WHERE (:description IS NULL OR dr.description LIKE %:description%) " +
            "AND (:createDate IS NULL OR dr.createDate = :createDate) " +
            "AND (:projectId IS NULL OR dr.project.Id = :projectId) " +
            "AND (:userIds IS NULL OR dr.user.id IN (:userIds))")
    List<DailyReport> findByFilterCriteria(
            @Param("description") String description,
            @Param("createDate") LocalDate createDate,
            @Param("projectId") Long projectId,
            @Param("userIds") List<Long> userIds
    );
}
