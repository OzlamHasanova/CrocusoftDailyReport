package az.crocusoft.CrocusoftDailyReport.service;


import az.crocusoft.CrocusoftDailyReport.dto.ReportDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.ReportRequestForCreate;
import az.crocusoft.CrocusoftDailyReport.dto.response.DailyReportResponse;
import az.crocusoft.CrocusoftDailyReport.exception.DailyReportNotFoundException;
import az.crocusoft.CrocusoftDailyReport.exception.UpdateTimeException;
import az.crocusoft.CrocusoftDailyReport.model.DailyReport;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.repository.ProjectRepository;
import az.crocusoft.CrocusoftDailyReport.repository.ReportRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    public DailyReportResponse createReport(ReportRequestForCreate reportdto, Authentication authentication) {
        DailyReport report = new DailyReport();
        report.setUser(userRepository.findById(reportdto.getId()).get()     );

        report.setDescription(reportdto.getDescription());
        report.setCreateDate(LocalDate.now());
        report.setProject(projectRepository.findById(reportdto.getProjectId()).get());//todo: bunu serviceden getirmelisen
        reportRepository.save(report);
        DailyReportResponse response=mapToDailyReportResponse(report);
        return response;
    }

    public DailyReportResponse updateReport(Long id, String description) {
        DailyReport existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new DailyReportNotFoundException("Report not found."));

        // Eğer raporun create date'i mevcut create date'den farklı ise güncelleme yapma
        if (!existingReport.getCreateDate().equals(LocalDate.now())) {
            throw new UpdateTimeException("The report's creation date cannot be updated.");
        }

        existingReport.setDescription(description);
        reportRepository.save(existingReport);
        DailyReportResponse response=mapToDailyReportResponse(existingReport);
        return response;
    }



//    public List<Report> getAllReports() {
//        return reportRepository.findAll();
//    }

    public void deleteReport(Long id) {
        DailyReport report = reportRepository.findById(id).get();
        reportRepository.delete(report);
    }

    public ReportDto getById(Long id) {
        DailyReport dailyReport = reportRepository.findById(id)
                .orElseThrow(() -> new DailyReportNotFoundException("Daily report not found with id: " + id));
        ReportDto reportDto = new ReportDto();
        reportDto.setEmployeeId(dailyReport.getUser().getId());
        reportDto.setDescription(dailyReport.getDescription());
        reportDto.setProjectId(dailyReport.getProject().getId());

        return reportDto;
    }
    public Page<DailyReport> filterDailyReports(String description, LocalDate createDate, Long projectId, List<Long> userIds, int page, int pageSize) {
        Pageable pageable =  PageRequest.of(page, pageSize);
        return reportRepository.findByFilterCriteria(description, createDate, projectId, userIds, pageable);

    }

    public DailyReportResponse mapToDailyReportResponse(DailyReport report) {
        DailyReportResponse response = new DailyReportResponse();
        response.setDescription(report.getDescription());
        response.setProjectId(report.getProject().getId());
        return response;
    }
}