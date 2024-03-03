package az.crocusoft.CrocusoftDailyReport.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
    private String newPassword;
    private String newPasswordAgain;
}
