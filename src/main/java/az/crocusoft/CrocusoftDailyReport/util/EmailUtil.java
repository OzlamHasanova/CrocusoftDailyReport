package az.crocusoft.CrocusoftDailyReport.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final JavaMailSender javaMailSender;

    public void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify OTP");
        mimeMessageHelper.setText("Your otp code:"+otp);
//        mimeMessageHelper.setText("""
//        <div>
//          <a href="http://localhost:8081/verify-account?email=%s&otp=%s" target="_blank">click link to verify</a>
//        </div>
//        """.formatted(email, otp), true);

        javaMailSender.send(mimeMessage);
    }
}