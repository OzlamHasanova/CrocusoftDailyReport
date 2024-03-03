package az.crocusoft.CrocusoftDailyReport.dto.response;

import java.util.List;

public record ProjectResponseForSearch (
        List<ProjectResponseForFilter> content,
        int totalPages,
        long totalElements,
        boolean hasNext
){

}
