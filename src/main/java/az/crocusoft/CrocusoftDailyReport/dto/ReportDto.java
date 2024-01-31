package az.crocusoft.CrocusoftDailyReport.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportDto {
    private Long employeeId;
    private String description;
    private LocalDate createDate;
    private Long projectId;
}
