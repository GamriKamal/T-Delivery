package tdelivery.mr_irmag.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tdelivery.mr_irmag.auth_service.domain.dto.JwtAuthenticationResponse;
import tdelivery.mr_irmag.auth_service.domain.dto.SignInRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.SignUpRequest;
import tdelivery.mr_irmag.auth_service.domain.model.User;
import tdelivery.mr_irmag.auth_service.exceptions.FieldAlreadyExistsException;

@Slf4j
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;


    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        User user;
        try {
            user = userServiceClient.createUser(request);
        } catch (FieldAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage() + e.getLocalizedMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while signing up " + e.getLocalizedMessage());
        }

        log.info("User created: {}", user);
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        User user = userServiceClient.getUserByUsername(request);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

}
