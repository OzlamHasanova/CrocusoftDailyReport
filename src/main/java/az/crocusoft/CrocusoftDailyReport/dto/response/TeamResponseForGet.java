package az.crocusoft.CrocusoftDailyReport.dto.response;

import az.crocusoft.CrocusoftDailyReport.dto.TeamMemberDto;
import lombok.Data;

import java.util.List;

@Data
public class TeamResponseForGet {
    private String name;
    private List<TeamMemberDto> members;
}
