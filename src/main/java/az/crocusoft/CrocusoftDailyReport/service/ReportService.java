package az.crocusoft.CrocusoftDailyReport.service;


import az.crocusoft.CrocusoftDailyReport.dto.ReportDto;
import az.crocusoft.CrocusoftDailyReport.dto.ReportUpdateDto;
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
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);



    public DailyReportResponse createReport(ReportRequestForCreate reportdto) {
        logger.info("Creating report");

        DailyReport report = new DailyReport();
        report.setUser(authenticationService.getSignedInUser());
        report.setDescription(reportdto.getDescription());
        report.setCreateDate(LocalDate.now());
        report.setProject(projectRepository.findById(reportdto.getProjectId()).get());
        reportRepository.save(report);

        DailyReportResponse response = mapToDailyReportResponse(report);

        logger.info("Report created successfully");
        return response;
    }

    public DailyReportResponse updateReport(Long id, ReportUpdateDto description) {
        logger.info("Updating report with id: {}", id);

        DailyReport existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new DailyReportNotFoundException("Report not found."));

        if (!existingReport.getCreateDate().equals(LocalDate.now())) {
            logger.error("Report creation date cannot be updated for report with id: {}", id);
            throw new UpdateTimeException("The report's creation date cannot be updated.");
        }

        existingReport.setDescription(description.getDescription());
        reportRepository.save(existingReport);

        DailyReportResponse response = mapToDailyReportResponse(existingReport);

        logger.info("Report updated successfully");
        return response;
    }

    public void deleteReport(Long id) {
        logger.info("Deleting report with id: {}", id);

        DailyReport report = reportRepository.findById(id).get();
        reportRepository.delete(report);

        logger.info("Report deleted successfully");
    }

    public ReportDto getById(Long id) {
        logger.info("Getting report by id: {}", id);

        DailyReport dailyReport = reportRepository.findById(id)
                .orElseThrow(() -> new DailyReportNotFoundException("Daily report not found with id: " + id));

        ReportDto reportDto = new ReportDto();
        reportDto.setEmployeeId(dailyReport.getUser().getId());
        reportDto.setDescription(dailyReport.getDescription());
        reportDto.setProjectId(dailyReport.getProject().getId());

        logger.info("Report retrieved successfully");
        return reportDto;
    }

    public Page<DailyReport> filterDailyReports(LocalDate createDate, List<Long> projectIds, int page, int pageSize) {
        logger.info("Filtering daily reports");

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<DailyReport> filteredReports = reportRepository.findByFilter(createDate, projectIds, pageable);

        logger.info("Daily reports filtered successfully");
        return filteredReports;
    }


    public DailyReportResponse mapToDailyReportResponse(DailyReport report) {
        DailyReportResponse response = new DailyReportResponse();
        response.setDescription(report.getDescription());
        response.setProjectId(report.getProject().getId());
        return response;
    }

    public Page<DailyReport> filterDailyReportsForAdmin(LocalDate createDate, List<Long> projectIds, List<Long> userIds, int page, int pageSize) {
        logger.info("Filtering daily reports for admin");

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<DailyReport> filteredReports = reportRepository.findByFilterCriteria(createDate, projectIds, userIds, pageable);

        logger.info("Daily reports filtered successfully for admin");
        return filteredReports;
    }

    public Page<DailyReport> generateDailyReportExcel(
            HttpServletResponse httpServletResponse,
            LocalDate creatDate,
            List<Long> projectIds,
            List<Long> userIds,
            Pageable pageable) throws IOException {
        logger.info("Generating daily report Excel");


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
        logger.info("Daily report Excel generated successfully");

        return reports;

    }
}