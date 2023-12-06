package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userRepository.create(user));
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for(User user : userRepository.getAll()) {
            users.add(userMapper.toDto(user));
        }
        return users;
    }

    @Override
    public UserDto getById(Long id) {
        return userMapper.toDto(userRepository.getById(id));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userRepository.update(user));
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }
}
