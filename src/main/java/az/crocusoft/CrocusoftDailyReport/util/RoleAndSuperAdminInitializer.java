package az.crocusoft.CrocusoftDailyReport.util;

import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.RoleEnum;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.repository.RoleRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RoleAndSuperAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;


    public RoleAndSuperAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    boolean isRoleDataInitialized = false;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            isRoleDataInitialized = true;
      }

        if(isRoleDataInitialized||(!userRepository.existsByRoleEnum(RoleEnum.SUPERADMIN)&&!userRepository.existsByRoleEnum(RoleEnum.HEAD))){
            Role superAdminRole = roleRepository.findByRoleEnum(RoleEnum.SUPERADMIN)
            .orElseGet(() -> {
                Role newSuperAdminRole = new Role();
                newSuperAdminRole.setRoleEnum(RoleEnum.SUPERADMIN);
                return roleRepository.save(newSuperAdminRole);
            });

    Role headRole = roleRepository.findByRoleEnum(RoleEnum.HEAD)
            .orElseGet(() -> {
                Role newHeadRole = new Role();
                newHeadRole.setRoleEnum(RoleEnum.HEAD);
                return roleRepository.save(newHeadRole);
            });

    RoleEnum[] initialRoles = {RoleEnum.SUPERADMIN, RoleEnum.HEAD};
    String[] initialEmails = {"superadmin@crocusoft.com", "head@crocusoft.com"};
    String[] initialNames = {"Superadmintest", "Headtest"};
    String[] initialSurnames = {"Supertestov", "headtestov"};
    String[] initialPassword={passwordEncoder.encode("super"),passwordEncoder.encode("head")};

    for (int i = 0; i < initialRoles.length; i++) {
        RoleEnum roleName = initialRoles[i];
        String email = initialEmails[i];
        String name = initialNames[i];
        String surname = initialSurnames[i];
        String password=initialPassword[i];

        Role role = roleRepository.findByRoleEnum(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found."));

        role = entityManager.merge(role);

        UserEntity user = UserEntity.builder()
                .email(email)
                .name(name)
                .surname(surname)
                .password(password)
                .status(Status.ACTIVE)
                .isDeleted(false)
                .role(role)
                .roleEnum(roleName)
                .build();

        userRepository.save(user);
        isRoleDataInitialized=true;
    }
}
//        if (roleRepository.count() == 0) {
//            isRoleDataInitialized = true;
//        }
//
//        if (isRoleDataInitialized) {
//            for (RoleEnum roleName : RoleEnum.values()) {
//                if (!roleRepository.existsByRoleEnum(roleName)) {
//                    Role role = new Role();
//                    role.setRoleEnum(roleName);
//                    roleRepository.save(role);
//                }
//            }
//        }
//
//        if (isRoleDataInitialized) {
//            Role role = roleRepository.findById(1)
//                    .orElseThrow(() -> new RuntimeException("Role not found."));
//            role = entityManager.merge(role);
//
//
//            UserEntity superAdmin = UserEntity.builder()
//                    .email("admin")
//                    .password(passwordEncoder.encode("superadminpassword"))
//                    .status(Status.ACTIVE)
//                    .isDeleted(false)
//                    .role(role)
//                    .roleEnum(RoleEnum.SUPERADMIN)
//                    .build();
//
//            userRepository.save(superAdmin);
//        }
    }

}