package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findAllUsersItemRequest() {
        User firstUser = User.builder().id(1).name("Mark").email("kostrykinmark@gmail.com").build();
        User secondUser = User.builder().id(2).name("John").email("johndoe@gmail.com").build();
        userRepository.save(firstUser);
        assertTrue(userRepository.findById(1).isPresent());
        userRepository.save(secondUser);
        assertTrue(userRepository.findById(2).isPresent());
        itemRequestRepository
                .save(ItemRequest.builder().description("Нужна кофеварка").requestor(firstUser).created(LocalDateTime.now().minusHours(2)).build());
        assertTrue(itemRequestRepository.findById(1).isPresent());
        itemRequestRepository
                .save(ItemRequest.builder().description("Нужен пылесос").requestor(firstUser).created(LocalDateTime.now()).build());
        assertTrue(itemRequestRepository.findById(2).isPresent());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllUsersItemRequest(2, 0, 3);
        assertEquals(2, itemRequests.size());
        assertEquals("Нужен пылесос", itemRequests.get(0).getDescription());
        assertEquals("Нужна кофеварка", itemRequests.get(1).getDescription());
    }

}
