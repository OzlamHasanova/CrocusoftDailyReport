//package az.crocusoft.CrocusoftDailyReport.util;
//
//import az.crocusoft.CrocusoftDailyReport.model.Role;
//import az.crocusoft.CrocusoftDailyReport.repository.RoleRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//
//@Component
//public class RoleInitializer implements CommandLineRunner {
//    private RoleRepository roleRepository;
//    @Transactional
//    @Override
//    public void run(String... args) throws Exception {
//        Role role1 = new Role("Admin");
//        Role role2 = new Role("User");
//        Role role3 = new Role("Manager");
//        Role role4 = new Role("Guest");
//
//        roleRepository.saveAll(Arrays.asList(role1, role2, role3, role4));
//    }
//}
