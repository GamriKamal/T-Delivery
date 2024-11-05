package tdelivry.mr_irmag.user_service.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tdelivry.mr_irmag.user_service.domain.entity.Role;
import tdelivry.mr_irmag.user_service.domain.entity.User;
import tdelivry.mr_irmag.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitConfig {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initAdminAndCourier() {
        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("12345678"))
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build());

        userRepository.save(User.builder()
                .username("courier")
                .password(passwordEncoder.encode("12345678"))
                .email("courier@gmail.com")
                .role(Role.COURIER)
                .build());
    }
}
