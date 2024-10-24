package tdelivry.mr_irmag.user_service.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tdelivry.mr_irmag.user_service.domain.dto.SignInRequest;
import tdelivry.mr_irmag.user_service.domain.dto.UserDTO;
import tdelivry.mr_irmag.user_service.domain.dto.UserExistenceResponse;
import tdelivry.mr_irmag.user_service.domain.entity.User;
import tdelivry.mr_irmag.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@Validated
@Log4j2
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        User createdUser = userService.createUser(userDTO);
        UserDTO createdUserDTO = userService.mapToDTO(createdUser);
        log.info("Created user {}", createdUserDTO.toString());
        return ResponseEntity.ok(createdUserDTO);
    }

    @PostMapping("/check-username")
    public ResponseEntity<UserDTO> getUserByUsername(@Valid @RequestBody SignInRequest request) {
        User user = userService.getUserByName(request.getUsername());

        if (user == null || !userService.checkPassword(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(userService.mapToDTO(user));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<User> getCustomerInfo(@PathVariable UUID userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userName}")
    public ResponseEntity<User> getCustomerByName(@PathVariable String userName) {
        User user = userService.getUserByName(userName);
        log.info("Found user with name {}", user.toString());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/check")
    public ResponseEntity<UserExistenceResponse> existUserByNameOrEmail(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.existUserByNameOrEmail(userDTO);

        if (user != null) {
            String message;
            if (user.getEmail().equalsIgnoreCase(userDTO.getEmail())) {
                message = "User exists by email: " + userDTO.getEmail();
            } else {
                message = "User exists by username: " + userDTO.getUsername();
            }
            UserExistenceResponse response = new UserExistenceResponse(true, message, LocalDateTime.now());
            return ResponseEntity.ok(response);
        } else {
            UserExistenceResponse response = new UserExistenceResponse(false, "User not found", LocalDateTime.now());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getCustomerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllCustomers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateCustomer(@PathVariable UUID userId, @Valid @RequestBody User updatedCustomer) {
        return ResponseEntity.ok(userService.updateUser(userId, updatedCustomer));
    }

    @PutMapping("/{userId}/address")
    public ResponseEntity<User> updateCustomerAddress(@PathVariable UUID userId, @RequestParam String newAddress) {
        return ResponseEntity.ok(userService.updateAddressOfUser(userId, newAddress));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
