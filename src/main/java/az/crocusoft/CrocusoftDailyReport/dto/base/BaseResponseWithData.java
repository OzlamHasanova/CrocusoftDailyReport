package az.crocusoft.CrocusoftDailyReport.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponseWithData<T> {
    String msg;
    private T data;
}
