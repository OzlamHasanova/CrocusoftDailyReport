//package az.crocusoft.CrocusoftDailyReport.controller;
//
//import az.crocusoft.CrocusoftDailyReport.dto.response.RoleResponse;
//import az.crocusoft.CrocusoftDailyReport.service.RoleService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("v1/api/roles")
//public class RoleController {
//    private final RoleService roleService;
//    @GetMapping
//    public ResponseEntity<List<RoleResponse>> getAllResponse(){
//        return ResponseEntity.ok(roleService.getAllRoles());
//    }
//}
