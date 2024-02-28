package az.crocusoft.CrocusoftDailyReport.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportFilterResponseForUser {
    private Long id;
    private LocalDate creatDate;
    private String description;
    private ProjectDtoForGetApi project;
}
