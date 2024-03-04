package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.constant.PaginationConstants;
import az.crocusoft.CrocusoftDailyReport.dto.TeamDto;
import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponse;
import az.crocusoft.CrocusoftDailyReport.dto.request.ChangePasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.ForgotPasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.UserRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectDtoForGetApi;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserFilterResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForGetAll;
import az.crocusoft.CrocusoftDailyReport.exception.PasswordChangeIsFalse;
import az.crocusoft.CrocusoftDailyReport.exception.UnsupportedOperationException;
import az.crocusoft.CrocusoftDailyReport.exception.UserNotFoundException;
import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.RoleEnum;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.repository.*;
import az.crocusoft.CrocusoftDailyReport.util.EmailUtil;
import az.crocusoft.CrocusoftDailyReport.util.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
    private final EmailUtil emailUtil;
    private final OtpUtil otpUtil;
    private final AuthenticationService authenticationService;


    public UserDto update(Long id, UserRequest userRequest) {
        logger.info("Updating user with id: {}", id);

        UserEntity user = userRepository.findById(id).orElseThrow();
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setEmail(userRequest.getEmail());
        user.setRole(roleRepository.findById(userRequest.getRoleId()).orElseThrow());

        UserEntity updatedUser = userRepository.save(user);
        UserDto userDto = authenticationService.convertToDto(updatedUser);

        logger.info("User updated successfully");
        return userDto;
    }

    public void delete(Long id) {
        logger.info("Deleting user with id: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        if(user.getRoleEnum().name().equals("SUPERADMIN")||user.getRoleEnum().name().equals("HEAD")){
            throw new UnsupportedOperationException("You cannot delete this user");
        }
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
        UserEntity user=userRepository.findByEmailAndIsDeletedAndStatus(forgotPasswordRequest.getEmail(),false,Status.ACTIVE);
        if(user.getOtp()==null){
            throw new PasswordChangeIsFalse("Password change is false");
        }
        if (verifyOtp(user.getOtp()).equals(new BaseResponse("verify is success"))) {
            if(Objects.equals(forgotPasswordRequest.getNewPassword(), forgotPasswordRequest.getNewPasswordAgain())){
                user.setPassword(passwordEncoder.encode(forgotPasswordRequest.getNewPassword()));
                user.setOtp(null);
                userRepository.save(user);
            }
            return new BaseResponse("Password change is success");
        }
        return new BaseResponse("Please regenerate otp and try again");
    }

    public BaseResponse regenerateOtp(String email) {
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
        return new BaseResponse("Email sent... please verify account within 5 minute");
    }

    public UserFilterResponse filterUsers(String name, String surname, List<Long> teamIds, List<Long> projectIds, int page, int pageSize) {
        UserEntity user=authenticationService.getSignedInUser();
        page = Math.max(page, Integer.parseInt(PaginationConstants.PAGE_NUMBER));
        pageSize = pageSize < 1 ? Integer.parseInt(PaginationConstants.PAGE_SIZE) : pageSize;
        pageSize = Math.min(pageSize, 550);

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        if(user.getRoleEnum().equals(RoleEnum.SUPERADMIN) || user.getRoleEnum().equals(RoleEnum.HEAD)) {
        Page<UserEntity> filteredUsers=userRepository.filterUsers(name,surname,teamIds,projectIds,pageable);
            return new UserFilterResponse(authenticationService.convertToDtoList(filteredUsers), filteredUsers.getTotalPages(), filteredUsers.getTotalElements(),filteredUsers.hasNext());
        }
        Page<UserEntity> filteredUserForAdmin =userRepository.filterAdmin(name,surname,teamIds,projectIds, user.getId(), pageable);
        return new UserFilterResponse(authenticationService.convertToDtoList(filteredUserForAdmin), filteredUserForAdmin.getTotalPages(), filteredUserForAdmin.getTotalElements(),filteredUserForAdmin.hasNext());


    }


    public BaseResponse verifyOtp(String otp) {
        UserEntity user=userRepository.findByOtp(otp);
        if (Objects.equals(user.getOtp(), otp) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (5 * 60)) {
            user.setStatus(Status.ACTIVE);
            return new BaseResponse("verify is success");
        }
        return new BaseResponse("Please regenerate otp and try again");
    }

    public List<UserResponseForGetAll> getAllUsers() {
        List<UserEntity> userEntityList= userRepository.findAllByRoleEnumAndIsDeletedAndStatus(RoleEnum.EMPLOYEE,false,Status.ACTIVE);
        return convertToDtoList(userEntityList);
    }
    public UserDto getById(Long userId) {

        logger.info("Getting user by id: {}", userId);

        Optional<UserEntity> user = userRepository.findById(userId);
        UserEntity userEntity = user.orElse(null);
        if (userEntity != null) {
            UserDto userDto = authenticationService.convertToDto(userEntity);
            logger.info("User retrieved successfully");
            return userDto;
        } else {
            logger.warn("User not found with id: {}", userId);
            return null;
        }
    }

    public List<UserResponseForGetAll> convertToDtoList(List<UserEntity> userEntityList) {
        List<UserResponseForGetAll> dtoList = new ArrayList<>();
        for (UserEntity userEntity : userEntityList) {
            UserResponseForGetAll.UserResponseForGetAllBuilder dtoBuilder = UserResponseForGetAll.builder()
                    .userId(userEntity.getId())
                    .name(userEntity.getName())
                    .surname(userEntity.getSurname())

                    .email(userEntity.getEmail())
                    .role(userEntity.getRole().getRoleEnum().name())
                    .status(userEntity.getStatus().name());

            if (userEntity.getTeam() != null) {
                dtoBuilder.teamName(userEntity.getTeam().getName());
            } else {
                dtoBuilder.teamName(null);
            }

            UserResponseForGetAll dto = dtoBuilder.build();
            dtoList.add(dto);
        }
        return dtoList;
    }

}


