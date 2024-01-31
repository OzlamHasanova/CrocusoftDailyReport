package az.crocusoft.CrocusoftDailyReport.service;


import az.crocusoft.CrocusoftDailyReport.dto.ReportDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.ReportRequest;
import az.crocusoft.CrocusoftDailyReport.model.DailyReport;
import az.crocusoft.CrocusoftDailyReport.repository.ProjectRepository;
import az.crocusoft.CrocusoftDailyReport.repository.ReportRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    public DailyReport createReport(ReportDto reportDTO) {
        DailyReport report = new DailyReport();
        report.setUser(userRepository.findById(reportDTO.getEmployeeId()).get());
        report.setDescription(reportDTO.getDescription());
        report.setCreateDate(LocalDate.now());
        report.setProject(projectRepository.findById(reportDTO.getProjectId()).get());//todo: bunu serviceden getirmelisen
        return reportRepository.save(report);
    }

    public DailyReport updateReport(Long id, String description) {
        DailyReport existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapor bulunamadı."));

        // Eğer raporun create date'i mevcut create date'den farklı ise güncelleme yapma
        if (!existingReport.getCreateDate().equals(LocalDate.now())) {
            throw new IllegalArgumentException("Raporun create date'i güncellenemez.");
        }

        existingReport.setDescription(description);

        return reportRepository.save(existingReport);
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
                .orElseThrow(() -> new IllegalArgumentException("Daily report not found with id: " + id));
        ReportDto reportDto = new ReportDto();
        reportDto.setEmployeeId(dailyReport.getUser().getId());
        reportDto.setDescription(dailyReport.getDescription());
        reportDto.setProjectId(dailyReport.getProject().getId());

        return reportDto;
    }
    public List<DailyReport> filterDailyReports(String description, LocalDate createDate, Long projectId, List<Long> userIds) {
        return reportRepository.findByFilterCriteria(description, createDate, projectId, userIds);
    }
}