package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager testEM;
    @Autowired
    private CommentRepository commentRepository;

    private final User owner = new User(null, "owner","owner@email.com");
    private final User commentator = new User(null, "commentator","commentator@email.com");
    private final Item item = new Item(null, "name", "desc",
            true, owner, null);

    private final LocalDateTime testTime = LocalDateTime.now();

    Comment comment = Comment.builder()
            .text("commentText")
            .created(testTime)
            .item(item)
            .author(commentator)
            .build();

    @BeforeEach
    public void persistData() {
        testEM.persist(owner);
        testEM.persist(commentator);
        testEM.persist(item);
        testEM.flush();

        commentRepository.save(comment);
    }

    @Test
    public void findCommentsTest() {
        comment.setId(1L);

        Comment commentFromRepo = commentRepository.findById(1L).orElseThrow();

        assertEquals(comment, commentFromRepo);
        assertEquals(commentator, commentFromRepo.getAuthor());
        assertEquals(item, commentFromRepo.getItem());
        assertEquals(1, commentRepository.findAll().size());
        assertTrue(commentRepository.findAll().contains(comment));
        assertEquals(1, commentRepository.findAllByItem_Id(1L).size());
        assertTrue(commentRepository.findAllByItem_Id(1L).contains(comment));
    }


}
