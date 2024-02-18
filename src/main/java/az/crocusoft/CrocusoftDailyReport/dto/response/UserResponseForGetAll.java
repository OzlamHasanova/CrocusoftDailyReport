package az.crocusoft.CrocusoftDailyReport.dto.response;

import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseForGetAll {
    @JsonProperty("user_id")
    private Long userId;
    private String fullname;
    private String email;
    private String teamName;
    private String status;
    private String role;
}
