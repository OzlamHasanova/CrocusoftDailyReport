package az.crocusoft.CrocusoftDailyReport.dto.response;

import az.crocusoft.CrocusoftDailyReport.dto.TeamMemberDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TeamResponse {
    @JsonProperty("team_id")
    private Long teamId;
    private String name;
    private List<TeamMemberDto> members;
}
