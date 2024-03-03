package az.crocusoft.CrocusoftDailyReport.dto;

import az.crocusoft.CrocusoftDailyReport.dto.response.DailyReportFilterAdminResponse;

import java.util.List;

public record ReportFilterResponseWithPaginationForAdmin(
        List<DailyReportFilterAdminResponse> content,
        int totalPages,
        long TotalElements,
        boolean hasNext
) {
}
