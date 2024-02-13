package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.dto.response.RoleResponse;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<RoleResponse> getAllRoles(){
        List<Role> role=roleRepository.findAll();
        List<RoleResponse> roleResponseList=mapRoleListToRoleResponseList(role);
        return roleResponseList;
    }

    private List<RoleResponse> mapRoleListToRoleResponseList(List<Role> roles) {
        return roles.stream()
                .map(role -> {
                    RoleResponse roleResponse = new RoleResponse();
                    roleResponse.setId(role.getId());
                    roleResponse.setName(role.getRoleEnum().name());
                    return roleResponse;
                })
                .collect(Collectors.toList());
    }

}
