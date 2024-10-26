package tdelivry.mr_irmag.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tdelivry.mr_irmag.user_service.domain.dto.*;
import tdelivry.mr_irmag.user_service.domain.entity.User;
import tdelivry.mr_irmag.user_service.service.OrderServiceClient;
import tdelivry.mr_irmag.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@Validated
@Log4j2
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OrderServiceClient orderServiceClient;

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


    @GetMapping("/getuserById")
    public ResponseEntity<User> getUserById(@RequestHeader("id") UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<UserOrderResponse>> getUserOrder(
            @RequestHeader("id") UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<UserOrderResponse> list = orderServiceClient.getOrderOfUser(id, page, size);
        log.info(list.toString());
        return ResponseEntity.ok(list);
    }


    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(@RequestHeader("id") UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userName}")
    public ResponseEntity<User> getUserByName(@PathVariable String userName) {
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
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/getUsernameAndEmailByID")
    public ResponseEntity<OrderUserDTO> getUsernameAndEmailByID(@RequestHeader("id") UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(
                OrderUserDTO.builder().
                username(user.getUsername())
                .email(user.getEmail())
                .build());
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestHeader("id") UUID id, @Valid @RequestBody User updatedCustomer) {
        return ResponseEntity.ok(userService.updateUser(id, updatedCustomer));
    }

    @PutMapping("/address")
    public ResponseEntity<User> updateUserAddress(@RequestHeader("id") UUID id, @RequestParam String newAddress) {
        return ResponseEntity.ok(userService.updateAddressOfUser(id, newAddress));
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteCustomer(@RequestHeader("id") UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
