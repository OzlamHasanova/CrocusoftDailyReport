package az.crocusoft.CrocusoftDailyReport.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeamRequest {
    @JsonProperty("team_name")
    private String teamName;
}
