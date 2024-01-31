package az.crocusoft.CrocusoftDailyReport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseForFilter {
    private String name;
    private String surname;
    private String email;

}
