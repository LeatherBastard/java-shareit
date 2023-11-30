package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByText() {
        assertFalse(userRepository.findById(1).isPresent());
        User user = new User(1, "Mark", "kostrykinmark@gmail.com");
        userRepository.save(user);
        assertTrue(userRepository.findById(1).isPresent());
        itemRepository.save(
                Item.builder()
                        .name("Пылесос")
                        .description("Пылесос")
                        .owner(user)
                        .available(true)
                        .build());

        itemRepository.save(
                Item.builder()
                        .name("коФеВаРка")
                        .description("КофеВАРка")
                        .owner(user)
                        .available(true)
                        .build());
        List<Item> items = itemRepository.findAllByText("кофеварка", 0, 1);
        assertEquals(1, items.size());
        assertEquals(items.get(0).getId(), 2);
    }

    @Test
    void findAllByOwnerFromAndLimit() {
        assertFalse(userRepository.findById(1).isPresent());
        User user = new User(1, "Mark", "kostrykinmark@gmail.com");
        userRepository.save(user);
        assertTrue(userRepository.findById(1).isPresent());
        itemRepository.save(
                Item.builder()
                        .name("Пылесос")
                        .description("Пылесос")
                        .owner(user)
                        .available(true)
                        .build());

        itemRepository.save(
                Item.builder()
                        .name("коФеВаРка")
                        .description("КофеВАРка")
                        .owner(user)
                        .available(true)
                        .build());
        List<Item> items = itemRepository.findAllByOwnerFromAndLimit(1, 0, 2);
        assertEquals(2, items.size());
        assertEquals(1, (int) items.get(0).getOwner().getId());
        assertEquals("Пылесос", items.get(0).getName());
    }

}
