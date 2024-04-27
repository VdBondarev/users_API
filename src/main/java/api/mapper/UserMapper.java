package api.mapper;

import api.config.MapperConfig;
import api.dto.UserRegistrationRequestDto;
import api.dto.UserResponseDto;
import api.dto.UserUpdateRequestDto;
import api.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    User toModel(@MappingTarget User user, UserUpdateRequestDto requestDto);

    UserResponseDto toResponseDto(User user);
}
