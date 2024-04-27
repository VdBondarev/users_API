package api.service;

import api.dto.UserResponseDto;
import api.dto.UserUpdateRequestDto;
import api.model.User;

public interface UserService {
    UserResponseDto getMyInfo(User user);

    UserResponseDto updateMyInfo(UserUpdateRequestDto requestDto, User user);

    void delete(Long id);

    UserResponseDto updateRole(Long id, String roleName);
}
