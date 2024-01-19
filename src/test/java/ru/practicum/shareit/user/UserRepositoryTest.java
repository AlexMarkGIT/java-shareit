package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private final User user = User.builder()
            .name("name")
            .email("email@email.com")
            .build();

    @BeforeEach
    public void persistData() {
        userRepository.save(user);
    }

    @Test
    public void getByIdTest() {
        User userFromRep = userRepository.findById(1L).orElseThrow();

        assertEquals(1L, userFromRep.getId());
        assertEquals("name", userFromRep.getName());
        assertEquals("email@email.com", userFromRep.getEmail());
    }

    @Test
    public void getAllTest() {
        User userExpected = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .build();

        List<User> userAll = userRepository.findAll();

        assertTrue(userAll.size() == 1);
        assertTrue(userAll.contains(userExpected));
    }



}
