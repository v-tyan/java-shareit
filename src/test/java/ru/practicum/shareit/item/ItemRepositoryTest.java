package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
class ItemRepositoryTest {
    User user = User.builder()
            .id(null)
            .name("user")
            .email("user@user.ru")
            .build();
    Item item = Item.builder()
            .id(null)
            .name("name1")
            .description("description first")
            .available(true)
            .owner(user)
            .request(null)
            .build();
    Item item2 = Item.builder()
            .id(null)
            .name("name2")
            .description("description second")
            .available(true)
            .owner(user)
            .request(null)
            .build();
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllByOwnerIdOrderByIdAscTest() {
        em.persist(user);
        em.persist(item2);
        em.persist(item);
        PageRequest p = PageRequest.of(0, 20);

        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(user.getId(), p);

        assertEquals(item2, items.get(0));
        assertEquals(item, items.get(1));
    }

    @Test
    void searchByTextTest() {
        em.persist(user);
        em.persist(item2);
        em.persist(item);
        PageRequest p = PageRequest.of(0, 20);

        List<Item> items = itemRepository.searchItems("first", p);
        assertEquals(1, items.size());
        assertEquals("name1", items.get(0).getName());
    }

    @Test
    void searchByRequestsIdTest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(null)
                .description("Description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        Item itemWithRequest = Item.builder()
                .id(null)
                .name("name3")
                .description("item with request")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        em.persist(user);
        em.persist(itemRequest);
        em.persist(itemWithRequest);

        List<Item> items = itemRepository.searchByRequestsId(List.of(2L));
        assertEquals(1, items.size());
        assertEquals("name3", items.get(0).getName());
        assertEquals("item with request", items.get(0).getDescription());
    }

    @Test
    void findByItemRequestIdTest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(null)
                .description("Description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        Item itemWithRequest = Item.builder()
                .id(null)
                .name("name3")
                .description("item with request")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        em.persist(user);
        em.persist(itemRequest);
        em.persist(itemWithRequest);

        List<Item> items = itemRepository.findByItemRequestId(1L);
        assertEquals("name3", items.get(0).getName());
        assertEquals("item with request", items.get(0).getDescription());
    }
}