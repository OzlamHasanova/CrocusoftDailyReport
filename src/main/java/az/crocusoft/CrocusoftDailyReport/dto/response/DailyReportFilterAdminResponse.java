package az.crocusoft.CrocusoftDailyReport.dto.response;

import lombok.Data;

@Data
public class DailyReportFilterAdminResponse {
    private Long id;
    private String description;
    private UserResponse user;
    private ProjectDtoForGetApi project;
}
