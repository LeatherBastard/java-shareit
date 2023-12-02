package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;


    @AfterEach
    void clearRepositories() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByText() {
        User user = new User(1, "Mark", "kostrykinmark@gmail.com");
        User savedUser = userRepository.save(user);
        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
        itemRepository.save(
                Item.builder()
                        .name("Пылесос")
                        .description("Пылесос")
                        .owner(savedUser)
                        .available(true)
                        .build());

        itemRepository.save(
                Item.builder()
                        .name("коФеВаРка")
                        .description("КофеВАРка")
                        .owner(savedUser)
                        .available(true)
                        .build());
        List<Item> items = itemRepository.findAllByText("кофеварка", 0, 1);
        assertEquals(1, items.size());
        assertNotNull(items.get(0).getId());
        assertEquals("коФеВаРка", items.get(0).getName());
        assertEquals("КофеВАРка", items.get(0).getDescription());
    }

    @Test
    void findAllByOwnerFromAndLimit() {
        User user = new User(1, "Mark", "kostrykinmark@gmail.com");
        User savedUser = userRepository.save(user);
        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
        itemRepository.save(
                Item.builder()
                        .name("Пылесос")
                        .description("Пылесос")
                        .owner(savedUser)
                        .available(true)
                        .build());

        itemRepository.save(
                Item.builder()
                        .name("коФеВаРка")
                        .description("КофеВАРка")
                        .owner(savedUser)
                        .available(true)
                        .build());
        List<Item> items = itemRepository.findAllByOwnerFromAndLimit(savedUser.getId(), 0, 2);
        assertEquals(2, items.size());
        assertEquals(savedUser.getId(), (int) items.get(0).getOwner().getId());
        assertEquals("Пылесос", items.get(0).getName());
    }

}
