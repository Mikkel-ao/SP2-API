package app.mappers;

import app.dtos.UserProfileDTO;
import app.entities.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserProfileDTO toDTO(User user) {
        if (user == null) return null;

        return UserProfileDTO.builder()
                .username(user.getUsername())
                .roles(user.getRoles().stream()
                        .map(r -> r.getRoleName().toUpperCase())
                        .collect(Collectors.toSet()))
                .build();
    }
}
