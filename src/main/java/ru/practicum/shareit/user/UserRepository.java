package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User create(User user);

    List<User> getAll();

    User getById(Long id);

    User update(User user);

    void delete(Long id);

    Boolean contains(Long userId);
}
