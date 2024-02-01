package az.crocusoft.CrocusoftDailyReport.config;

import java.util.Date;

import java.security.Key;
import java.util.Optional;

import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.repository.RoleRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JWTGenerator {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private static final Key accessKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final Key refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

//    public String generateAccessToken(Authentication authentication) {
//        User user = (User) authentication.getPrincipal(); // User sınıfı olarak alınır
//        String email = user.getUsername(); // Kullanıcının e-posta adresi alınır
//
//        Date currentDate = new Date();
//        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
//
//        String token = Jwts.builder()
//                .setSubject(email)
//                .setIssuedAt(currentDate)
//                .setExpiration(expireDate)
//                .signWith(accessKey, SignatureAlgorithm.HS512)
//                .compact();
//
//        return token;
//    }
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = authentication.getName();
        System.out.println(email);

        UserEntity user = userRepository.findByEmail(email);
        String roleName = user.getRole().getName();

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", roleName)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(accessKey, SignatureAlgorithm.HS512)
                .compact();

        return token;
    }
    public String getRoleFromToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        UserEntity user = userRepository.findByEmail(email);
        String roleName = user.getRole().getName();

        return roleName;
    }

    public String generateRefreshToken(Authentication authentication) {
        String email = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.REFRESH_TOKEN_EXPIRATION);

        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .compact();

        return token;
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


    public boolean validateAccessToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();
            String email = claims.getSubject();
            String roleName = claims.get("role", String.class);

            UserEntity user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }

            String storedRoleName = user.getRole().getName();
            if (!roleName.equals(storedRoleName)) {
                throw new AccessDeniedException("Invalid role");
            }

            return true;
        } catch (ExpiredJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Access token has expired", ex);
        } catch (JwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Invalid access token", ex);
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("Refresh token was expired or incorrect", ex.fillInStackTrace());
        }
    }
}