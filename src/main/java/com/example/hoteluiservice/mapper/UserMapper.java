package com.example.hoteluiservice.mapper;

import com.example.hoteluiservice.dto.UpdateUserDto;
import com.example.hoteluiservice.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UpdateUserDto toUpdateUserDto(UserDto userDto) {
        if (userDto == null) {
            return new UpdateUserDto();
        }

        return UpdateUserDto.builder()
                .username(userDto.getUsername())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .build();
    }
}
