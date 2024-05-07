package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class CommentRepositoryTest {
    User user = User.builder()
            .id(null)
            .name("user")
            .email("user@user.ru")
            .build();
    Item item = Item.builder()
            .id(null)
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .request(null)
            .build();
    Comment comment = Comment.builder()
            .id(null)
            .text("text")
            .item(item)
            .author(user)
            .created(LocalDateTime.now())
            .build();
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllComments() {
        em.persist(user);
        em.persist(item);
        em.persist(comment);

        List<Comment> comments = commentRepository.findByItemId_IdIn(List.of(item.getId()));

        assertEquals(1, comments.size());
        assertEquals(comment, comments.get(0));
    }
}