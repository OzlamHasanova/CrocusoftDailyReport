package az.crocusoft.CrocusoftDailyReport.repository;

import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleEnum(RoleEnum roleEnum);

    boolean existsByRoleEnum(RoleEnum roleName);

}
