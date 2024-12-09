package tdelivery.mr_irmag.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.auth_service.domain.dto.JwtAuthenticationResponse;
import tdelivery.mr_irmag.auth_service.domain.dto.SignInRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.SignUpRequest;
import tdelivery.mr_irmag.auth_service.domain.model.User;
import tdelivery.mr_irmag.auth_service.exceptions.FieldAlreadyExistsException;
import tdelivery.mr_irmag.auth_service.exceptions.UserNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        User user;
        try {
            user = userServiceClient.createUser(request);
        } catch (FieldAlreadyExistsException e) {
            throw new FieldAlreadyExistsException("User with this field already exists: " + e.getLocalizedMessage());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while signing up: " + e.getLocalizedMessage());
        }

        log.info("User created: {}", user);
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        try {
            User user = userServiceClient.getUserByUsername(request);
            var jwt = jwtService.generateToken(user);
            return new JwtAuthenticationResponse(jwt);
        } catch (UserNotFoundException e) {
            log.info("User not found: {}", e.getLocalizedMessage());
            throw new UserNotFoundException("User with username not found: " + e.getLocalizedMessage());
        } catch (Exception e) {
            log.error("An error occurred during sign in: {}", e.getLocalizedMessage());
            throw new RuntimeException("An internal server error occurred during sign in: " + e.getLocalizedMessage());
        }
    }

}
