package tdelivry.mr_irmag.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserController {
    private final UserService userService;
    private final OrderServiceClient orderServiceClient;


    @PostMapping
    @Operation(summary = "Создание пользователя", description = "Создает нового пользователя с указанными данными")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        var createdUser = userService.createUser(userDTO);
        var createdUserDTO = UserDTO.of(createdUser);
        log.info("User created: {}", createdUserDTO);
        return ResponseEntity.ok(createdUserDTO);
    }

    @PostMapping("/check-username")
    @Operation(summary = "Проверка имени пользователя", description = "Проверяет наличие пользователя по имени и паролю")
    public ResponseEntity<UserDTO> getUserByUsername(@Valid @RequestBody SignInRequest request) {
        var user = userService.getUserByName(request.getUsername());

        if (user == null || !userService.checkPassword(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        log.info("User found with username: {}", request.getUsername());
        return ResponseEntity.ok(UserDTO.of(user));
    }


    @GetMapping("/getuserById")
    @Operation(summary = "Получение пользователя по ID", description = "Возвращает информацию о пользователе по его уникальному идентификатору")
    public ResponseEntity<User> getUserById(@RequestHeader("id") @Parameter(description = "UUID пользователя") UUID id) {
        log.info("Fetching user by ID: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/info")
    @Operation(summary = "Получение информации о пользователе", description = "Возвращает полную информацию о пользователе по ID")
    public ResponseEntity<User> getUserInfo(@RequestHeader("id") @Parameter(description = "UUID пользователя") UUID id) {
        log.info("Fetching user info by ID: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{userName}")
    @Operation(summary = "Получение пользователя по имени", description = "Возвращает информацию о пользователе по его имени")
    public ResponseEntity<User> getUserByName(@PathVariable String userName) {
        var user = userService.getUserByName(userName);
        log.info("User found with name: {}", userName);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Получение пользователя по email", description = "Возвращает информацию о пользователе по его email")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/getUsernameAndEmailByID")
    @Operation(summary = "Получение имени и email пользователя по ID", description = "Возвращает имя и email пользователя по его ID")
    public ResponseEntity<OrderUserDTO> getUsernameAndEmailByID(@RequestHeader("id") @Parameter(description = "UUID пользователя") UUID id) {
        var user = userService.getUserById(id);
        var response = OrderUserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
        log.info("Fetched username and email for user ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findAll")
    @Operation(summary = "Получение всех пользователей", description = "Возвращает список всех пользователей")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Fetching all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/check")
    @Operation(summary = "Проверка существования пользователя", description = "Проверяет наличие пользователя по имени или email")
    public ResponseEntity<UserExistenceResponse> existUserByNameOrEmail(@Valid @RequestBody UserDTO userDTO) {
        var user = userService.findExistingUser(userDTO);

        var message = (user != null && user.getEmail().equalsIgnoreCase(userDTO.getEmail()))
                ? "Пользователь существует с указанным email: " + userDTO.getEmail()
                : "Пользователь существует с указанным именем: " + userDTO.getUsername();

        var response = new UserExistenceResponse(user != null, message, LocalDateTime.now());
        log.info("User existence check: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    @Operation(summary = "Получение заказов пользователя", description = "Возвращает заказы пользователя по его ID с пагинацией")
    public ResponseEntity<List<UserOrderResponse>> getUserOrder(
            @RequestHeader("id") @Parameter(description = "UUID пользователя") UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var orders = orderServiceClient.getOrderOfUser(id, page, size);
        log.info("Fetched orders for user ID: {}", id);
        return ResponseEntity.ok(orders);
    }

    @PutMapping
    @Operation(summary = "Обновление пользователя", description = "Обновляет информацию о пользователе по его ID")
    public ResponseEntity<User> updateUser(@RequestHeader("id") @Parameter(description = "UUID пользователя") UUID id, @Valid @RequestBody User updatedCustomer) {
        log.info("Updating user with ID: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, updatedCustomer));
    }

    @PutMapping("/address")
    @Operation(summary = "Обновление адреса пользователя", description = "Обновляет адрес пользователя по его ID")
    public ResponseEntity<User> updateUserAddress(@RequestHeader("id") @Parameter(description = "UUID пользователя") UUID id, @RequestParam String newAddress) {
        log.info("Updating address for user ID: {}", id);
        return ResponseEntity.ok(userService.updateAddressOfUser(id, newAddress));
    }

    @DeleteMapping
    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя по его ID")
    public ResponseEntity<Void> deleteCustomer(@RequestHeader("id") @Parameter(description = "UUID пользователя") UUID userId) {
        log.info("Deleting user with ID: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
