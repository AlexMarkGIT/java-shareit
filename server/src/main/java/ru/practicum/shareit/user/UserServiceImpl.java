package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
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
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(userMapper.toDto(user));
        }
        return users;
    }

    @Override
    public UserDto getById(Long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с таким id: " + id)));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userDto.getId()));
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
