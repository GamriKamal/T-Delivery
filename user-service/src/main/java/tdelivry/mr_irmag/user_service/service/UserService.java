package tdelivry.mr_irmag.user_service.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tdelivry.mr_irmag.user_service.domain.dto.UserDTO;
import tdelivry.mr_irmag.user_service.domain.entity.Role;
import tdelivry.mr_irmag.user_service.domain.entity.User;
import tdelivry.mr_irmag.user_service.exception.UserException.FieldAlreadyExistsException;
import tdelivry.mr_irmag.user_service.exception.UserException.InvalidUserDataException;
import tdelivry.mr_irmag.user_service.exception.UserException.UserNotFoundException;
import tdelivry.mr_irmag.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserDTO userDTO) {
        var user = User.of(userDTO);

        checkFieldExistence("username", user.getUsername());
        checkFieldExistence("email", user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public User getUserByName(String name) {
        return userRepository.findByUsername(name)
                .orElseThrow(() -> new UserNotFoundException("User not found with name: " + name));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public User findExistingUser(UserDTO userDTO) {
        return userRepository.findByUsername(userDTO.getUsername())
                .or(() -> userRepository.findByEmail(userDTO.getEmail()))
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + userDTO.getUsername() + " or email: " + userDTO.getEmail()));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateAddressOfUser(UUID id, String newAddress) {
        validateAddress(newAddress);

        var existingUser = getUserById(id);
        existingUser.setAddress(newAddress);

        return userRepository.save(existingUser);
    }

    public User updateUser(UUID id, @Valid User updatedUser) {
        checkFieldExistence("email", updatedUser.getEmail());

        var existingUser = getUserById(id);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setAddress(updatedUser.getAddress());

        return userRepository.save(existingUser);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private void checkFieldExistence(String field, String value) {
        var exists = switch (field) {
            case "username" -> userRepository.findByUsername(value).isPresent();
            case "email" -> userRepository.findByEmail(value).isPresent();
            default -> false;
        };

        if (exists) {
            throw new FieldAlreadyExistsException(field + " already exists: " + value);
        }
    }

    private void validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new InvalidUserDataException("The new address is null or empty!");
        }
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
