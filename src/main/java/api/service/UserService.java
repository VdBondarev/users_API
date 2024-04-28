package api.service;

import api.dto.UserResponseDto;
import api.dto.UserSearchParametersRequestDto;
import api.dto.UserUpdateRequestDto;
import api.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDto getMyInfo(User user);

    UserResponseDto updateMyInfo(UserUpdateRequestDto requestDto, User user);

    void delete(Long id);

    UserResponseDto updateRole(Long id, String roleName);

    List<UserResponseDto> search(UserSearchParametersRequestDto requestDto, Pageable pageable);

    UserResponseDto findById(Long id);
}
