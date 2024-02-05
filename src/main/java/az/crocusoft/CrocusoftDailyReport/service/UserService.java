package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.ChangePasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.ForgotPasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.UserRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.exception.UserNotFoundException;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.repository.RoleRepository;
import az.crocusoft.CrocusoftDailyReport.repository.TeamRepository;
import az.crocusoft.CrocusoftDailyReport.repository.TokenRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import az.crocusoft.CrocusoftDailyReport.util.EmailUtil;
import az.crocusoft.CrocusoftDailyReport.util.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final TeamRepository teamRepository;
    private final TokenRepository tokenRepository;
    private final EmailUtil emailUtil;
    private final OtpUtil otpUtil;
    private final AuthenticationService authenticationService;


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
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setIsDeleted(true);
        userRepository.save(user);
    }


    public void updateUserPassword(Long userId, String password) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    public void updateUserStatus(Long userId, Status status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setStatus(status);
        userRepository.save(user);
    }
    public void changeUserPassword( ChangePasswordRequest changePassword) {
        Long userId=authenticationService.getSignedInUser().getId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        if((passwordEncoder.matches(changePassword.getOldPassword(),user.getPassword())&& Objects.equals(changePassword.getNewPassword(), changePassword.getNewPasswordAgain()))){
            user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));

            userRepository.save(user);
        }

    }
    public String verifyAccount(ForgotPasswordRequest forgotPasswordRequest) {
        UserEntity user = userRepository.findByEmail(forgotPasswordRequest.getEmail());
               // .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        if (user.getOtp().equals(forgotPasswordRequest.getOtp()) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (5 * 60)) {
            user.setStatus(Status.ACTIVE);
            if(Objects.equals(forgotPasswordRequest.getNewPassword(), forgotPasswordRequest.getNewPasswordAgain())){
                user.setPassword(passwordEncoder.encode(forgotPasswordRequest.getNewPassword()));
                userRepository.save(user);
            }

            return "OTP verified you can login";
        }
        return "Please regenerate otp and try again";
    }

    public String regenerateOtp(String email) {
        UserEntity user = userRepository.findByEmail(email);
               // .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... please verify account within 5 minute";
    }

    public List<UserResponseForFilter> filterUsers(String name, String surname,List<Long> teamIds, List<Long> projectIds ) {
        List<UserEntity> filteredUsers = userRepository.filterUsers(name, surname,teamIds, projectIds );
        return mapToUserResponseDTOs(filteredUsers);
    }

    private List<UserResponseForFilter> mapToUserResponseDTOs(List<UserEntity> users) {
        return users.stream()
                .map(user -> new UserResponseForFilter(user.getName(), user.getSurname(), user.getEmail()))
                .collect(Collectors.toList());
    }
//    private void saveUserToken(UserEntity user, String jwtToken) {
//        var token = Token.builder()
//                .user(user)
//                .token(jwtToken)
////                .tokenType(TokenType.BEARER)
//                .expired(false)
//                .revoked(false)
//                .build();
//        tokenRepository.save(token);
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

