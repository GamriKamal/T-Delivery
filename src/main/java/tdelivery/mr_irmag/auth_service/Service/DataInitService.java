package tdelivery.mr_irmag.auth_service.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.auth_service.Domain.Model.Role;
import tdelivery.mr_irmag.auth_service.Domain.Model.User;
import tdelivery.mr_irmag.auth_service.Exceptions.EmailAlreadyExistsException;
import tdelivery.mr_irmag.auth_service.Exceptions.UsernameAlreadyExistsException;

@Component
@Log4j2
@RequiredArgsConstructor
public class DataInitService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
        try {
            var admin = User.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456789"))
                    .role(Role.ROLE_ADMIN)
                    .build();

            userService.create(admin);

            var courier = User.builder()
                    .username("courier")
                    .email("courier@gmail.com")
                    .password(passwordEncoder.encode("123456789"))
                    .role(Role.ROLE_COURIER)
                    .build();

            userService.create(courier);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e){
            log.error("Error occurred: {}", e.getLocalizedMessage());
        } catch (Exception e){
            log.error("Error occurred: {}", e.getLocalizedMessage());
        }

    }
}