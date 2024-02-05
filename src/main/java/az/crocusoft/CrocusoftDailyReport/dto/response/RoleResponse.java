package az.crocusoft.CrocusoftDailyReport.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class RoleResponse {
    @JsonProperty("role_id")
    private Integer id;
    @JsonProperty("role_name")
    private String name;
}
