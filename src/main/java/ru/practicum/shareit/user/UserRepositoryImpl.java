package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();
    private final HashSet<String> usersEmails = new HashSet<>();
    private Long identifier = 0L;

    @Override
    public User create(User user) {
        if (usersEmails.contains(user.getEmail())) {
            throw new UserAlreadyExistException("Такой пользователь уже создан");
        }
        user.setId(++identifier);
        users.put(user.getId(), user);
        usersEmails.add(user.getEmail());
        return user;
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("пользователь с id: " + id + "не найден");
        }
        return users.get(id);
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("пользователь с id: " + user.getId() + "не найден");
        }
        if (user.getEmail() == null && !user.getName().isBlank()) {
            User userUpd = users.get(user.getId());
            userUpd.setName(user.getName());
            users.put(userUpd.getId(), userUpd);
            return userUpd;
        }
        if (isEmailUnique(user)) {
            User userUpd = users.get(user.getId());
            usersEmails.remove(userUpd.getEmail());
            if (user.getName() == null) {
                userUpd.setEmail(user.getEmail());
            } else {
                userUpd.setName(user.getName());
                userUpd.setEmail(user.getEmail());
            }
            usersEmails.add(userUpd.getEmail());
            users.put(userUpd.getId(), userUpd);
            return userUpd;
        } else {
            throw new EmailAlreadyExistException("Пользователь с такой почтой уже существует");
        }
    }

    @Override
    public void delete(Long id) {
        usersEmails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public Boolean contains(Long userId) {
        return users.containsKey(userId);
    }

    private Boolean isEmailUnique(User user) {
        return users.get(user.getId()).getEmail().equals(user.getEmail()) ||
                !usersEmails.contains(user.getEmail());
    }
}
