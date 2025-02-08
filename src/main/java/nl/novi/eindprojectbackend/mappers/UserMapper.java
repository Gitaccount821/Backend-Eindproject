package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.UserDto;
import nl.novi.eindprojectbackend.models.Authority;
import nl.novi.eindprojectbackend.models.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.isEnabled(),
                user.getApikey(),
                user.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toSet())
        );
    }

    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setEnabled(dto.isEnabled());
        user.setApikey(dto.getApikey());

        dto.getRoles().forEach(role -> user.addAuthority(new Authority(dto.getUsername(), role)));
        return user;
    }
}
