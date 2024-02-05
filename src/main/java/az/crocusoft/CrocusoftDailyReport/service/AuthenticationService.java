package az.crocusoft.CrocusoftDailyReport.service;
import az.crocusoft.CrocusoftDailyReport.config.JwtService;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponse;
import az.crocusoft.CrocusoftDailyReport.dto.request.AuthenticationRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.RegisterRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.AuthenticationResponse;
import az.crocusoft.CrocusoftDailyReport.model.Token;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.TokenType;
import az.crocusoft.CrocusoftDailyReport.repository.TokenRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<BaseResponse> register(RegisterRequest request) {
        String role = getSignedInUser().getRole().getName();
                if (!role.equals("SUPERADMIN") && !role.equals("ADMIN")) {
                    return new ResponseEntity<>(new BaseResponse("Only superadmin or admin can register users!"), HttpStatus.UNAUTHORIZED);
        }
        if(request.getRole().getId()==1){
            return new ResponseEntity<>(new BaseResponse("Nobody cannot create superadmin !"), HttpStatus.UNAUTHORIZED);

        }
        if (role.equals("ADMIN") && request.getRole().getId()==2) {
            return new ResponseEntity<>(new BaseResponse("Admin cannot create Admin!"), HttpStatus.UNAUTHORIZED);
        }

        if (repository.existsByEmail(request.getEmail())) {
            return new ResponseEntity<>(new BaseResponse("Username is taken!"), HttpStatus.BAD_REQUEST);
        }
        var user = UserEntity.builder()
                .name(request.getFirstname())
                .surname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
         repository.save(user);

        return new ResponseEntity<>(new BaseResponse("Register is succeesful"),HttpStatus.CREATED);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        Long userId = repository.findByEmail(request.getEmail()).getId();
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
               .id(userId)
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
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

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail);
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    public UserEntity getSignedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return repository
                .findByEmail(authentication.getName());
//                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}