package org.example.ProjectTraninng.WebApi.Controllers.Admin.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.ProjectTraninng.Common.DTOs.LoginDTO;
import org.example.ProjectTraninng.Common.DTOs.PaginationDTO;
import org.example.ProjectTraninng.Common.DTOs.UserDTO;
import org.example.ProjectTraninng.Common.DTOs.UserUpdateDTO;
import org.example.ProjectTraninng.Common.Responses.AuthenticationResponse;
import org.example.ProjectTraninng.Common.Responses.GeneralResponse;
import org.example.ProjectTraninng.Core.Servecies.AuthenticationService;
import org.example.ProjectTraninng.WebApi.Exceptions.UserNotFoundException;
import org.example.ProjectTraninng.SessionManagement;
import org.example.ProjectTraninng.Common.Enums.Role;
import org.example.ProjectTraninng.Common.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController extends SessionManagement {

    private final AuthenticationService service;

    @PostMapping("/")
    public ResponseEntity<AuthenticationResponse> adduser(@RequestBody @Valid User request, HttpServletRequest httpServletRequest) throws UserNotFoundException, IOException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return ResponseEntity.ok(service.adduser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateuser(@RequestBody @Valid UserUpdateDTO request, @PathVariable Long id, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAllUser(user);
        return ResponseEntity.ok(service.UpdateUser(request,id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteuser(@PathVariable Long id, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
          validateLoggedInAdmin(user);
        return ResponseEntity.ok(service.DeleteUser(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getuser(@PathVariable Long id, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return ResponseEntity.ok(service.GetUser(id));
    }

    @GetMapping("")
    public PaginationDTO<User> getallusers(@RequestParam(defaultValue = "1",required = false) int page,
                                           @RequestParam(defaultValue = "10",required = false) int size,
                                           @RequestParam(defaultValue = "",required = false) String search,
                                           @RequestParam(defaultValue = "",required = false) Role role, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
          validateLoggedInAdmin(user);
        return service.GetAllUsers(page, size,search,role);
    }

    @GetMapping("/deleted")
    public PaginationDTO<User> getalldeleted(@RequestParam(defaultValue = "1",required = false) int page,
                                           @RequestParam(defaultValue = "10",required = false) int size,
                                           @RequestParam(defaultValue = "",required = false) String search,
                                           @RequestParam(defaultValue = "",required = false) Role role, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return service.getAllDeletedUsers(page, size,search,role);
    }

    @GetMapping("byRole/{role}")
    public Page<User> getAllUsersByRole(@PathVariable Role role,@RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
         validateLoggedInAdmin(user);
        return service.getAllUsersByRole(role, page, size);
    }
    // make a query to restore the user when the user is deleted equal to true
    @PostMapping ("/restore/{id}")
    public ResponseEntity<GeneralResponse> restoreUser(@PathVariable Long id, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return ResponseEntity.ok(service.restoreUser(id));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<AuthenticationResponse> changePassword(@RequestParam String email,
                                                                 @RequestParam String oldPassword,
                                                                 @RequestParam String newPassword,
                                                                 HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAllUser(user);
        AuthenticationResponse response = service.ChangePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctors")
    public PaginationDTO<User> getAllDoctors(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return service.findAllDoctors(page, size);
    }

    @GetMapping("/secretaries")
    public PaginationDTO<User> getAllSecretaries(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size,HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return service.findAllSecretaries(page, size);
    }

    @GetMapping("/doctorsUpdated")
    public PaginationDTO<User> getAllDoctorsUpdated(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return service.findAllDoctorsUpdated(page, size);
    }

    @GetMapping("/secretariesUpdated")
    public PaginationDTO<User> getAllSecretariesUpdated(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size,HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return service.findAllSecretariesUpdated(page, size);
    }

}
