package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.config.JWTGenerator;
import az.crocusoft.CrocusoftDailyReport.dto.AuthResponseDTO;
import az.crocusoft.CrocusoftDailyReport.dto.LoginDto;
import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.UserRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.repository.RoleRepository;
import az.crocusoft.CrocusoftDailyReport.repository.TeamRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;
    private final TeamRepository teamRepository;

    @Transactional
    public void createSuperAdminUser() {
//        String superAdminRoleName = "SUPERADMIN";

        // Check if the superadmin user already exists
        if (!userRepository.existsByEmail("admin")) {
            UserEntity superAdmin = new UserEntity();
            superAdmin.setEmail("admin");
            superAdmin.setPassword(passwordEncoder.encode("adminpassword"));
            superAdmin.setRole(roleRepository.findById(1).get());
            superAdmin.setStatus(Status.ACTIVE);

            // Check if the superadmin role already exists
            Role superAdminRole = roleRepository.findByName("SUPERADMIN").get();
            if (superAdminRole == null) {
                superAdminRole = new Role(superAdminRole);
                roleRepository.save(superAdminRole);
            }

            superAdmin.setRole(superAdminRole);
            userRepository.save(superAdmin);
        }
    }
    public ResponseEntity<String> registerUser(UserRequest userRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = jwtGenerator.generateAccessToken(authentication);
        System.out.println(accessToken);
        String role = jwtGenerator.getRoleFromToken(authentication);

        if (!role.equals("SUPERADMIN") && !role.equals("ADMIN")) {
            return new ResponseEntity<>("Only superadmin or admin can register users!", HttpStatus.UNAUTHORIZED);
        }
        if(userRequest.getRoleId()==1){
            return new ResponseEntity<>("Nobody cannot create superadmin !", HttpStatus.UNAUTHORIZED);

        }
        if (role.equals("ADMIN") && userRequest.getRoleId()==2) {
            return new ResponseEntity<>("Admin cannot create Admin!", HttpStatus.UNAUTHORIZED);
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }


        UserEntity user = new UserEntity();
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(roleRepository.findById(userRequest.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found")));
        user.setTeam(teamRepository.findById(userRequest.getTeamId()).orElseThrow(() -> new RuntimeException("Team not found")));
        user.setStatus(Status.ACTIVE);

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }
    public ResponseEntity<AuthResponseDTO> loginUser(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtGenerator.generateAccessToken(authentication);
        String refreshToken = jwtGenerator.generateRefreshToken(authentication);
        Long userId = userRepository.findByEmail(loginDto.getEmail()).getId();

        AuthResponseDTO responseDTO = new AuthResponseDTO(userId,accessToken, refreshToken);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    public UserDto getById(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (userEntity != null) {
            return convertToDto(userEntity);
        } else {
            return null;
        }
    }

    private UserDto convertToDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setPassword(userEntity.getPassword());
        userDto.setRole(roleRepository.findById(userEntity.getRole().getId()).get());
        userDto.setTeam(teamRepository.findById(userEntity.getTeam().getId()).get());
        userDto.setStatus(userEntity.getStatus().toString());
        return userDto;
    }

    public UserDto update(Long id, UserRequest userRequest) {
        UserEntity user=userRepository.findById(id).orElseThrow();
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setEmail(userRequest.getEmail());
        user.setPassword(user.getPassword());
        user.setRole(roleRepository.findById(userRequest.getRoleId()).get());
        return convertToDto(user);

    }

    public void deleteProject(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setIsDeleted(true);
        userRepository.save(user);
    }

//    public List<UserResponseForFilter> filterUsers(String name, String surname,List<Long> teamIds, List<Long> projectIds ) {
//        List<UserEntity> filteredUsers = userRepository.filterUsers(name, surname,teamIds, projectIds );
//        return mapToUserResponseDTOs(filteredUsers);
//    }

    private List<UserResponseForFilter> mapToUserResponseDTOs(List<UserEntity> users) {
        return users.stream()
                .map(user -> new UserResponseForFilter(user.getName(), user.getSurname(), user.getEmail()))
                .collect(Collectors.toList());
    }


    //todo: silinmeli olsa da ehtimala qalib
//    public List<UserEntity> filterUsers(
//            String name,
//            String lastname,
//            String username,
//            String email,
//            String teamName,
//
//            Integer roleId,
//            Status status
//    ) {
//        return userRepository.filterUsers(name, lastname, username, email, teamName,  roleId, status);
//    }

}