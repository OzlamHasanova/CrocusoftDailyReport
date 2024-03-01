package az.crocusoft.CrocusoftDailyReport.dto.response;

import az.crocusoft.CrocusoftDailyReport.dto.UserDto;

import java.util.List;

public record ReportFilterResponseWithPaginationForUser(
        List<ReportFilterResponseForUser> reportFilterResponseForUsers,
        int totalPages,
        long TotalElements,
        boolean hasNext
) {

}
