package az.crocusoft.CrocusoftDailyReport.dto.request;

import lombok.Data;

import javax.print.DocFlavor;

@Data
public class ReportRequestForCreate {
//    private Long id;
    private String description;
    private Long projectId;
}
