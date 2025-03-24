package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.UserCreateRequestDto;
import nl.novi.eindprojectbackend.dtos.UserResponseDto;
import nl.novi.eindprojectbackend.models.Authority;
import nl.novi.eindprojectbackend.models.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static User toEntity(UserCreateRequestDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setEnabled(true); // default true
        return user;
    }

    public static UserResponseDto toResponseDto(User user) {
        Set<String> roles = user.getAuthorities().stream()
                .map(Authority::getAuthority)
                .collect(Collectors.toSet());

        return new UserResponseDto(
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.getApikey(),
                roles
        );
    }
}
