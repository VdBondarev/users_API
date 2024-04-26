package api.mapper;

import api.config.MapperConfig;
import api.dto.UserRegistrationRequestDto;
import api.dto.UserResponseDto;
import api.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    UserResponseDto toResponseDto(User user);
}
