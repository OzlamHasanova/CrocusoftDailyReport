package az.crocusoft.CrocusoftDailyReport.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProjectResponseForFilter {
    @JsonProperty("report_id")
    private Long reportId;
    private String name;
    private List<UserResponse> user;
}
