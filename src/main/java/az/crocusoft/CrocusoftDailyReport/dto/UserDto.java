package az.crocusoft.CrocusoftDailyReport.dto;

import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import lombok.Data;

@Data
public class UserDto {
    private String name;
    private String surname;
    private String password;
    private Role role;
    private Team team;
    private String status;
}
