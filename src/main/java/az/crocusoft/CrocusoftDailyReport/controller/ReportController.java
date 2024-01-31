package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.ReportDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.ReportRequest;
import az.crocusoft.CrocusoftDailyReport.model.DailyReport;
import az.crocusoft.CrocusoftDailyReport.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("v1/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DailyReport> createReport(@RequestBody ReportDto reportDTO){
        return ResponseEntity.ok(reportService.createReport(reportDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyReport> updateReport(@PathVariable Long id, @RequestBody String description) {
        return ResponseEntity.ok(reportService.updateReport(id, description));
    }
    @GetMapping("get/{id}")
    public ResponseEntity<ReportDto> getById(@PathVariable Long id){
        ReportDto reportDto=reportService.getById(id);
        return ResponseEntity.ok(reportDto);
    }
    @GetMapping("/filter")
    public ResponseEntity<?> filterDailyReports(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) LocalDate createDate,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) List<Long> userIds,
            HttpServletResponse response
    ) {
        List<DailyReport> filteredReports = reportService.filterDailyReports(description, createDate, projectId, userIds);

        if (filteredReports.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching reports found.");
        }

        return ResponseEntity.ok(filteredReports);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}