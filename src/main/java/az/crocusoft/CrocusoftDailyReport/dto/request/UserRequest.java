package az.crocusoft.CrocusoftDailyReport.dto.request;

import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String surname;
    private String email;
    private Integer roleId;
    private Long teamId;
}
