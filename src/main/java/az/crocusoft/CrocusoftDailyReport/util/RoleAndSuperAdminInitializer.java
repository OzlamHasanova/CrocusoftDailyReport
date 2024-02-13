package az.crocusoft.CrocusoftDailyReport.util;

import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.RoleEnum;
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
//    @Override
//    public void run(String... args) throws Exception {
//        for (RoleEnum roleName : RoleEnum.values()) {
//            if (!roleRepository.existsByRoleEnum(roleName)) {
//                Role role = new Role();
//                role.setRoleEnum(roleName);
//                roleRepository.save(role);
//            }
//        }
//    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {

        boolean isRoleDataInitialized = false;

        // Check if role data is already initialized
        if (roleRepository.count() == 0) {
            isRoleDataInitialized = true;
        }

        if (isRoleDataInitialized) {
            for (RoleEnum roleName : RoleEnum.values()) {
                if (!roleRepository.existsByRoleEnum(roleName)) {
                    Role role = new Role();
                    role.setRoleEnum(roleName);
                    roleRepository.save(role);
                }
            }
        }

        if (isRoleDataInitialized) {
            Role role = roleRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Role not found."));
            role = entityManager.merge(role);

            UserEntity superAdmin = UserEntity.builder()
                    .email("admin")
                    .password(passwordEncoder.encode("superadminpassword"))
                    .role(role)
                    .roleEnum(RoleEnum.SUPERADMIN)
                    .build();

            userRepository.save(superAdmin);
        }
    }

}