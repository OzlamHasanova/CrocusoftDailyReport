package az.crocusoft.CrocusoftDailyReport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private Long id;
    private String accessToken;
    private String refreshToken;
//    private String tokenType = "Bearer ";


}