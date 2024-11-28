package tdelivry.mr_irmag.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import tdelivry.mr_irmag.user_service.TestContainerBase;
import tdelivry.mr_irmag.user_service.domain.dto.UserDTO;
import tdelivry.mr_irmag.user_service.domain.entity.Role;
import tdelivry.mr_irmag.user_service.domain.entity.User;
import tdelivry.mr_irmag.user_service.exception.UserException.FieldAlreadyExistsException;
import tdelivry.mr_irmag.user_service.exception.UserException.InvalidUserDataException;
import tdelivry.mr_irmag.user_service.exception.UserException.UserNotFoundException;
import tdelivry.mr_irmag.user_service.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest extends TestContainerBase {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void createUser_ExistingUsername_ShouldThrowFieldAlreadyExistsException() {
        // Arrange
        UserDTO userDTO = UserDTO.builder()
                .username("testuser1")
                .email("testuser1@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build();
        userRepository.save(User.of(userDTO));

        // Act & Assert
        assertThrows(FieldAlreadyExistsException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_ExistingEmail_ShouldThrowFieldAlreadyExistsException() {
        // Arrange
        UserDTO userDTO = UserDTO.builder()
                .username("testuser11")
                .email("testuser1@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build();
        userService.createUser(userDTO);
        userDTO.setUsername("testuser112");


        // Act & Assert
        assertThrows(FieldAlreadyExistsException.class, () -> userService.createUser(userDTO));
    }


    @Test
    void getUserByEmail_UserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        String email = "nonexistentuser@test.com";

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByEmail(email);
        });
    }


    @Test
    void findExistingUser_UserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        UserDTO userDTO = UserDTO.builder()
                .username("nonexistentuser")
                .email("nonexistentuser@test.com")
                        .build();

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.findExistingUser(userDTO);
        });
    }

    @Test
    void updateAddressOfUser_InvalidAddress_ShouldThrowInvalidUserDataException() {
        // Arrange
        var user = userService.createUser(UserDTO.builder()
                .username("testUser")
                .email("testUser1@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());
        String invalidAddress = "";

        // Act & Assert
        assertThrows(InvalidUserDataException.class, () -> userService.updateAddressOfUser(user.getId(), invalidAddress));
    }


}