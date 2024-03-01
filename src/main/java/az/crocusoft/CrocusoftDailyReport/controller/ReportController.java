package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.constant.PaginationConstants;
import az.crocusoft.CrocusoftDailyReport.dto.ReportDto;
import az.crocusoft.CrocusoftDailyReport.dto.ReportFilterResponseWithPaginationForAdmin;
import az.crocusoft.CrocusoftDailyReport.dto.ReportUpdateDto;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponse;
import az.crocusoft.CrocusoftDailyReport.dto.request.ReportRequestForCreate;
import az.crocusoft.CrocusoftDailyReport.dto.response.DailyReportFilterAdminResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.DailyReportResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.ReportFilterResponseForUser;
import az.crocusoft.CrocusoftDailyReport.dto.response.ReportFilterResponseWithPaginationForUser;
import az.crocusoft.CrocusoftDailyReport.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReportFilterResponseForUser> getById(@PathVariable Long id){
        ReportFilterResponseForUser reportDto=reportService.getById(id);
        return ResponseEntity.ok(reportDto);
    }
    @GetMapping("/filter")
    public ResponseEntity<ReportFilterResponseWithPaginationForUser> filterDailyReports(
            @RequestParam(value = "startDate",required = false) LocalDate startDate,
            @RequestParam(value = "endDate",required = false) LocalDate endDate,
            @RequestParam(value = "projectIds",required = false) List<Long> projectIds,
            @RequestParam(name = "pageNumber", defaultValue = PaginationConstants.PAGE_NUMBER) Integer page,
            @RequestParam(name = "pageSize", defaultValue = PaginationConstants.PAGE_SIZE) Integer size
    ) {
        return ResponseEntity.ok(reportService.filterDailyReports( startDate,endDate, projectIds,  page, size));
    }

    @GetMapping("/filter-admin")
    public ResponseEntity<ReportFilterResponseWithPaginationForAdmin> filterDailyReportsForAdmin(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "projectIds", required = false) List<Long> projectIds,
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            @RequestParam(name = "pageNumber", defaultValue = PaginationConstants.PAGE_NUMBER) Integer page,
            @RequestParam(name = "pageSize", defaultValue = PaginationConstants.PAGE_SIZE) Integer size
    ) {
        return ResponseEntity.ok(reportService.filterDailyReportsForAdmin( startDate,endDate, projectIds,userIds, page, size));
    }
    @GetMapping("/filter-and-export-excel")
    public ResponseEntity<ReportFilterResponseWithPaginationForAdmin> filterDailyReportsAndExportExcel(
            HttpServletResponse response,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "projectIds", required = false) List<Long> projectIds,
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            @RequestParam(name = "pageNumber", defaultValue = PaginationConstants.PAGE_NUMBER) Integer page,
            @RequestParam(name = "pageSize", defaultValue = PaginationConstants.PAGE_SIZE) Integer size
    ) throws IOException {

        response.setContentType("application/octet-stream");
        String headerKey="Content-Disposition";
        String headerValue="attachment;filename=Daily-reports.xls";
        response.setHeader(headerKey,headerValue);

        return ResponseEntity.ok(reportService.generateDailyReportExcel(response, startDate,endDate, projectIds,userIds, page, size));
    }


}