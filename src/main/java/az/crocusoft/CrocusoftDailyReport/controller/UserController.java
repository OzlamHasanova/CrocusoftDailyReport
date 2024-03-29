package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.constant.PaginationConstants;
import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponse;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponseWithData;
import az.crocusoft.CrocusoftDailyReport.dto.request.ChangePasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.ForgotPasswordRequest;
import az.crocusoft.CrocusoftDailyReport.dto.request.UserRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserFilterResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponseForGetAll;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
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
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) {
        UserDto user = userService.getById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
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
    public ResponseEntity<BaseResponseWithData> update(@PathVariable Long id, @RequestBody UserRequest userRequest) {
         UserDto userDto=userService.update(id, userRequest);
        return ResponseEntity.ok(new BaseResponseWithData("User update is successfully",userDto));
    }
    @GetMapping("/filter")
    public ResponseEntity<UserFilterResponse> filterUsers(@RequestParam(value = "firstName", required = false) String firstName,
                                                          @RequestParam(value = "lastName", required = false) String surname,
                                                          @RequestParam(value = "teamIds", required = false) List<Long> teamIds,
                                                          @RequestParam(value = "projectIds", required = false) List<Long> projectIds,
                                                          @RequestParam(name = "page", defaultValue = PaginationConstants.PAGE_NUMBER) Integer page,
                                                          @RequestParam(name = "pageSize", defaultValue = PaginationConstants.PAGE_SIZE) Integer size
                                                                   ) {
        if (size == 0) {
            size = Integer.MAX_VALUE;
        }
        return ResponseEntity.ok(userService.filterUsers(firstName, surname,teamIds, projectIds,page,size));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(new BaseResponse("User deleted successfully"));
    }
    @PutMapping("/status/{id}")
    public ResponseEntity<BaseResponse> updateUserStatus(@PathVariable Long id, @RequestParam Status status) {
        userService.updateUserStatus(id,status);
        return ResponseEntity.ok(new BaseResponse("status update is successfully"));
    }
    @PostMapping("/resetPassword/{id}")
    public ResponseEntity<BaseResponse> updateUserPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.updateUserPassword(id,newPassword);
        return ResponseEntity.ok(new BaseResponse("Password reset completed successfully"));
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
    public ResponseEntity<BaseResponse> forgotPasswordRegenerateOtp(@RequestParam String email) {
        return new ResponseEntity<>(userService.regenerateOtp(email), HttpStatus.OK);
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<BaseResponse> verifyOtp(@RequestParam String otp) {
        return new ResponseEntity<>(userService.verifyOtp(otp), HttpStatus.OK);
    }

}
