package tdelivery.mr_irmag.auth_service.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import tdelivery.mr_irmag.auth_service.Domain.DTO.JwtAuthenticationResponse;
import tdelivery.mr_irmag.auth_service.Domain.DTO.SignInRequest;
import tdelivery.mr_irmag.auth_service.Domain.DTO.SignUpRequest;
import tdelivery.mr_irmag.auth_service.Service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
@Log4j2
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        log.info("Request: {}", request.toString());
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @GetMapping("/getAdmin")
    public void getAdmin(){
        authenticationService.getAdmin();
    }

}
