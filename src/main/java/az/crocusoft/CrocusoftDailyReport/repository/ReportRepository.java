package az.crocusoft.CrocusoftDailyReport.repository;

import az.crocusoft.CrocusoftDailyReport.model.DailyReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<DailyReport,Long> {
    @Query("SELECT dr FROM DailyReport dr " +
            "JOIN dr.user u " +
            "WHERE (COALESCE(:startDate, :endDate) IS NULL OR dr.createDate BETWEEN COALESCE(:startDate, dr.createDate) AND COALESCE(:endDate, dr.createDate)) " +
            "AND (:projectIds IS NULL OR dr.project.Id IN (:projectIds)) " +
            "AND (:userIds IS NULL OR u.id IN (:userIds))")
    Page<DailyReport> findByFilterCriteria(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("projectIds") List<Long> projectIds,
            @Param("userIds") List<Long> userIds,
            Pageable pageable
    );
    @Query("SELECT dr FROM DailyReport dr " +
            "JOIN dr.user u " +
            "WHERE (COALESCE(:startDate, :endDate) IS NULL OR dr.createDate BETWEEN COALESCE(:startDate, dr.createDate) AND COALESCE(:endDate, dr.createDate)) " +
            "AND (:projectIds IS NULL OR dr.project.Id IN (:projectIds)) " +
            "AND (:userId IS NULL OR u.id = :userId)")
    Page<DailyReport> findByFilter(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("projectIds") List<Long> projectIds,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
