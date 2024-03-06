package az.crocusoft.CrocusoftDailyReport.dto.request;

import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String surname;
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Pattern(regexp = ".+@crocusoft\\..+", message = "Email must be from crocusoft domain")
    private String email;
    private Integer roleId;
    private Long teamId;
}
