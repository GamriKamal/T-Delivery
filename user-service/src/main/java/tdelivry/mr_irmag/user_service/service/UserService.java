package tdelivry.mr_irmag.user_service.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tdelivry.mr_irmag.user_service.domain.dto.UserDTO;
import tdelivry.mr_irmag.user_service.domain.entity.Role;
import tdelivry.mr_irmag.user_service.domain.entity.User;
import tdelivry.mr_irmag.user_service.exception.UserException.UserNotFoundException;
import tdelivry.mr_irmag.user_service.exception.UserException.FieldAlreadyExistsException;
import tdelivry.mr_irmag.user_service.exception.UserException.InvalidUserDataException;
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
        var user = mapToEntity(userDTO);

        checkNameExists(user.getUsername());
        checkEmailExists(user.getEmail());
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

    public User existUserByNameOrEmail(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String email = userDTO.getEmail();

        Optional<User> userByName = userRepository.findByUsername(username);

        if (userByName.isPresent()) {
            return userByName.get();
        }

        Optional<User> userByEmail = userRepository.findByEmail(email);

        if (userByEmail.isPresent()) {
            return userByEmail.get();
        }

        return null;
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User updateAddressOfUser(UUID id, String newAddress) {
        if (newAddress == null || newAddress.isEmpty()) {
            throw new InvalidUserDataException("The new address is null or empty!");
        }

        User existingUser = getUserById(id);
        existingUser.setAddress(newAddress);
        return userRepository.save(existingUser);
    }

    public User updateUser(UUID id, @Valid User updatedUser) {
        checkEmailExists(updatedUser.getEmail());

        User existingUser = getUserById(id);
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

    private void checkEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new FieldAlreadyExistsException("Email already exists: " + email);
        }
    }

    private void checkNameExists(String name) {
        if (userRepository.findByUsername(name).isPresent()) {
            throw new FieldAlreadyExistsException("Name already exists: " + name);
        }
    }

    public User mapToEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .address(userDTO.getAddress())
                .build();
    }

    public UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .address(user.getAddress())
                .build();
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


}
