package az.crocusoft.CrocusoftDailyReport.service;


import az.crocusoft.CrocusoftDailyReport.dto.ReportDto;
import az.crocusoft.CrocusoftDailyReport.dto.ReportUpdateDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.ReportRequestForCreate;
import az.crocusoft.CrocusoftDailyReport.dto.response.*;
import az.crocusoft.CrocusoftDailyReport.exception.DailyReportNotFoundException;
import az.crocusoft.CrocusoftDailyReport.exception.UpdateTimeException;
import az.crocusoft.CrocusoftDailyReport.model.DailyReport;
import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final TeamService teamService;
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

    public DailyReportResponse updateReport(Long id, ReportUpdateDto reportUpdateDto) {
        logger.info("Updating report with id: {}", id);

        DailyReport existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new DailyReportNotFoundException("Report not found."));

        if (!existingReport.getCreateDate().equals(LocalDate.now())) {
            logger.error("Report creation date cannot be updated for report with id: {}", id);
            throw new UpdateTimeException("The report's creation date cannot be updated.");
        }

        existingReport.setDescription(reportUpdateDto.getDescription());
        reportRepository.save(existingReport);

        DailyReportResponse response = mapToDailyReportResponse(existingReport);

        logger.info("Report updated successfully");
        return response;
    }

    public ReportFilterResponseForUser getById(Long id) {
        logger.info("Getting report by id: {}", id);

        DailyReport dailyReport = reportRepository.findById(id)
                .orElseThrow(() -> new DailyReportNotFoundException("Daily report not found with id: " + id));

        ReportFilterResponseForUser reportDto = new ReportFilterResponseForUser();
        reportDto.setId(dailyReport.getUser().getId());
        reportDto.setDescription(dailyReport.getDescription());
        reportDto.setProject(mapToProjectDto(projectRepository.findById(dailyReport.getProject().getId()).get()));
        reportDto.setCreatDate(dailyReport.getCreateDate());

        logger.info("Report retrieved successfully");
        return reportDto;
    }

    public Page<ReportFilterResponseForUser> filterDailyReports(LocalDate startDate, LocalDate endDate, List<Long> projectIds, int page, int pageSize) {
        int newPage=page-1;
        logger.info("Filtering daily reports");
        Long userId=authenticationService.getSignedInUser().getId();

        Pageable pageable = PageRequest.of(newPage, pageSize);
        Page<DailyReport> filteredReports = reportRepository.findByFilter(startDate,endDate, projectIds, userId,pageable);

        List<ReportFilterResponseForUser> responseList = filteredReports.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        logger.info("Daily reports filtered successfully");
        return new PageImpl<>(responseList, pageable, filteredReports.getTotalElements());
    }


    public DailyReportResponse mapToDailyReportResponse(DailyReport report) {
        DailyReportResponse response = new DailyReportResponse();
        response.setDescription(report.getDescription());
        response.setProjectId(report.getProject().getId());
        return response;
    }

    public Page<DailyReportFilterAdminResponse> filterDailyReportsForAdmin(LocalDate startDate, LocalDate endDate, List<Long> projectIds, List<Long> userIds, int page, int pageSize) {
        logger.info("Filtering daily reports for admin");
        int newPage=page-1;
        Pageable pageable = PageRequest.of(newPage, pageSize);
        Page<DailyReport> filteredReports = reportRepository.findByFilterCriteria(startDate, endDate, projectIds, userIds, pageable);

        List<DailyReportFilterAdminResponse> responseList = filteredReports.stream()
                .map(this::mapToAdminResponse)
                .collect(Collectors.toList());

        logger.info("Daily reports filtered successfully for admin");
        return new PageImpl<>(responseList, pageable, filteredReports.getTotalElements());
    }

    private DailyReportFilterAdminResponse mapToAdminResponse(DailyReport dailyReport) {
        DailyReportFilterAdminResponse response = new DailyReportFilterAdminResponse();
        response.setId(dailyReport.getId());
        response.setDescription(dailyReport.getDescription());
        response.setUser(mapToUserResponse(dailyReport.getUser()));
        response.setProject(mapToProjectDto(dailyReport.getProject()));
        response.setCreatDate(dailyReport.getCreateDate());
        return response;
    }
    private ReportFilterResponseForUser mapToUserResponse(DailyReport dailyReport) {
        ReportFilterResponseForUser response = new ReportFilterResponseForUser();
        response.setId(dailyReport.getId());
        response.setDescription(dailyReport.getDescription());
        response.setProject(mapToProjectDto(dailyReport.getProject()));
        response.setCreatDate(dailyReport.getCreateDate());
        return response;
    }


    private UserResponse mapToUserResponse(UserEntity user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setSurname(user.getSurname());
        response.setTeamName(user.getTeam().getName());

        return response;
    }

    private ProjectDtoForGetApi mapToProjectDto(Project project) {
        ProjectDtoForGetApi dto = new ProjectDtoForGetApi();
        dto.setId(project.getId());
        dto.setName(project.getName());
        return dto;
    }

    public Page<DailyReportFilterAdminResponse> generateDailyReportExcel(
            HttpServletResponse httpServletResponse,
            LocalDate startDate,
            LocalDate endDate,
            List<Long> projectIds,
            List<Long> userIds,
            Pageable pageable) throws IOException {
        logger.info("Generating daily report Excel");


        Page<DailyReport> reports = reportRepository.findByFilterCriteria(
                startDate,
                endDate,
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
        List<DailyReportFilterAdminResponse> responseList = reports.stream()
                .map(this::mapToAdminResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responseList, pageable, reports.getTotalElements());

    }
}