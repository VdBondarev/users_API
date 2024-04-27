package api.service;

import api.dto.UserResponseDto;
import api.dto.UserUpdateRequestDto;
import api.mapper.UserMapper;
import api.model.User;
import api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto getMyInfo(User user) {
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateMyInfo(UserUpdateRequestDto requestDto) {

    }
}
