package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ResponseUserDto toResponseUserDto(User user);

    User toEntity(RequestUserDto userDto);
}
