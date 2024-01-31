package az.crocusoft.CrocusoftDailyReport.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectDto {
    private String name;
    private List<Long> employeeIds;

}
