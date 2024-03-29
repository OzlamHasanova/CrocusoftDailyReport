package az.crocusoft.CrocusoftDailyReport.dto.request;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "First name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only letters")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstname;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name must contain only letters")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastname;

    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @JsonProperty("team_id")
    private Long teamId;


    private Role role;
}