package tn.dymes.store.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import tn.dymes.store.entites.User;
import tn.dymes.store.services.IAuthorizationService;
import tn.dymes.store.services.IUserService;
import tn.dymes.store.dtos.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
@CrossOrigin("*")
public class EmployeeController {

    @Autowired
    IUserService userService;

    @Autowired
    IAuthorizationService authorizationService;

    @Autowired
    JwtDecoder jwtDecoder;

    @PostMapping(path = "/changeStatus")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, String>> changeStatus(@RequestParam("userid") String userId, @RequestParam("status") boolean status) {
        try {
            this.userService.changeSuspensionStatus(userId,status);
            return new ResponseEntity<>(Map.of("message","Employee status updated successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/delete/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, String>> DeleteEmployee(@PathVariable String userId) {
        try {
              this.userService.deleteUser(userId);
              return new ResponseEntity<>(Map.of("message","Employee deleted successfully"), HttpStatus.OK);
            }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getAll")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> getAllEmployees() {
        try {
            List<User> employees = this.userService.findAllEmployee();
            System.out.println("Number of employees found: " + employees.size());
            if (employees.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(employees, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, String>> updateEmployee(@RequestBody AddEmployeeDTO employeeDTO) {
        try {
            if (this.userService.findUserByEmail(employeeDTO.email()) != null){
                this.userService.updateEmployee(employeeDTO);
                return new ResponseEntity<>(Map.of("message","Employee updated success."), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(Map.of("message","Employee not found."), HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, String>> addEmployee(@RequestBody AddEmployeeDTO employeeDTO) {
        try {
            if (this.userService.findUserByEmail(employeeDTO.email()) != null){
                return new ResponseEntity<>(Map.of("message","Employee already exist"), HttpStatus.OK);
            }
            this.userService.addEmployee(employeeDTO);
            return new ResponseEntity<>(Map.of("message","Employee added success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/loadModules")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, String>> loadModules(@RequestBody List<ModuleDTO> moduleDTOList) {
        try {
            this.authorizationService.loadModules(moduleDTOList);
            return new ResponseEntity<>(Map.of("message","Module Loaded Success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }
}
