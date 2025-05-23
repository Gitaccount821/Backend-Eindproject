package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.UserCreateRequestDto;
import nl.novi.eindprojectbackend.dtos.UserResponseDto;
import nl.novi.eindprojectbackend.services.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponseDto registerUser(@Valid @RequestBody UserCreateRequestDto userDto) {
        return userService.registerUser(userDto);
    }

    @PostMapping("/create-user")
    public UserResponseDto createUser(@Valid @RequestBody UserCreateRequestDto userDto, @RequestParam String role) {
        return userService.createUser(userDto, role);
    }

}
