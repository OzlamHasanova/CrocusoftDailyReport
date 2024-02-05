package az.crocusoft.CrocusoftDailyReport.service;


import az.crocusoft.CrocusoftDailyReport.dto.ReportDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.ReportRequestForCreate;
import az.crocusoft.CrocusoftDailyReport.dto.response.DailyReportResponse;
import az.crocusoft.CrocusoftDailyReport.exception.DailyReportNotFoundException;
import az.crocusoft.CrocusoftDailyReport.exception.UpdateTimeException;
import az.crocusoft.CrocusoftDailyReport.model.DailyReport;
import az.crocusoft.CrocusoftDailyReport.repository.ProjectRepository;
import az.crocusoft.CrocusoftDailyReport.repository.ReportRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        report.setProject(projectRepository.findById(reportdto.getProjectId()).get());
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
    public Page<DailyReport> filterDailyReports( LocalDate createDate, List<Long> projectIds, List<Long> userIds, int page, int pageSize) {
        Pageable pageable =  PageRequest.of(page, pageSize);
        return reportRepository.findByFilterCriteria( createDate, projectIds, userIds, pageable);

    }

    public DailyReportResponse mapToDailyReportResponse(DailyReport report) {
        DailyReportResponse response = new DailyReportResponse();
        response.setDescription(report.getDescription());
        response.setProjectId(report.getProject().getId());
        return response;
    }
    public Page<DailyReport> filterDailyReportsForAdmin( LocalDate createDate, List<Long> projectIds, List<Long> userIds, int page, int pageSize) {
        Pageable pageable =  PageRequest.of(page, pageSize);
        return reportRepository.findByFilterCriteria( createDate, projectIds, userIds, pageable);

    }

    public Page<DailyReport> generateDailyReportExcel(
            HttpServletResponse httpServletResponse,
            LocalDate creatDate,
            List<Long> projectIds,
            List<Long> userIds,
            Pageable pageable) throws IOException {

        Page<DailyReport> reports = reportRepository.findByFilterCriteria(
                creatDate,
                projectIds,
                userIds,
                pageable
        );
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet("Product with Variation Info");
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("Daily Report ID");
        row.createCell(1).setCellValue("UserId");
        row.createCell(2).setCellValue("User FirstName");
        row.createCell(3).setCellValue("User LastName");
        row.createCell(4).setCellValue("LocalDate");
        row.createCell(5).setCellValue("DailyReport Description");
        row.createCell(6).setCellValue("ProjectName");

        int dataRowIndex = 1;

        for (DailyReport report : reports) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(report.getId());
            dataRow.createCell(1).setCellValue(report.getUser().getId());
            dataRow.createCell(2).setCellValue(report.getUser().getName());
            dataRow.createCell(3).setCellValue(report.getUser().getSurname());
            dataRow.createCell(4).setCellValue(report.getCreateDate());
            dataRow.createCell(5).setCellValue(report.getDescription());
            dataRow.createCell(6).setCellValue(report.getProject().getName());

            dataRowIndex++;
        }

        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        hssfWorkbook.write(outputStream);
        hssfWorkbook.close();
        outputStream.close();
        return reports;
    }
}