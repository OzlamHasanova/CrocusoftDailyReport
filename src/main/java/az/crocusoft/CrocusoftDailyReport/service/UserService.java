package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponse;
import az.crocusoft.CrocusoftDailyReport.dto.request.ChangePasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.ForgotPasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.UserRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForGetAll;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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
        logger.info("Getting user by id: {}", id);

        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (userEntity != null) {
            UserDto userDto = convertToDto(userEntity);
            logger.info("User retrieved successfully");
            return userDto;
        } else {
            logger.warn("User not found with id: {}", id);
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
        logger.info("Updating user with id: {}", id);

        UserEntity user = userRepository.findById(id).orElseThrow();
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setEmail(userRequest.getEmail());
        user.setPassword(user.getPassword());
        user.setRole(roleRepository.findById(userRequest.getRoleId()).get());

        UserDto userDto = convertToDto(user);

        logger.info("User updated successfully");
        return userDto;
    }

    public void deleteProject(Long id) {
        logger.info("Deleting user with id: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setIsDeleted(true);
        userRepository.save(user);

        logger.info("User deleted successfully");
    }

    public void updateUserPassword(Long userId, String password) {
        logger.info("Updating password for user with id: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        logger.info("User password updated successfully");
    }

    public void updateUserStatus(Long userId, Status status) {
        logger.info("Updating status for user with id: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setStatus(status);
        userRepository.save(user);

        logger.info("User status updated successfully");
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
    public BaseResponse verifyAccount(ForgotPasswordRequest forgotPasswordRequest) {
        UserEntity user=authenticationService.getSignedInUser();
        if (verifyOtp(user.getOtp()).equals(new BaseResponse("verify is success"))) {
            user.setStatus(Status.ACTIVE);
            if(Objects.equals(forgotPasswordRequest.getNewPassword(), forgotPasswordRequest.getNewPasswordAgain())){
                user.setPassword(passwordEncoder.encode(forgotPasswordRequest.getNewPassword()));
                userRepository.save(user);
            }

            return new BaseResponse("OTP verified you can login");
        }
        return new BaseResponse("Please regenerate otp and try again");
    }

    public String regenerateOtp(String email) {
        UserEntity user = userRepository.findByEmail(email);
               // .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        String otp = otpUtil.generateOtp();
        System.out.println(otp);
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

    public BaseResponse verifyOtp(String otp) {
        UserEntity user=authenticationService.getSignedInUser();
        if (Objects.equals(user.getOtp(), otp) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (5 * 60)) {
            user.setStatus(Status.ACTIVE);
            return new BaseResponse("verify is success");
        }
        return new BaseResponse("Please regenerate otp and try again");
    }

    public List<UserResponseForGetAll> getAllUsers() {
        List<UserEntity> userEntityList=userRepository.findAll();
        List<UserResponseForGetAll> userResponseForGetAllList=convertToDtoList(userEntityList);
        return userResponseForGetAllList;
    }

    public List<UserResponseForGetAll> convertToDtoList(List<UserEntity> userEntityList) {
        List<UserResponseForGetAll> dtoList = new ArrayList<>();
        for (UserEntity userEntity : userEntityList) {
            UserResponseForGetAll dto = UserResponseForGetAll.builder()
                    .fullname(userEntity.getName() + " " + userEntity.getSurname())
                    .email(userEntity.getEmail())
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }


}


