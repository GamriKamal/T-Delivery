package tdelivery.mr_irmag.auth_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tdelivery.mr_irmag.auth_service.domain.dto.JwtAuthenticationResponse;
import tdelivery.mr_irmag.auth_service.domain.dto.SignInRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.SignUpRequest;
import tdelivery.mr_irmag.auth_service.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы для аутентификации пользователей")
@Log4j2
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Регистрация пользователя",
            description = "Этот метод позволяет зарегистрировать нового пользователя в системе и получить JWT токен для последующей аутентификации.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Регистрация прошла успешно, токен выдан")
            }
    )
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(
            @RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(
            summary = "Авторизация пользователя",
            description = "Этот метод позволяет пользователю авторизоваться в системе с помощью логина и пароля и получить JWT токен.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Авторизация прошла успешно, токен выдан")
            }
    )
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(
            @RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }
}


