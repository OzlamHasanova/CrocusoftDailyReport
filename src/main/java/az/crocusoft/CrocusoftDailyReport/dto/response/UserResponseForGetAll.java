package az.crocusoft.CrocusoftDailyReport.dto.response;

import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseForGetAll {
    private String fullname;
    private String email;
}
