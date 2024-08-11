package org.example.chatapi.user.mapper;


import org.example.chatapi.user.dto.UserDto;
import org.example.chatapi.user.model.UserModel;

public class UserMapper {

    public UserDto entityToDto(UserModel user) {
        return UserDto
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .isActive(user.getIsActive())
                .isBlocked(user.getIsBlocked())
                .build();
    }

}
