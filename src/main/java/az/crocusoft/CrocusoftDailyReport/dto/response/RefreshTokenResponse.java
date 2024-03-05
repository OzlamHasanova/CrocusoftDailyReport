package az.crocusoft.CrocusoftDailyReport.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {
    @JsonProperty("user_id")
    private Long id;
    @JsonProperty("access_token")
    private String accessToken;
}
