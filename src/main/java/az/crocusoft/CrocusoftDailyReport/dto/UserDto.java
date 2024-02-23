package az.crocusoft.CrocusoftDailyReport.dto;

import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectDtoForGetApi;
import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private Role role;
    private TeamDto team;
    private List<ProjectDtoForGetApi> project;
    private String status;
}
