package az.crocusoft.CrocusoftDailyReport.dto;

import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String name;
    private String surname;
    private Role role;
    private Team team;
    private List<Project> project;
    private String status;
}
