package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.dtos.UserCreateRequestDto;
import nl.novi.eindprojectbackend.dtos.UserResponseDto;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.ForbiddenActionException;
import nl.novi.eindprojectbackend.mappers.UserMapper;
import nl.novi.eindprojectbackend.models.Authority;
import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserResponseDto registerUser(UserCreateRequestDto userDto) {
        if (userRepository.existsById(userDto.getUsername())) {
            throw new BadRequestException("Username already exists!");
        }

        User user = UserMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.addAuthority(new Authority(user.getUsername(), "ROLE_KLANT"));
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDto(savedUser);
    }

    public UserResponseDto createUser(UserCreateRequestDto userDto, String role) {
        String currentUserRole = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(a -> a.getAuthority())
                .findFirst().orElse("");

        if (!currentUserRole.equals("ROLE_MEDEWERKER")) {
            throw new ForbiddenActionException();
        }


        if (userRepository.existsById(userDto.getUsername())) {
            throw new BadRequestException("Username already exists!");
        }

        String assignedRole = switch (role.toUpperCase()) {
            case "MEDEWERKER" -> "ROLE_MEDEWERKER";
            case "MONTEUR" -> "ROLE_MONTEUR";
            default -> throw new BadRequestException("Invalid role. Only MEDEWERKER or MONTEUR allowed.");
        };

        User user = UserMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.addAuthority(new Authority(user.getUsername(), assignedRole));
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDto(savedUser);
    }
}
