package az.crocusoft.CrocusoftDailyReport.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static az.crocusoft.CrocusoftDailyReport.model.enums.RoleEnum.SUPERADMIN;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"};
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    private final CorsConfig corsConfig;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/v1/api/auth/**").permitAll()
                                .requestMatchers("/v1/api/user/generate-otp").permitAll()
                                .requestMatchers("/v1/api/user/verify-otp").permitAll()
                               .requestMatchers(HttpMethod.GET, "/v1/api/user/filter").hasAnyRole("SUPERADMIN","ADMIN","HEAD")
                               .requestMatchers(HttpMethod.GET, "/v1/api/user").hasAnyRole("SUPERADMIN","ADMIN","HEAD")
                               .requestMatchers(HttpMethod.GET, "/v1/api/user/{id}").hasAnyRole("SUPERADMIN","ADMIN")
                               .requestMatchers(HttpMethod.GET, "/v1/api/user/all").hasAnyRole("SUPERADMIN","ADMIN","HEAD")
                               .requestMatchers(HttpMethod.PUT, "/v1/api/user/{id}").hasAnyRole("SUPERADMIN","ADMIN")
                               .requestMatchers(HttpMethod.DELETE, "/v1/api/user/delete/{id}").hasAnyRole("SUPERADMIN","ADMIN")
                               .requestMatchers(HttpMethod.PUT, "/v1/api/user/status/{id}").hasAnyRole("SUPERADMIN","ADMIN")
                               .requestMatchers(HttpMethod.POST, "/v1/api/user/resetPassword/{id}").hasAnyRole("SUPERADMIN","ADMIN")
                               .requestMatchers(HttpMethod.POST, "/v1/api/auth/register").hasAnyRole("SUPERADMIN","ADMIN")

                                .requestMatchers(HttpMethod.GET, "/v1/api/projects/search").hasAnyRole("SUPERADMIN","ADMIN","HEAD","EMPLOYEE")
                                .requestMatchers(HttpMethod.POST, "/v1/api/projects").hasAnyRole("SUPERADMIN","ADMIN")
                               .requestMatchers(HttpMethod.GET, "/v1/api/projects/{id}").hasAnyRole("SUPERADMIN","ADMIN","HEAD")
                               .requestMatchers(HttpMethod.PUT, "/v1/api/projects/{id}").hasAnyRole("SUPERADMIN","ADMIN")

                               .requestMatchers(HttpMethod.GET, "/v1/api/reports/filter-admin").hasAnyRole("SUPERADMIN","ADMIN","HEAD")
                                .requestMatchers(HttpMethod.GET, "/v1/api/reports/filter-and-export-excel").hasAnyRole("SUPERADMIN","ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/api/reports/{id}").hasAnyRole("SUPERADMIN","ADMIN")
                                .requestMatchers(HttpMethod.POST, "/v1/api/reports").hasRole("EMPLOYEE")
                                .requestMatchers(HttpMethod.PUT, "/v1/api/reports/{id}").hasRole("EMPLOYEE")
                                .requestMatchers(HttpMethod.GET, "/v1/api/reports/{id}").hasAnyRole("SUPERADMIN","ADMIN","HEAD")
                                .requestMatchers(HttpMethod.GET,"/v1/api/reports/filter").hasRole("EMPLOYEE")
                                .requestMatchers("v1/api/team/redis/**").permitAll()

                                .requestMatchers(HttpMethod.GET, "/v1/api/roles").hasAnyRole("SUPERADMIN","ADMIN","HEAD")

                                .requestMatchers(HttpMethod.GET, "/v1/api/teams").hasAnyRole("SUPERADMIN","ADMIN","HEAD")
                                .requestMatchers(HttpMethod.GET, "/v1/api/teams/{id}").hasAnyRole("SUPERADMIN","ADMIN","HEAD")
                                .requestMatchers(HttpMethod.PUT, "/v1/api/teams/{id}").hasAnyRole("SUPERADMIN","ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/api/teams/{id}").hasAnyRole("SUPERADMIN","ADMIN")

                                .anyRequest()
                                .authenticated());

                http.authenticationProvider(authenticationProvider);
                http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}