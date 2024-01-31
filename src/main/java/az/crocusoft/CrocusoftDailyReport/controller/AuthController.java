package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.AuthResponseDTO;
import az.crocusoft.CrocusoftDailyReport.dto.LoginDto;
import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.UserRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.model.Role;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/auth")

@AllArgsConstructor
public class AuthController {
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto) {
        return userService.loginUser(loginDto);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<String> register(@RequestBody UserRequest userRequest) {
        return userService.registerUser(userRequest);
    }

//    @GetMapping("/filter")
//    public List<UserResponseForFilter> filterUsers(
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String surname,
//            @RequestParam(required = false) List<Long> teamIds,
//            @RequestParam(required = false) List<Long> projectIds
//    ) {
//        return userService.filterUsers(name, surname,  teamIds, projectIds);
//    }

}