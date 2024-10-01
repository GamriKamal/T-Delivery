package tdelivery.mr_irmag.auth_service.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.auth_service.Domain.Model.Role;
import tdelivery.mr_irmag.auth_service.Domain.Model.User;
import tdelivery.mr_irmag.auth_service.Exceptions.EmailAlreadyExistsException;
import tdelivery.mr_irmag.auth_service.Exceptions.UsernameAlreadyExistsException;
import tdelivery.mr_irmag.auth_service.Repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User create(User user) {
        try {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new UsernameAlreadyExistsException("A user with that name already exists!");
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                throw new EmailAlreadyExistsException("A user with this email already exists!");
            }

            return save(user);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred when creating a user: ", e);
        }
    }


    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        save(user);
    }
}
