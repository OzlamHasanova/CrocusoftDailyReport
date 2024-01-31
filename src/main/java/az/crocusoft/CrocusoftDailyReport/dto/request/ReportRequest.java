package az.crocusoft.CrocusoftDailyReport.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReportRequest {
//    private Long projectId;
//    private List<Long> employees;
    private LocalDate createDate;
    private Long projectId;
    private List<Long> userIds;
}
