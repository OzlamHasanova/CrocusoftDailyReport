package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.ReportDto;
import az.crocusoft.CrocusoftDailyReport.dto.ReportUpdateDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.ReportRequestForCreate;
import az.crocusoft.CrocusoftDailyReport.dto.response.DailyReportFilterAdminResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.DailyReportResponse;
import az.crocusoft.CrocusoftDailyReport.model.DailyReport;
import az.crocusoft.CrocusoftDailyReport.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("v1/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DailyReportResponse> createReport(@RequestBody ReportRequestForCreate reportDTO){
        DailyReportResponse response=reportService.createReport(reportDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyReportResponse> updateReport(@PathVariable Long id, @RequestBody ReportUpdateDto description) {
        return ResponseEntity.ok(reportService.updateReport(id, description));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getById(@PathVariable Long id){
        ReportDto reportDto=reportService.getById(id);
        return ResponseEntity.ok(reportDto);
    }
    @GetMapping("/filter")
    public ResponseEntity<?> filterDailyReports(
            @RequestParam(required = false) LocalDate createDate,
            @RequestParam(required = false) List<Long> projectIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int pageSize
    ) {
        Page<DailyReport> filteredReports = reportService.filterDailyReports( createDate, projectIds,  page, pageSize);

        if (filteredReports.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching reports found.");
        }

        return ResponseEntity.ok(filteredReports);
    }

    @GetMapping("/filter-admin")
    public ResponseEntity<Page<DailyReportFilterAdminResponse>> filterDailyReportsForAdmin(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "projectIds", required = false) List<Long> projectIds,
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int pageSize
    ) {
        Page<DailyReportFilterAdminResponse> searchResult = reportService.filterDailyReportsForAdmin(
                 startDate,endDate, projectIds, userIds, page,pageSize
        );
        return ResponseEntity.ok(searchResult);
    }
    @GetMapping("/filter-and-export-excel")
    public ResponseEntity<Page<DailyReportFilterAdminResponse>> filterDailyReportsAndExportExcel(
            HttpServletResponse response,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "projectIds", required = false) List<Long> projectIds,
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int pageSize
    ) throws IOException {
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setContentType("application/octet-stream");
        String headerKey="Content-Disposition";
        String headerValue="attachment;filename=Daily-reports.xls";
        response.setHeader(headerKey,headerValue);
        Page<DailyReportFilterAdminResponse> searchResult = reportService.generateDailyReportExcel(
               response, startDate,endDate, projectIds, userIds,pageable
        );
        return ResponseEntity.ok(searchResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}