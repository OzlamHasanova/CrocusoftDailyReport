package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.ProjectDto;
import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.request.ChangePasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.ForgotPasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.UserRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.exception.PasswordChangeIsFalse;
import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/user")
@RequiredArgsConstructor
public class EmployeeController {
    private final UserService userService;
    @GetMapping("/get")
    public ResponseEntity<UserDto> getById(@RequestParam Long id) {
        UserDto user = userService.getById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        UserDto updateUser = userService.update(id, userRequest);
        return ResponseEntity.ok(updateUser);
    }
//    @GetMapping("/filter")
//    public ResponseEntity<List<UserResponseForFilter>> filterUsers(@RequestParam(value = "firstName", required = false) String firstName,
//                                                                   @RequestParam(value = "lastName", required = false) String surname,
//            @RequestParam(value = "teamIds", required = false) List<Long> teamIds,
//                                                                   @RequestParam(value = "projectIds", required = false) List<Long> projectIds
//                                                                   ) {
//        List<UserResponseForFilter> filteredUsers = userService.filterUsers(firstName, surname,teamIds, projectIds);
//        return ResponseEntity.ok(filteredUsers);
//    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        userService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/update/status/{userId}")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long userId, @RequestParam Status status) {
        userService.updateUserStatus(userId,status);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/update/resetPassword/{userId}")
    public ResponseEntity<Void> updateUserPassword(@PathVariable Long userId, @RequestParam String newPassword) {
        userService.updateUserPassword(userId,newPassword);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/changePassword/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest changePassword)  {
        userService.changeUserPassword(userId,changePassword);
        return ResponseEntity.ok("success");
    }
    @PutMapping("/forgot-password")
    public ResponseEntity<String> forgotPasswordWithOtp(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return new ResponseEntity<>(userService.verifyAccount(forgotPasswordRequest), HttpStatus.OK);
    }
    @PutMapping("/generate-otp")
    public ResponseEntity<String> forgotPasswordRegenerateOtp(@RequestParam String email) {
        return new ResponseEntity<>(userService.regenerateOtp(email), HttpStatus.OK);
    }

}
