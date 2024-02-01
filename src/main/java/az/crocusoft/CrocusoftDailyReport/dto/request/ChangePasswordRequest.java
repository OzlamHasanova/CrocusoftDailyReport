package az.crocusoft.CrocusoftDailyReport.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String newPasswordAgain;
}
