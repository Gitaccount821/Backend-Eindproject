package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.UserDto;
import nl.novi.eindprojectbackend.models.Authority;
import nl.novi.eindprojectbackend.models.User;

public class UserMapper {



    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setEnabled(dto.isEnabled());
        user.setApikey(dto.getApikey());

        if (dto.getRoles() != null) {
            dto.getRoles().forEach(role -> {
                if (role.equals("ROLE_KLANT") || role.equals("ROLE_MEDEWERKER") || role.equals("ROLE_MONTEUR")) {
                    user.addAuthority(new Authority(dto.getUsername(), role));
                } else {
                    throw new IllegalArgumentException("Invalid role: " + role);
                }
            });
        }
        return user;
    }

}
