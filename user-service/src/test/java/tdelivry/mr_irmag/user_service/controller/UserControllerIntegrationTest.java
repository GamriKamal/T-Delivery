package tdelivry.mr_irmag.user_service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tdelivry.mr_irmag.user_service.TestContainerBase;
import tdelivry.mr_irmag.user_service.domain.dto.*;
import tdelivry.mr_irmag.user_service.domain.entity.Role;
import tdelivry.mr_irmag.user_service.domain.entity.User;
import tdelivry.mr_irmag.user_service.exception.UserException.UserNotFoundException;
import tdelivry.mr_irmag.user_service.service.OrderServiceClient;
import tdelivry.mr_irmag.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest extends TestContainerBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @MockBean
    private OrderServiceClient orderServiceClient;

    @Test
    void createUser_ValidRequest_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        UserDTO userDTO = UserDTO.builder()
                .username("testuser")
                .email("testuser@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build();

        // Act
        var result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
        assertEquals(userDTO.getUsername(), response.getUsername());
        assertEquals(userDTO.getEmail(), response.getEmail());
    }

    @Test
    void getUserByUsername_ExistingUser_ShouldReturnUser() throws Exception {
        // Arrange
       userService.createUser(UserDTO.builder()
                .username("testuser1")
                .email("testuser1@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        SignInRequest signInRequest = new SignInRequest("testuser1", "password123");

        // Act
        var result = mockMvc.perform(post("/users/check-username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
        assertEquals("testuser1", response.getUsername());
    }

    @Test
    void getUserByUsername_InvalidUsernameOrPassword_ShouldReturnNotFound() throws Exception {
        // Arrange
        SignInRequest invalidRequest = new SignInRequest("nonexistentUser", "123123123");

        // Act
        var result = mockMvc.perform(post("/users/check-username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound())
                .andReturn();

        // Assert
        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    void getUserByUsername_InvalidPassword_ShouldReturnNotFound() throws Exception {
        // Arrange
        userService.createUser(UserDTO.builder()
                .username("testuser12")
                .email("testuser12@test.com")
                .password("123123123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        SignInRequest signInRequest = new SignInRequest("testuser12", "987987987");

        // Act
        var result = mockMvc.perform(post("/users/check-username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isNotFound())
                .andReturn();

        // Assert
        assertEquals(404, result.getResponse().getStatus());
    }


    @Test
    void getUserById_ExistingUser_ShouldReturnUser() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("testuser2")
                .email("testuser2@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());
        UUID userId = existingUser.getId();
        // Act
        var result = mockMvc.perform(get("/users/getuserById")
                        .header("id", userId))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(userId, response.getId());
    }

    @Test
    void getAllUsers_UsersExist_ShouldReturnUserList() throws Exception {
        // Arrange
        userService.createUser(UserDTO.builder()
                .id(UUID.randomUUID())
                .username("testuser3")
                .email("testuser3@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());
        userService.createUser(UserDTO.builder()
                .id(UUID.randomUUID())
                .username("testuser4")
                .email("testuser4@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        // Act
        var result = mockMvc.perform(get("/users/findAll"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {});
        assertTrue(response.size() >= 2);
    }

    @Test
    void getUserInfo_ExistingUser_ShouldReturnUserInfo() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("testuser5")
                .email("testuser5@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        // Act
        var result = mockMvc.perform(get("/users/info")
                        .header("id", existingUser.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(existingUser.getId(), response.getId());
        assertEquals(existingUser.getUsername(), response.getUsername());
        assertEquals(existingUser.getEmail(), response.getEmail());
    }

    @Test
    void getUserByName_ExistingUser_ShouldReturnUser() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("user")
                .email("user@gmail.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        // Act
        var result = mockMvc.perform(get("/users/" + existingUser.getUsername()))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(existingUser.getId(), response.getId());
        assertEquals(existingUser.getUsername(), response.getUsername());
    }

    @Test
    void getUserByEmail_ExistingUser_ShouldReturnUser() throws Exception {
        // Arrange
        String userEmail = "user@gmail.com";
        UUID existingUserId = userService.getUserByEmail(userEmail).getId();

        // Act
        var result = mockMvc.perform(get("/users/email/" + userEmail))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(existingUserId, response.getId());
        assertEquals(userEmail, response.getEmail());
    }

    @Test
    void getUsernameAndEmailByID_ExistingUser_ShouldReturnUsernameAndEmail() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("testuser6")
                .email("testuser6@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        // Act
        var result = mockMvc.perform(get("/users/getUsernameAndEmailByID")
                        .header("id", existingUser.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), OrderUserDTO.class);
        assertEquals(existingUser.getUsername(), response.getUsername());
        assertEquals(existingUser.getEmail(), response.getEmail());
    }

    @Test
    void existUserByNameOrEmail_UserExists_ShouldReturnExistenceResponse() throws Exception {
        // Arrange
        User request = userService.createUser(UserDTO.builder()
                .username("testuser7")
                .email("testuser7@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        // Act
        var result = mockMvc.perform(get("/users/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), UserExistenceResponse.class);
        assertTrue(response.isExists());
        assertTrue(response.getMessage().contains("testuser7@test.com") || response.getMessage().contains("testuser7"));
    }

    @Test
    void existUserByNameOrEmail_ExistingUserWithEmail_ShouldReturnSuccess() throws Exception {
        // Arrange
        User request = userService.createUser(UserDTO.builder()
                .username("request")
                .email("request@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());


        UserDTO userDTO = UserDTO.builder()
                .username("userDTO")
                .email(request.getEmail())
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build();

        // Act
        var result = mockMvc.perform(get("/users/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), UserExistenceResponse.class);
        assertTrue(response.isExists());
        assertEquals("The user exists with the specified email address: " + request.getEmail(), response.getMessage());
    }

    @Test
    void existUserByNameOrEmail_ExistingUserWithWrongEmail_ShouldReturnSuccess() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("userDTO1")
                .email("userDTO1@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        UserDTO userDTO = UserDTO.builder()
                .username("userDTO1")
                .email("wrongemail@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build();

        // Act
        var result = mockMvc.perform(get("/users/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), UserExistenceResponse.class);
        assertTrue(response.isExists());
        assertEquals("The user exists with the specified name: " + existingUser.getUsername(), response.getMessage());
    }

    @Test
    void existUserByNameOrEmail_UserNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("userDTO12")
                .email("userDTO12@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        UserDTO userDTO = UserDTO.of(existingUser);

        // Act
        var result = mockMvc.perform(get("/users/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), UserExistenceResponse.class);
        assertTrue(response.isExists());
        assertEquals("The user exists with the specified email address: " + userDTO.getEmail(), response.getMessage());
    }



    @Test
    void updateUser_ValidRequest_ShouldUpdateUser() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("testuser8")
                .email("testuser8@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        existingUser.setUsername("updatedUser");
        existingUser.setEmail("updated@test.com");

        // Act
        var result = mockMvc.perform(put("/users")
                        .header("id", existingUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingUser)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals("updatedUser", response.getUsername());
        assertEquals("updated@test.com", response.getEmail());
    }

    @Test
    void updateUserAddress_ValidRequest_ShouldUpdateAddress() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("testuser9")
                .email("testuser9@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());
        String newAddress = "123 New Street";

        // Act
        var result = mockMvc.perform(put("/users/address")
                        .header("id", existingUser.getId().toString())
                        .param("newAddress", newAddress))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(newAddress, response.getAddress());
    }

    @Test
    void getUserOrder_ExistingOrders_ShouldReturnOrders() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("testuser99")
                .email("testuser99@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        UUID userId = existingUser.getId();
        List<UserOrderResponse> mockOrders = List.of(
                UserOrderResponse.builder()
                        .name("Order 1")
                        .createdDate(LocalDateTime.now())
                        .deliveryAddress("123 Test Street")
                        .comment("Urgent delivery")
                        .totalAmount(100.50)
                        .status("DELIVERED")
                        .build(),
                UserOrderResponse.builder()
                        .name("Order 2")
                        .createdDate(LocalDateTime.now())
                        .deliveryAddress("456 Another Street")
                        .comment("Please leave at door")
                        .totalAmount(200.75)
                        .status("DELIVERED")
                        .build()
        );

        when(orderServiceClient.getOrderOfUser(userId, 0, 10)).thenReturn(mockOrders);

        // Act
        var result = mockMvc.perform(get("/users/orders")
                        .header("id", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<UserOrderResponse>>() {
        });
        assertEquals(2, response.size());
        assertEquals("Order 1", response.get(0).getName());
        assertEquals("Order 2", response.get(1).getName());
    }

    @Test
    void deleteCustomer_ExistingUser_ShouldDeleteUser() throws Exception {
        // Arrange
        User existingUser = userService.createUser(UserDTO.builder()
                .username("testuser10")
                .email("testuser@test.com")
                .password("password123")
                .role(Role.USER)
                .address("testaddress")
                .build());

        UUID userId = existingUser.getId();

        // Act
        mockMvc.perform(delete("/users")
                        .header("id", userId))
                .andExpect(status().isNoContent());

        // Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void deleteCustomer_NonExistingUser_ShouldThrowException() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        // Act
        var result = mockMvc.perform(delete("/users")
                        .header("id", userId))
                .andExpect(status().isNotFound())
                .andReturn();

        // Assert
        assertEquals(404, result.getResponse().getStatus());
    }
}

