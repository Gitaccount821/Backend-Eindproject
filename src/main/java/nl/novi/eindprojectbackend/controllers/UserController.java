package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.UserDto;
import nl.novi.eindprojectbackend.mappers.UserMapper;
import nl.novi.eindprojectbackend.models.Authority;
import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.repositories.UserRepository;
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

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        try {
            if (userRepository.existsById(userDto.getUsername())) {
                return ResponseEntity.badRequest().body("Username already exists!");
            }

            User user = UserMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.addAuthority(new Authority(user.getUsername(), "ROLE_KLANT"));

            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully with role: ROLE_KLANT");
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

        if (userRepository.existsById(userDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        User user = UserMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        String assignedRole;
        switch (role.toUpperCase()) {
            case "MEDEWERKER":
                assignedRole = "ROLE_MEDEWERKER";
                break;
            case "MONTEUR":
                assignedRole = "ROLE_MONTEUR";
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid role. Only MEDEWERKER or MONTEUR allowed.");
        }

        user.addAuthority(new Authority(user.getUsername(), assignedRole));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully with role: " + assignedRole);
    }
}
