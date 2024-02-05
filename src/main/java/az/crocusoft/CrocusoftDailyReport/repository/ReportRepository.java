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
//    @Query("SELECT dr FROM DailyReport dr " +
//            "WHERE (:createDate IS NULL OR dr.createDate = :createDate) " +
//            "AND (:projectIds IS NULL OR dr.project.Id IN (:projectIds) ) " +
//            "AND (:userIds IS NULL OR dr.user.id IN (:userIds))")
//    Page<DailyReport> findByFilterCriteria(
//            @Param("createDate") LocalDate createDate,
//            @Param("projectId") List<Long> projectIds,
//            @Param("userIds") List<Long> userIds,
//            Pageable pageable
//    );

    @Query("SELECT dr FROM DailyReport dr " +
            "JOIN dr.user u " +
            "WHERE (:createDate IS NULL OR dr.createDate = :createDate) " +
            "AND (:projectIds IS NULL OR dr.project.Id IN (:projectIds)) " +
            "AND (:userIds IS NULL OR u.id IN (:userIds))")
    Page<DailyReport> findByFilterCriteria(
            @Param("createDate") LocalDate createDate,
            @Param("projectIds") List<Long> projectIds,
            @Param("userIds") List<Long> userIds,
            Pageable pageable
    );

}
