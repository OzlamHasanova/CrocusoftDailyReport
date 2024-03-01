package az.crocusoft.CrocusoftDailyReport.dto.response;

import az.crocusoft.CrocusoftDailyReport.dto.UserDto;

import java.util.List;

public record UserFilterResponse(
        List<UserDto> userResponseList,
        int totalPages,
        long TotalElements,
        boolean hasNext
) {
}
