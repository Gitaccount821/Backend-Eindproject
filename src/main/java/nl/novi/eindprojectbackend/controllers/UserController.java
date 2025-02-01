package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.models.Authority;
import nl.novi.eindprojectbackend.repositories.UserRepository;
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
    public String registerUser(@RequestBody User user) {
        if (userRepository.existsById(user.getUsername())) {
            return "Username already exists!";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.addAuthority(new Authority(user.getUsername(), "ROLE_MONTEUR"));
        userRepository.save(user);
        return "User registered successfully!";
    }
}
