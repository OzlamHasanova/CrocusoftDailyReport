package az.crocusoft.CrocusoftDailyReport.dto;

import az.crocusoft.CrocusoftDailyReport.dto.response.DailyReportFilterAdminResponse;

import java.util.List;

public record ReportFilterResponseWithPaginationForAdmin(
        List<DailyReportFilterAdminResponse> userResponseList,
        int totalPages,
        long TotalElements,
        boolean hasNext
) {
}
