package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponse;
import az.crocusoft.CrocusoftDailyReport.dto.request.AuthenticationRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.RegisterRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.AuthenticationResponse;
import az.crocusoft.CrocusoftDailyReport.service.AuthenticationService;
import az.crocusoft.CrocusoftDailyReport.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("v1/api/auth")

@AllArgsConstructor
public class AuthController {
    @Autowired
    private UserService userService;
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
           @Valid @RequestBody RegisterRequest request
    ) {
        return service.register(request);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

}