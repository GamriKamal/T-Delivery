package tdelivery.mr_irmag.auth_service.service;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.auth_service.domain.dto.SignInRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.SignUpRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.UserDTO;
import tdelivery.mr_irmag.auth_service.domain.model.Role;
import tdelivery.mr_irmag.auth_service.domain.model.User;
import tdelivery.mr_irmag.auth_service.exceptions.UserNotFoundException;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Gson gson;

    @InjectMocks
    private UserServiceClient userServiceClient;

    @Value("${tdelivery.user-service.url}")
    private String userServiceURL = "http://localhost:8080";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        userServiceClient = new UserServiceClient(restTemplate, gson);

        Field userServiceURLField = UserServiceClient.class.getDeclaredField("userServiceURL");
        userServiceURLField.setAccessible(true);
        userServiceURLField.set(userServiceClient, userServiceURL);
    }


    @Test
    void getUserByUsername_UsernameNull_ShouldThrowIllegalArgumentException() {
        // Arrange
        SignInRequest request = new SignInRequest(" ", "");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userServiceClient.getUserByUsername(request)
        );
        assertEquals("Username must not be null or empty", exception.getMessage());
    }

    @Test
    void getUserByUsername_UsernameEmpty_ShouldThrowIllegalArgumentException() {
        // Arrange
        SignInRequest request = new SignInRequest(" ", " ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userServiceClient.getUserByUsername(request)
        );
        assertEquals("Username must not be null or empty", exception.getMessage());
    }

    @Test
    void getUserByUsername_ValidUsername_ShouldReturnUser() {
        // Arrange
        SignInRequest request = new SignInRequest("validUsername", "pass");
        UUID uuid = UUID.randomUUID();
        String jsonResponse = "{\"id\":" + uuid + ",\"username\":\"validUsername\",\"password\":\"pass\"}";
        UserDTO userDTO = new UserDTO(UUID.randomUUID(), "validUsername", "validUsername@gmail.com", "pass", Role.USER, "someAddress");
        User expectedUser = User.of(userDTO);

        when(restTemplate.exchange(
                eq(userServiceURL + "/check-username"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
        when(gson.fromJson(jsonResponse, UserDTO.class)).thenReturn(userDTO);

        // Act
        User actualUser = userServiceClient.getUserByUsername(request);

        // Assert
        assertEquals(expectedUser.getId(), actualUser.getId());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        verify(gson, times(1)).fromJson(jsonResponse, UserDTO.class);
    }

    @Test
    void getUserByUsername_UserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        SignInRequest request = new SignInRequest("unknownUser", "pass");

        when(restTemplate.exchange(
                eq(userServiceURL + "/check-username"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenThrow(HttpClientErrorException.NotFound.create(
                HttpStatus.NOT_FOUND,
                "User not found",
                null,
                null,
                null
        ));

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userServiceClient.getUserByUsername(request)
        );
        assertTrue(exception.getMessage().contains("User with username unknownUser not found."));
    }

    @Test
    void createUser_ValidRequest_ShouldReturnUser() {
        // Arrange
        SignUpRequest request = new SignUpRequest("newUser", "newUser@gmail.com", "pass");
        UUID uuid = UUID.randomUUID();
        String jsonResponse = "{\"id\":" + uuid + ",\"username\":\"newUser\",\"password\":\"pass\"}";
        UserDTO userDTO = new UserDTO(uuid, "newUser", "newUser@gmail.com", "pass", Role.USER, "someAddress");
        User expectedUser = User.of(userDTO);

        when(restTemplate.exchange(
                eq(userServiceURL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.CREATED));
        when(gson.fromJson(jsonResponse, UserDTO.class)).thenReturn(userDTO);

        // Act
        User actualUser = userServiceClient.createUser(request);

        // Assert
        assertEquals(expectedUser.getId(), actualUser.getId());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        verify(gson, times(1)).fromJson(jsonResponse, UserDTO.class);
    }
}
