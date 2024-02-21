package az.crocusoft.CrocusoftDailyReport.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamDto {
    private Long Id;
    private String name;

}
