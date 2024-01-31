package az.crocusoft.CrocusoftDailyReport;

import az.crocusoft.CrocusoftDailyReport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrocusoftDailyReportApplication implements CommandLineRunner {
	@Autowired
	private UserService userService;


	public static void main(String[] args) {
		SpringApplication.run(CrocusoftDailyReportApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
        userService.createSuperAdminUser();
	}
}
