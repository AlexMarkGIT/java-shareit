package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Создание пользователя...");
        UserDto user = userService.create(userDto);
        log.info("Пользователь создан");
        return user;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Запрос всех пользователей...");
        List<UserDto> users = userService.getAll();
        log.info("Список пользователей отправлен");
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Запрос пользователя с id: " + id + "...");
        UserDto user = userService.getById(id);
        log.info("Пользователь отправлен");
        return  user;
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Запрос на обновление пользователя с id: " + id + "...");
        userDto.setId(id);
        UserDto user = userService.update(userDto);
        log.info("Пользователь обновлен");
        return user;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя с id: " + id + "...");
        userService.delete(id);
        log.info("Пользоватль удален");
    }

}
