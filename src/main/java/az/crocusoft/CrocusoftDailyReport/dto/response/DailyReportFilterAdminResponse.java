package az.crocusoft.CrocusoftDailyReport.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyReportFilterAdminResponse {
    private Long id;
    private LocalDate creatDate;
    private String description;
    private UserResponse user;
    private ProjectDtoForGetApi project;
}
