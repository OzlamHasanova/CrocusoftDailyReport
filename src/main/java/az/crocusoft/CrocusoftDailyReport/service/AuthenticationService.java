package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.config.JwtService;
import az.crocusoft.CrocusoftDailyReport.dto.TeamDto;
import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponse;
import az.crocusoft.CrocusoftDailyReport.dto.request.AuthenticationRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.RegisterRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.AuthenticationResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectDtoForGetApi;
import az.crocusoft.CrocusoftDailyReport.dto.response.RefreshTokenResponse;
import az.crocusoft.CrocusoftDailyReport.exception.EmailAlreadyExistException;
import az.crocusoft.CrocusoftDailyReport.exception.UserNotFoundException;
import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import az.crocusoft.CrocusoftDailyReport.model.Token;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.model.enums.TokenType;
import az.crocusoft.CrocusoftDailyReport.repository.TeamRepository;
import az.crocusoft.CrocusoftDailyReport.repository.TokenRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TeamRepository  teamRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public ResponseEntity<BaseResponse> register(RegisterRequest request) {
        String role = getSignedInUser().getRole().getRoleEnum().name();
        if(!isValidEmailDomain(request.getEmail())){
            throw new EmailAlreadyExistException("Email must be from crocusoft domain");
        }
        if (!role.equals("SUPERADMIN") && !role.equals("ADMIN")) {
            logger.warn("Unauthorized registration attempt by user: {}", getSignedInUser().getEmail());
            return new ResponseEntity<>(new BaseResponse("Only superadmin or admin can register users!"), HttpStatus.UNAUTHORIZED);
        }
        if (request.getRole().getId() == 1) {
            logger.warn("Unauthorized attempt to create superadmin by user: {}", getSignedInUser().getEmail());
            return new ResponseEntity<>(new BaseResponse("Nobody cannot create superadmin!"), HttpStatus.UNAUTHORIZED);
        }
        if (role.equals("ADMIN") && request.getRole().getId() == 2) {
            logger.warn("Unauthorized attempt to create admin by user: {}", getSignedInUser().getEmail());
            return new ResponseEntity<>(new BaseResponse("Admin cannot create Admin!"), HttpStatus.UNAUTHORIZED);
        }

        if (repository.existsByEmail(request.getEmail())) {
            logger.warn("Username is already taken: {}", request.getEmail());
            return new ResponseEntity<>(new BaseResponse("Username is already taken!"), HttpStatus.BAD_REQUEST);
        }
        var user = UserEntity.builder()
                .name(request.getFirstname())
                .surname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .team(teamRepository.findById(request.getTeamId()).orElse(null))
                .roleEnum(request.getRole().getRoleEnum())
                .status(Status.ACTIVE)
                .isDeleted(Boolean.FALSE)
                .role(request.getRole())
                .build();
        repository.save(user);

        logger.info("User registered successfully: {}", user.getEmail());
        return new ResponseEntity<>(new BaseResponse("Register is successful"), HttpStatus.CREATED);
    }
    private boolean isValidEmailDomain(String email) {
        return email.endsWith("@crocusoft.com");
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = repository.findByEmailAndIsDeletedAndStatus(request.getEmail(),false,Status.ACTIVE);

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            Long userId = repository.findByEmailAndIsDeletedAndStatus(request.getEmail(),false,Status.ACTIVE).getId();
            revokeAllUserTokens(user);
            saveUserToken(user, refreshToken);
            return AuthenticationResponse.builder()
                    .id(userId)
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .expiredDate(jwtService.extractExpiration(jwtToken))
                    .build();
        }catch (Exception exception){
            throw new UserNotFoundException("User or password is wrong"+exception.getMessage());
        }

    }

    private void saveUserToken(UserEntity user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


public RefreshTokenResponse refreshToken(String refreshToken) {
    String mail = jwtService.extractUsername(refreshToken);
    if (mail != null) {
        var user = repository.findByEmail(mail).get();
        if (jwtService.isTokenValid(refreshToken, user)) {
            var accessToken = jwtService.generateToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, accessToken);
            return RefreshTokenResponse.builder()
                    .id(user.getId())
                    .accessToken(accessToken)
                    .build();
        }
    }
    return null;
}
    public UserDto getProfile() {
        Long userId=getSignedInUser().getId();
        logger.info("Getting user by id: {}", userId);

        Optional<UserEntity> user = repository.findById(userId);
        UserEntity userEntity = user.orElse(null);
        if (userEntity != null) {
            UserDto userDto = convertToDto(userEntity);
            logger.info("User retrieved successfully");
            return userDto;
        } else {
            logger.warn("User not found with id: {}", userId);
            return null;
        }
    }
    public UserDto convertToDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setName(userEntity.getName());
        userDto.setSurname(userEntity.getSurname());
        userDto.setEmail(userEntity.getEmail());
        userDto.setRole(userEntity.getRole());
        userDto.setTeam(convertToTeamDto(userEntity.getTeam()));
        userDto.setProject(convertToProjectDtoList(userEntity.getProjects()));
        userDto.setStatus(userEntity.getStatus().toString());
        return userDto;
    }
    public List<UserDto> convertToDtoList(Page<UserEntity> userEntitys) {
        List<UserDto> userDtos=new ArrayList<>();
        for(UserEntity user:userEntitys){
            UserDto userDto=new UserDto();
            userDto.setId(user.getId());
            userDto.setName(user.getName());
            userDto.setSurname(user.getSurname());
            userDto.setEmail(user.getEmail());
            userDto.setRole(user.getRole());
            userDto.setTeam(convertToTeamDto(user.getTeam()));
            userDto.setProject(convertToProjectDtoList(user.getProjects()));
            userDto.setStatus(user.getStatus().toString());
            userDtos.add(userDto);
        }

        return userDtos;
    }
    private TeamDto convertToTeamDto(Team team) {
        if (team == null) {
            return null;
        }
        TeamDto teamDto = TeamDto.builder()
                .Id(team.getId())
                .name(team.getName()).build();

        return teamDto;
    }

    private List<ProjectDtoForGetApi> convertToProjectDtoList(List<Project> projects) {
        if (projects == null) {
            return null;
        }
        List<ProjectDtoForGetApi> projectDtoList = new ArrayList<>();
        for (Project project : projects) {
            ProjectDtoForGetApi projectDto = new ProjectDtoForGetApi();
            projectDto.setId(project.getId());
            projectDto.setName(project.getName());
            projectDtoList.add(projectDto);
        }
        return projectDtoList;
    }

    public UserEntity getSignedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = repository.findByEmailAndIsDeletedAndStatus(authentication.getName(),false,Status.ACTIVE);
        if (user==null){
            throw new UserNotFoundException("User not found");
        }
        logger.info("Signed-in user: {}", user.getEmail());
        return user;
    }

}