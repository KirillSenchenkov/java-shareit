package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    User userDtotoUser(UserDto userDto);

    UserDto usertoUserDto(User user);
}
