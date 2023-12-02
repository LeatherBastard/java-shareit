package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void clearRepositories() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllUsersItemRequest() {
        User firstUser = userRepository.save(User.builder().id(1).name("Mark").email("kostrykinmark@gmail.com").build());
        assertTrue(userRepository.findById(firstUser.getId()).isPresent());
        User secondUser = userRepository.save(User.builder().id(2).name("John").email("johndoe@gmail.com").build());
        assertTrue(userRepository.findById(secondUser.getId()).isPresent());

        ItemRequest firstItemRequest = itemRequestRepository
                .save(ItemRequest.builder().description("Нужна кофеварка").requestor(firstUser).created(LocalDateTime.now().minusHours(2)).build());
        assertTrue(itemRequestRepository.findById(firstItemRequest.getId()).isPresent());
        ItemRequest secondItemRequest = itemRequestRepository
                .save(ItemRequest.builder().description("Нужен пылесос").requestor(firstUser).created(LocalDateTime.now()).build());
        assertTrue(itemRequestRepository.findById(secondItemRequest.getId()).isPresent());

        List<ItemRequest> itemRequests = itemRequestRepository.findAllUsersItemRequest(secondUser.getId(), 0, 3);
        assertEquals(2, itemRequests.size());
        assertEquals(secondItemRequest, itemRequests.get(0));
        assertEquals(firstItemRequest, itemRequests.get(1));
    }

}
