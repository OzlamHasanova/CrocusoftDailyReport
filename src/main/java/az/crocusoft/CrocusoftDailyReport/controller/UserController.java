package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponse;
import az.crocusoft.CrocusoftDailyReport.dto.request.ChangePasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.ForgotPasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.UserRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForGetAll;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    public ResponseEntity<UserDto> getById(@RequestParam Long id) {
        UserDto user = userService.getById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseForGetAll>> getAllUsers() {
        List<UserResponseForGetAll> user = userService.getAllUsers();
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        UserDto updateUser = userService.update(id, userRequest);
        return ResponseEntity.ok(updateUser);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<UserResponseForFilter>> filterUsers(@RequestParam(value = "firstName", required = false) String firstName,
                                                                   @RequestParam(value = "lastName", required = false) String surname,
                                                                   @RequestParam(value = "teamIds", required = false) List<Long> teamIds,
                                                                   @RequestParam(value = "projectIds", required = false) List<Long> projectIds
                                                                   ) {
        List<UserResponseForFilter> filteredUsers = userService.filterUsers(firstName, surname,teamIds, projectIds);
        return ResponseEntity.ok(filteredUsers);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        userService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/status/{id}")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestParam Status status) {
        userService.updateUserStatus(id,status);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/resetPassword/{id}")
    public ResponseEntity<Void> updateUserPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.updateUserPassword(id,newPassword);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword( @RequestBody ChangePasswordRequest changePassword)  {
        userService.changeUserPassword(changePassword);
        return ResponseEntity.ok("success");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse> forgotPasswordWithOtp(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return new ResponseEntity<>(userService.verifyAccount(forgotPasswordRequest), HttpStatus.OK);
    }
    @PostMapping("/generate-otp")
    public ResponseEntity<String> forgotPasswordRegenerateOtp(@RequestParam String email) {
        return new ResponseEntity<>(userService.regenerateOtp(email), HttpStatus.OK);
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<BaseResponse> verifyOtp(@RequestParam String otp) {
        return new ResponseEntity<>(userService.verifyOtp(otp), HttpStatus.OK);
    }

}
