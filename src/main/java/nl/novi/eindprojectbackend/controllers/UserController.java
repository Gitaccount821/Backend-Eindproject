package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.UserDto;
import nl.novi.eindprojectbackend.mappers.UserMapper;
import nl.novi.eindprojectbackend.models.Authority;
import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.repositories.UserRepository;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 5 && password.matches(".*\\d.*");
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new BadRequestException("Username cannot be empty!");
        }

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new BadRequestException("Email cannot be empty!");
        }

        if (!userDto.getEmail().contains("@") || !userDto.getEmail().contains(".")) {
            throw new BadRequestException("Invalid email format!");
        }

        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new BadRequestException("Password cannot be empty!");
        }

        if (!isPasswordValid(userDto.getPassword())) {
            throw new BadRequestException("Password must be at least 5 characters long and include at least one number.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        try {
            validateUserDto(userDto);

            if (userRepository.existsById(userDto.getUsername())) {
                throw new BadRequestException("Username already exists!");
            }

            User user = UserMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.addAuthority(new Authority(user.getUsername(), "ROLE_KLANT"));

            user.setEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully with role: ROLE_KLANT");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body("Error during registration: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during registration: " + e.getMessage());
        }
    }


    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto, @RequestParam String role) {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals("ROLE_MEDEWERKER"))) {
            return ResponseEntity.status(403).body("Only Medewerkers can create new users!");
        }

        try {
            validateUserDto(userDto);

            if (userRepository.existsById(userDto.getUsername())) {
                throw new BadRequestException("Username already exists!");
            }

            User user = UserMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setEnabled(true);

            String assignedRole = switch (role.toUpperCase()) {
                case "MEDEWERKER" -> "ROLE_MEDEWERKER";
                case "MONTEUR" -> "ROLE_MONTEUR";
                default -> throw new BadRequestException("Invalid role. Only MEDEWERKER or MONTEUR allowed.");
            };

            user.addAuthority(new Authority(user.getUsername(), assignedRole));
            userRepository.save(user);

            return ResponseEntity.ok("User registered successfully with role: " + assignedRole);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body("Error during user creation: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during user creation: " + e.getMessage());
        }
    }
}
